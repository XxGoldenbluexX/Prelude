package fr.nekotine.prelude;

import org.bukkit.entity.Player;

import me.libraryaddict.disguise.DisguiseAPI;

public class PlayerWrapper {
	private final Player player;
	private int tier;
	private int team;
	private EffigyList effigy;
	
	public PlayerWrapper(Player player, int tier, int team) {
		this.player=player;
		this.setTier(tier);
		this.setTeam(team);
	}

	public Player getPlayer() {
		return player;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public EffigyList getEffigy() {
		return effigy;
	}

	public void setEffigy(EffigyList effigy) {
		this.effigy = effigy;
		DisguiseAPI.undisguiseToAll(player);
		if (effigy!=null) DisguiseAPI.disguiseToAll(player, effigy.getDisguise());
	}
}
