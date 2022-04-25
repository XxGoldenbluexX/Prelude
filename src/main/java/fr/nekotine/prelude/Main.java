package fr.nekotine.prelude;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nekotine.prelude.inventories.MapInventory;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Team;

public class Main extends JavaPlugin implements Listener{
	
	private static Main main;
	public static Main getInstance() {
		return main;
	}
	
	private static final String questionMarkHeadUrl = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViOTVkYTEyODE2NDJkYWE1ZDAyMmFkYmQzZTdjYjY5ZGMwOTQyYzgxY2Q2M2JlOWMzODU3ZDIyMmUxYzhkOSJ9fX0=";
	public static String getQuestionMarkHeadUrl() {
		return questionMarkHeadUrl;
	}
	
	private HashMap<Player, PlayerWrapper> players = new HashMap<Player, PlayerWrapper>();
	private String mapName;
	private final MapInventory mapInventory = new MapInventory();
	private boolean running = false;
	
	@Override
	public void onEnable() {
		super.onEnable();
		main=this;
		EventRegisterer.registerEvent(this);
	}
	@Override
	public void onDisable() {
		EventRegisterer.unregisterEvent(this);
	}
	
	public boolean addPlayer(Player player, Team team) {
		if(!players.containsKey(player)) {
			players.put(player, new PlayerWrapper(player, team));
			return true;
		}
		return false;
	}
	public boolean removePlayer(Player player) {
		if(players.containsKey(player)) {
			players.get(player).destroy();
			players.remove(player);
			return true;
		}
		return false;
	}
	public void setMapName(String mapName) {
		this.mapName=mapName;
	}
	public String getMapName() {
		return mapName;
	}
	public PlayerWrapper getWrapper(Player player) {
		return players.get(player);
	}
	public boolean setTeam(Player player, Team team) {
		if(players.containsKey(player)) {
			getWrapper(player).setTeam(team);
			return true;
		}
		return false;
	}
	public Team swapTeam(Player player) {
		if(players.containsKey(player)) {
			PlayerWrapper wrapper = getWrapper(player);
			switch(wrapper.getTeam()) {
			case RED:
				setTeam(player, Team.BLUE);
				return Team.BLUE;
			case BLUE:
				setTeam(player, Team.RED);
				return Team.RED;
			}
		}
		return null;
	}
	public void openMapInventory(Player player) {
		mapInventory.open(player);
	}
	public boolean start() {
		if(!running) {
			return true;
		}else {
			return false;
		}
	}
}
