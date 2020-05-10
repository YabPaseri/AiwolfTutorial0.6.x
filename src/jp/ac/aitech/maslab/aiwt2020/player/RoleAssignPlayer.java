package jp.ac.aitech.maslab.aiwt2020.player;

import org.aiwolf.sample.lib.AbstractRoleAssignPlayer;

public class RoleAssignPlayer extends AbstractRoleAssignPlayer {

	public RoleAssignPlayer() {
		setVillagerPlayer(new VillagerPlayer());
	}

	@Override
	public String getName() {
		return "Tutorial";
	}

}
