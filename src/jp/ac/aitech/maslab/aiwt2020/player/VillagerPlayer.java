package jp.ac.aitech.maslab.aiwt2020.player;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.VoteContentBuilder;

public class VillagerPlayer extends BasePlayer {

	@Override
	public String talk() {
		// 投票対象が決まっていなければ，決めて発言する
		if (declaredVoteCandidate == null) {
			// 投票対象をランダムに生存者の中から決める
			voteCandidate = randomSelect(aliveOthers);
			// キューに発言を追加する
			talkQueue.add(new Content(new VoteContentBuilder(me, voteCandidate)));
			// 宣言済み投票対象を書き換える
			declaredVoteCandidate = voteCandidate;
		}
		return super.talk();
	}

}
