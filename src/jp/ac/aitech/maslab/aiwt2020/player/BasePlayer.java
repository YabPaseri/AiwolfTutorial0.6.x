package jp.ac.aitech.maslab.aiwt2020.player;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

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
	Deque<Content> talkQueue = new LinkedList<Content>();

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
		int i = 0;
		if (!comingoutMap.isEmpty()) {
			for (Role r : comingoutMap.values()) {
				if (r == role)
					i++;
			}
		}
		return i;
	}

	/** リストからランダムに選んで返す */
	<T> T randomSelect(List<T> list) {
		if (list.isEmpty()) {
			return null;
		} else {
			Random random = new Random();
			return list.get(random.nextInt(list.size()));
		}
	}

	/** 追放されたエージェントを生存者リストから削除し，追放リストに追加する */
	private void addExecutedAgent(Agent agent) {
		if (agent != null) {
			aliveOthers.remove(agent);
			if (!executedAgents.contains(agent)) {
				executedAgents.add(agent);
			}
		}
	}

	/** 襲撃されたエージェントを生存者リストから削除し，襲撃リストに追加する */
	private void addKilledAgent(Agent agent) {
		if (agent != null) {
			aliveOthers.remove(agent);
			if (!killedAgents.contains(agent)) {
				killedAgents.add(agent);
			}
		}
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

	@Override
	public Agent attack() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void dayStart() {
		// 発言用キューの初期化(前日に発言できずに残ってしまった内容を間違って話さないように)
		talkQueue.clear();
		// talkListはその日に話された内容しか入っていないので，ヘッドを0に戻す
		talkListHead = 0;
		// 誰に投票するかの変数もnullで初期化
		voteCandidate = null;
		// 宣言済みのほうもnullで初期化
		declaredVoteCandidate = null;
		// 昨日追放されたエージェントをリストに追加
		addExecutedAgent(currentGameInfo.getExecutedAgent());
		// 昨日襲撃されたエージェントをリストに追加
		if (!currentGameInfo.getLastDeadAgentList().isEmpty()) {
			addKilledAgent(currentGameInfo.getLastDeadAgentList().get(0));
		}
	}

	@Override
	public Agent divine() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Agent guard() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void initialize(GameInfo arg0, GameSetting arg1) {
		// 手元のゲーム情報を最新のもので初期化
		currentGameInfo = arg0;
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
		// カミングアウトマップはclearで初期化
		comingoutMap.clear();
	}

	@Override
	public String talk() {
		// 発話する内容がない時は，SKIPを返す
		if (talkQueue.isEmpty()) {
			return Talk.SKIP;
		}
		// キューの先頭を取ってくる
		Content content = talkQueue.poll();
		// 主語が自分なら，省略する．
		if (content.getSubject() == me) {
			return Content.stripSubject(content.getText());
		}
		return content.getText();
	}

	@Override
	public void update(GameInfo gameInfo) {
		// 手元のゲーム情報を最新のもので更新
		currentGameInfo = gameInfo;
		// 1日の最初の呼び出しは，dayStart()メソッドの前なので，何もしない
		if (currentGameInfo.getDay() == day + 1) {
			day = currentGameInfo.getDay();
			return;
		}

		// 2回目以降の呼び出し
		// (夜限定) 追放されたエージェントを登録
		addExecutedAgent(currentGameInfo.getLatestExecutedAgent());

		// talkListから，カミングアウト・占い報告・霊媒報告を抽出
		for (int i = talkListHead; i < currentGameInfo.getTalkList().size(); i++) {

			// リストからtalkを取得
			Talk talk = currentGameInfo.getTalkList().get(i);
			// 発話者を取得
			Agent talker = talk.getAgent();
			// 発話者が自分なら処理しない
			if (talker == me) {
				continue;
			}
			// 扱いやすいようにContent型にする
			Content content = new Content(talk.getText());
			// subject(主語)がUNSPEC(未特定)の場合は，発話者に入れ替える
			if (content.getSubject() == Content.UNSPEC) {
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

		switch (content.getTopic()) {
		// カミングアウト
		case COMINGOUT:
			comingoutMap.put(content.getTarget(), content.getRole());
			return;
		// 占い報告
		case DIVINED:
			divinationList.add(new Judge(
					day,
					content.getSubject(),
					content.getTarget(),
					content.getResult()));
			return;
		// 霊媒報告
		case IDENTIFIED:
			identList.add(new Judge(
					day,
					content.getSubject(),
					content.getTarget(),
					content.getResult()));
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
		switch (content.getOperator()) {
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
			for (Content c : content.getContentList()) {
				parseSentence(c);
			}
			break;
		// それ以外はここでは無視
		default:
			break;
		}
	}

	@Override
	public Agent vote() {
		if (voteCandidate == null) {
			return randomSelect(aliveOthers);
		}
		return voteCandidate;
	}

	@Override
	public String whisper() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
