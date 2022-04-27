package fr.nekotine.prelude;

import org.bukkit.entity.Player;

import fr.nekotine.prelude.inventories.MenuInventory;
import fr.nekotine.prelude.inventories.ShopInventory;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Team;

public class PlayerWrapper {
	private final Player player;
	private final MenuInventory menuInventory;
	private final ShopInventory shopInventory;
	
	private Team team;
	private Effigy effigy;
	private int money = 0;
	private boolean alive = true;
	
	
	public PlayerWrapper(Player player, Team team) {
		this.player=player;
		this.setTeam(team);
		
		this.menuInventory = new MenuInventory(this);
		this.shopInventory = new ShopInventory(this);
	}
	
	public void tick() {
		if(effigy != null) effigy.tick();
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
		shopInventory.destroy();
		if(effigy != null) {
			effigy.destroy();
		}
	}

	public void openMenuInventory() {
		menuInventory.open(player);
	}
	
	public void closeMenuInventory() {
		menuInventory.closeInventory(player);
	}
	
	public void openShopInventory() {
		shopInventory.open(player);
	}
	
	public void closeShopInventory() {
		shopInventory.closeInventory(player);
	}


	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		int before = this.money;
		this.money = money;
		
		EventRegisterer.callPlayerChangeMoneyEvent(player, before, money);
	}
	
	public void addMoney(int toAdd) {
		setMoney( money + toAdd);
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
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
