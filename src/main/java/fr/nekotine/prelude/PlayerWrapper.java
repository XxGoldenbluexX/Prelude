package fr.nekotine.prelude;

import org.bukkit.entity.Player;

import fr.nekotine.prelude.inventories.MenuInventory;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Team;

public class PlayerWrapper {
	private final Player player;
	private final MenuInventory menuInventory;
	
	private Team team;
	private Effigy effigy;
	private int money = 0;
	
	
	public PlayerWrapper(Player player, Team team) {
		this.player=player;
		this.setTeam(team);
		this.menuInventory = new MenuInventory(this);
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
		EffigyList before = null;
		if(effigy!=null) before=effigy.getEffigyType();
		
		destroy();
		effigy = EffigyList.buildEffigy(this, effigytype);
		
		EventRegisterer.callPlayerChangeEffigyEvent(player, before, effigytype);
	}
	
	public Effigy getEffigy() {
		return effigy;
	}
	
	public void destroy() {
		menuInventory.destroy();
		if(effigy != null) {
			effigy.destroy();
		}
	}

	public void openMenuInventory() {
		menuInventory.open(player);
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	public boolean buyEffigy(EffigyList effigy) {
		int cost = effigy.getCost();
		if(getMoney()>=cost) {
			setMoney(getMoney() - cost);
			setEffigy(effigy);
			return true;
		}else {
			return false;
		}
	}
}
