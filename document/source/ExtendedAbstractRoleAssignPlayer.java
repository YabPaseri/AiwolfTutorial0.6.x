/**
 * 既存のAbstractRoleAssignPlayerにはない
 * 5人人狼と15人人狼(正確には5人人狼以外全て)で使用するエージェントを分ける
 * 機能をつけたAbstractRoleAssignPlayer
 *
 * @author Hiroki Oyabu
 */

package jp.ac.aitech.maslab.aiwolf_tutorial.util;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.sample.player.SampleBodyguard;
import org.aiwolf.sample.player.SampleMedium;
import org.aiwolf.sample.player.SamplePossessed;
import org.aiwolf.sample.player.SampleSeer;
import org.aiwolf.sample.player.SampleVillager;
import org.aiwolf.sample.player.SampleWerewolf;

public abstract class ExtendedAbstractRoleAssignPlayer implements Player {

	private Player villagerPlayer5 = new SampleVillager();
	private Player villagerPlayer15 = new SampleVillager();
	private Player seerPlayer5 = new SampleSeer();
	private Player seerPlayer15 = new SampleSeer();
	private Player mediumPlayer5 = new SampleMedium();
	private Player mediumPlayer15 = new SampleMedium();
	private Player bodyguardPlayer5 = new SampleBodyguard();
	private Player bodyguardPlayer15 = new SampleBodyguard();
	private Player possessedPlayer5 = new SamplePossessed();
	private Player possessedPlayer15 = new SamplePossessed();
	private Player werewolfPlayer5 = new SampleWerewolf();
	private Player werewolfPlayer15 = new SampleWerewolf();

	private Player rolePlayer;

	public final Player getVillagerPlayer5() {
		return villagerPlayer5;
	}

	public final void setVillagerPlayer5(Player villagerPlayer5) {
		this.villagerPlayer5 = villagerPlayer5;
	}

	public final Player getVillagerPlayer15() {
		return villagerPlayer15;
	}

	public final void setVillagerPlayer15(Player villagerPlayer15) {
		this.villagerPlayer15 = villagerPlayer15;
	}

	public final Player getSeerPlayer5() {
		return seerPlayer5;
	}

	public final void setSeerPlayer5(Player seerPlayer5) {
		this.seerPlayer5 = seerPlayer5;
	}

	public final Player getSeerPlayer15() {
		return seerPlayer15;
	}

	public final void setSeerPlayer15(Player seerPlayer15) {
		this.seerPlayer15 = seerPlayer15;
	}

	public final Player getMediumPlayer5() {
		return mediumPlayer5;
	}

	public final void setMediumPlayer5(Player mediumPlayer5) {
		this.mediumPlayer5 = mediumPlayer5;
	}

	public final Player getMediumPlayer15() {
		return mediumPlayer15;
	}

	public final void setMediumPlayer15(Player mediumPlayer15) {
		this.mediumPlayer15 = mediumPlayer15;
	}

	public final Player getBodyguardPlayer5() {
		return bodyguardPlayer5;
	}

	public final void setBodyguardPlayer5(Player bodyGuardPlayer5) {
		this.bodyguardPlayer5 = bodyGuardPlayer5;
	}

	public final Player getBodyguardPlayer15() {
		return bodyguardPlayer15;
	}

	public final void setBodyguardPlayer15(Player bodyGuardPlayer15) {
		this.bodyguardPlayer5 = bodyGuardPlayer15;
	}

	public final Player getPossessedPlayer5() {
		return possessedPlayer5;
	}

	public final void setPossessedPlayer5(Player possesedPlayer5) {
		this.possessedPlayer5 = possesedPlayer5;
	}

	public final Player getPossessedPlayer15() {
		return possessedPlayer15;
	}

	public final void setPossessedPlayer15(Player possesedPlayer15) {
		this.possessedPlayer15 = possesedPlayer15;
	}

	public final Player getWerewolfPlayer5() {
		return werewolfPlayer5;
	}

	public final void setWerewolfPlayer5(Player werewolfPlayer5) {
		this.werewolfPlayer5 = werewolfPlayer5;
	}

	public final Player getWerewolfPlayer15() {
		return werewolfPlayer15;
	}

	public final void setWerewolfPlayer15(Player werewolfPlayer15) {
		this.werewolfPlayer15 = werewolfPlayer15;
	}

	@Override
	public abstract String getName();

	@Override
	public void update(GameInfo gameInfo) {
		rolePlayer.update(gameInfo);
	}

	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
		Role myRole = gameInfo.getRole();
		int playerNum = gameSetting.getPlayerNum();
		switch(myRole) {
		case VILLAGER:
			rolePlayer = (playerNum == 5) ? villagerPlayer5 : villagerPlayer15;
			break;
		case SEER:
			rolePlayer = (playerNum == 5) ? seerPlayer5 : seerPlayer15;
			break;
		case MEDIUM:
			rolePlayer = (playerNum == 5) ? mediumPlayer5 : mediumPlayer15;
			break;
		case BODYGUARD:
			rolePlayer = (playerNum == 5) ? bodyguardPlayer5 : bodyguardPlayer15;
			break;
		case POSSESSED:
			rolePlayer = (playerNum == 5) ? possessedPlayer5 : possessedPlayer15;
			break;
		case WEREWOLF:
			rolePlayer = (playerNum == 5) ? werewolfPlayer5 : werewolfPlayer15;
			break;
		default:
			rolePlayer = (playerNum == 5) ? villagerPlayer5 : villagerPlayer15;
			break;
		}
		rolePlayer.initialize(gameInfo, gameSetting);
	}

	@Override
	public void dayStart() {
		rolePlayer.dayStart();
	}

	@Override
	public String talk() {
		return rolePlayer.talk();
	}

	@Override
	public String whisper() {
		return rolePlayer.whisper();
	}

	@Override
	public Agent vote() {
		return rolePlayer.vote();
	}

	@Override
	public Agent attack() {
		return rolePlayer.attack();
	}

	@Override
	public Agent divine() {
		return rolePlayer.divine();
	}

	@Override
	public Agent guard() {
		return rolePlayer.guard();
	}

	@Override
	public void finish() {
		rolePlayer.finish();
	}
}
