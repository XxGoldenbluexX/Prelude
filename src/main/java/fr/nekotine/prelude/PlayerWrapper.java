package fr.nekotine.prelude;

import org.bukkit.entity.Player;

public class PlayerWrapper {
	protected final Player player;
	protected int tier;
	protected int team;
	protected EffigyList effigy;
	
	public PlayerWrapper(Player player, int tier, int team) {
		this.player=player;
		this.tier=tier;
		this.team=team;
	}
}
