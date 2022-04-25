package fr.nekotine.prelude;

import org.bukkit.entity.Player;

import fr.nekotine.prelude.inventories.MenuInventory;
import fr.nekotine.prelude.utils.Team;

public class PlayerWrapper {
	private final Player player;
	private Team team;
	private Effigy effigy;
	
	private final MenuInventory menuInventory;
	
	public PlayerWrapper(Player player, Team team) {
		this.player=player;
		this.setTeam(team);
		this.menuInventory = new MenuInventory(player);
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public void setEffigy(EffigyList effigytype) {
		destroy();
		effigy = EffigyList.buildEffigy(this, effigytype);
	}
	
	public Effigy getEffigy() {
		return effigy;
	}
	
	public void destroy() {
		if(effigy != null) effigy.destroy();
	}

	public void openMenuInventory() {
		menuInventory.open(player);
	}
}
