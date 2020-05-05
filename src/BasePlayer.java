public class BasePlayer implements Player {

	/** このエージェント */
	Agent me;

	/** 日付 */
	int day;

	/** 最新のゲーム情報 */
	GameInfo currentGameInfo;

	/** 自分以外の生存エージェントリスト */
	List<Agent> aliveOthers;

	/** 追放されたエージェントリスト */
	List<Agent> executedAgents = new ArrayList<Agent>();

	/** 襲撃されたエージェントリスト */
	List<Agent> killedAgents = new ArrayList<Agent>();

	/** 発言された占い結果報告リスト */
	List<Judge> divinationList = new ArrayList<Judge>();

	/** 発言された霊媒結果報告リスト */
	List<Judge> identList = new ArrayList<Judge>();

	/** 発言用Talkキュー */
	TalkQueue talkQueue;

	/** カミングアウト状況 */
	Map<Agent, Role> comingoutMap = new HashMap<Agent, Role>();

	/** talkListの読み込みヘッド */
	int talkListHead;

	/** その日投票するターゲット(候補) */
	Agent voteCandidate;

	/** その日投票するターゲット(宣言済) */
	Agent declaredVoteCandidate;

	/** エージェントが生存しているかどうかを返す */
	boolean isAlive(Agent agent) {
		return currentGameInfo.getStatusMap().get(agent) == Status.ALIVE;
	}

	/** エージェントが襲撃されたかどうかを返す */
	boolean isKilled(Agent agent) {
		return killedAgents.contains(agent);
	}

	/** エージェントがカミングアウトしているかどうかを返す */
	boolean isCo(Agent agent) {
		return comingoutMap.containsKey(agent);
	}

	/** 役職がカミングアウトされたかどうかを返す */
	boolean isCo(Role role) {
		return comingoutMap.containsValue(role);
	}

	/** その役職をカミングアウトした人数を返す */
	int countCoRole(Role role) {
		int i=0;
		if(!comingoutMap.isEmpty()) {
			for(Role r : comingoutMap.values()) {
				if (r == role) i++;
			}
		}
		return i;
	}

	/** リストからランダムに選んで返す */
	<T> T randomSelect(List<T> list) {
		if(list.isEmpty()) {
			return null;
		} else {
			return list.get(randomInt(0, list.size()));
		}
	}

	/** 追放されたエージェントを生存者リストから削除し，追放リストに追加する */
	private void addExecutedAgent(Agent agent) {
		if(agent != null) {
			aliveOthers.remove(agent);
			if(!executedAgents.contains(agent)) {
				executedAgents.add(agent);
			}
		}
	}

	/** 襲撃されたエージェントを生存者リストから削除し，襲撃リストに追加する */
	private void addKilledAgent(Agent agent) {
		if(agent != null) {
			aliveOthers.remove(agent);
			if(!killedAgents.contains(agent)) {
				killedAgents.add(agent);
			}
		}
	}

	/** arg0以上，arg1未満のランダムな整数を1つ返す */
	int randomInt(int arg0, int arg1) {
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			if (arg0 < arg1) { return secureRandom.nextInt(arg1 - arg0) + arg0; }
			else { throw new IllegalArgumentException(); }
		} catch (Exception e) {
			Random random = new Random();
			return random.nextInt(arg1 - arg0) + arg0;
		}
	}

	/**
	 * initialize
	 * 初期化メソッド，ゲーム開始のタイミングで呼ばれる
	 * ゲームごとに初期化したい処理などを書く
	 * 例：生存者リストの初期化，追放・襲撃リストのクリア
	 */
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		// 手元のゲーム情報を最新のもので初期化
		currentGameInfo = gameInfo;
		// 人狼知能プラットフォームには0日目が存在するので，初期化は-1
		day = -1;
		// 自分の情報をgameInfoの情報で初期化
		me = currentGameInfo.getAgent();
		// 生存者リストをgameInfoから複製し，自分をリストから削除
		aliveOthers = new ArrayList<>(currentGameInfo.getAliveAgentList());
		aliveOthers.remove(me);
		// 追放リストはclearで初期化
		executedAgents.clear();
		// 襲撃リストはclearで初期化
		killedAgents.clear();
		// 占いリストはclearで初期化
		divinationList.clear();
		// 霊媒リストはclearで初期化
		identList.clear();
		// トークキューはインスタンスの作成に自分の情報が必要なので，インスタンスの(再)作成で初期化
		talkQueue = new TalkQueue(me);
		// isECIS=true -> 連続する会話を除外する設定をONにする
		talkQueue.isECIS(true);
		// カミングアウトマップはclearで初期化
		comingoutMap.clear();
	}

	/**
	 * update
	 * 最新の情報を受け取るメソッド，(たしか)initializeを除く他のメソッドが呼ばれる前に呼ばれる
	 * 手元の情報を更新する処理を書く
	 * 例：他のエージェントの発言の整理，追放・襲撃リストの更新
	 */
	@Override
	public void update(GameInfo gameInfo) {
		// 手元のゲーム情報を最新のもので更新
		currentGameInfo = gameInfo;
		// 1日の最初の呼び出しは，dayStart()メソッドの前なので，何もしない
		if(currentGameInfo.getDay() == day + 1) {
			day = currentGameInfo.getDay();
			return;
		}
		// 2回目以降の呼び出し
		// (夜限定) 追放されたエージェントを登録
		addExecutedAgent(currentGameInfo.getLatestExecutedAgent());
		// talkListから，カミングアウト・占い報告・霊媒報告を抽出
		for(int i=talkListHead; i<currentGameInfo.getTalkList().size(); i++) {
			Talk talk = currentGameInfo.getTalkList().get(i);
			Agent talker = talk.getAgent();
			if(talker == me) {
				continue;
			}
			Content content = new Content(talk.getText());
			// subject(主語)がUNSPEC(未特定)の場合は，発話者に入れ替える
			if(content.getSubject() == Content.UNSPEC) {
				content = replaceSubject(content, talker);
			}
			// 演算子がある場合は再帰的に処理する必要があるため，別メソッドへ
			parseSentence(content);
		}
		// ヘッドを上書きする
		talkListHead = currentGameInfo.getTalkList().size();
	}

	// 再帰的に文を解析するメソッド
	void parseSentence(Content content) {
		switch(content.getTopic()) {
		// カミングアウト
		case COMINGOUT:
			comingoutMap.put(content.getTarget(), content.getRole());
			return;
		// 占い報告
		case DIVINED:
			divinationList.add(new Judge(day, content.getSubject(), content.getTarget(), content.getResult()));
			return;
		// 霊媒報告
		case IDENTIFIED:
			identList.add(new Judge(day, content.getSubject(), content.getTarget(), content.getResult()));
			return;
		// 演算子(OR,AND,NOT...)
		case OPERATOR:
			parseOperator(content);
			return;
		// それ以外はここでは無視
		default:
			break;
		}
	}

	// 演算子の分析をするメソッド
	void parseOperator(Content content) {
		switch(content.getOperator()) {
		case BECAUSE:
			// [0]に理由，[1]に結論が入っているので，結論だけ処理
			parseSentence(content.getContentList().get(1));
			break;
		case DAY:
			// 特定の日付について言及しているが，内容だけ処理
			parseSentence(content.getContentList().get(0));
			break;
		// AND(全て真)，OR(1つは真)，XOR(どちらかを主張≒どちらかが真)
		case AND:
		case OR:
		case XOR:
			for(Content c : content.getContentList()) {
				parseSentence(c);
			}
			break;
		// それ以外はここでは無視
		default:
			break;
		}
	}

	/**
	 * dayStart
	 * 各日の最初にだけ呼ばれるメソッド．
	 * 日ごとに初期化したい処理や，日ごとにしか更新されない情報の処理を書く
	 * 例：talkListHeadの初期化，昨夜追放・襲撃されたエージェントに関する処理
	 */
	@Override
	public void dayStart() {
		talkQueue.clear();
		talkListHead = 0;
		voteCandidate = null;
		addExecutedAgent(currentGameInfo.getExecutedAgent());
		if(!currentGameInfo.getLastDeadAgentList().isEmpty()) {
			addKilledAgent(currentGameInfo.getLastDeadAgentList().get(0));
		}
	}

	/**
	 * talk
	 * 原則毎ターン呼ばれるメソッド
	 * このターン発言したい内容を返す
	 */
	@Override
	public String talk() {
		return talkQueue.talk();
	}

	/**
	 * whisper
	 * 人狼専用．人狼が複数人いる場合に呼ばれるメソッド．人狼にしか届かない会話を話す．
	 * このターン，人狼とのみ共有したい内容を返す
	 */
	@Override
	public String whisper() {
		return null;
	}

	/**
	 * vote
	 * 投票のタイミングで呼ばれるメソッド．
	 * この日，投票したいエージェントを返す
	 * 投票数が同一のエージェントがいた場合は，再度呼ばれる(はず)
	 */
	@Override
	public Agent vote() {
		if(voteCandidate == null) {
			return randomSelect(aliveOthers);
		}
		return voteCandidate;
	}

	/**
	 * attack
	 * 人狼専用．襲撃のタイミングで呼ばれるメソッド
	 * この日，襲撃したいエージェントを返す．
	 * 複数人人狼がいる場合に襲撃先がばらけた場合は，再度呼ばれる(はず)
	 */
	@Override
	public Agent attack() {
		return null;
	}

	/**
	 * divine
	 * 占い師専用．占いのタイミングで呼ばれるメソッド
	 * この日，占いたいエージェントを返す
	 * 結果は翌日のgameInfoに入ってくる
	 */
	@Override
	public Agent divine() {
		return null;
	}

	/**
	 * guard
	 * 狩人専用．護衛のタイミングで呼ばれるメソッド
	 * この日，護衛したいエージェントを返す
	 */
	@Override
	public Agent guard() {
		return null;
	}

	/**
	 * finish
	 * ゲーム終了のタイミングで呼ばれるメソッド．
	 * ゲーム単位で行いたい処理を書く，
	 * 例：勝率計算(どのエージェントが強いのか)
	 */
	@Override
	public void finish() {

	}

	/**
	 * getName
	 * エージェントの名前を返すメソッド．
	 * AbstractRoleAssignPlayerを継承したクラスを使用してエージェントを作成している場合，
	 * そのクラスがgetNameを代わりに返してくれるので，ここにわざわざ書く必要はない
	 */
	@Override
	public String getName() {
		return null;
	}

	/**
	 * 主語を省略されたtalkを扱えるようにするため，主語を発話者に置き換えるメソッド
	 * @param content
	 * @param newSubject
	 */
	static Content replaceSubject(Content content, Agent newSubject) {
		if (content.getTopic() == Topic.SKIP || content.getTopic() == Topic.OVER) {
			return content;
		}
		if (newSubject == Content.UNSPEC) {
			return new Content(Content.stripSubject(content.getText()));
		} else {
			return new Content(newSubject + " " + Content.stripSubject(content.getText()));
		}
	}
}
