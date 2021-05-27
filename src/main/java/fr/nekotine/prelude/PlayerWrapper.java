package fr.nekotine.prelude;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.libraryaddict.disguise.DisguiseAPI;

public class PlayerWrapper {
	private final Player player;
	private int tier;
	private int team;
	private EffigyList effigytype;
	private Effigy effigy;
	
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

	public EffigyList getEffigyType() {
		return effigytype;
	}

	public void setEffigyType(EffigyList effigytype) {
		this.effigytype = effigytype;
		if (effigy!=null) HandlerList.unregisterAll(effigy);
		DisguiseAPI.undisguiseToAll(player);
		if (effigytype!=null) {
			DisguiseAPI.disguiseToAll(player, effigytype.getDisguise());
			effigy = EffigyList.buildEffigy(this, effigytype);
			Bukkit.getPluginManager().registerEvents(effigy, PreludeMain.main);
		}
	}
}
