package fr.nekotine.prelude;

import org.bukkit.entity.Player;

public class PlayerWrapper {
	public final Player player;
	public int tier;
	public int team;
	public Effigy effigy;
	public PlayerWrapper(Player player, int tier, int team) {
		this.player=player;
		this.tier=tier;
		this.team=team;
	}
}
