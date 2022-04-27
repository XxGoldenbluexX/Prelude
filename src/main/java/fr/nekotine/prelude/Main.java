package fr.nekotine.prelude;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import fr.nekotine.prelude.inventories.MapInventory;
import fr.nekotine.prelude.map.PreludeMap;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Serializer;
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
	
	private static final Material JOIN_GAME_MATERIAL = Material.SANDSTONE;
	private static final Material LEAVE_GAME_MATERIAL = Material.OBSIDIAN;
	
	
	private HashMap<Player, PlayerWrapper> players = new HashMap<Player, PlayerWrapper>();
	private String mapName;
	private MapInventory mapInventory;
	private boolean running = false;
	
	private PreludeMap map;
	
	@Override
	public void onEnable() {
		super.onEnable();
		main=this;
		
		EventRegisterer.registerEvent(this);
		
		Serializer.register();
		
		Commands.make().register();
		CommandAPI.onEnable(this);
		
		if (getDataFolder().exists()) {//making dataFolder
			getDataFolder().mkdir();
		}
		File mapf = new File(getDataFolder(),"Maps");//making map Folder
		if (!mapf.exists()){
				mapf.mkdir();
		}
		PreludeMap.setMapFolder(mapf);
		
		setMapName(PreludeMap.getMapNameList().get(0));
		
		mapInventory = new MapInventory();
		
		
	}
	@Override
	public void onLoad() {
		super.onLoad();
		CommandAPI.onLoad(new CommandAPIConfig());
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
		String before = this.mapName;
		this.mapName=mapName;
		EventRegisterer.callMapChangeEvent(before, mapName);
	}
	public String getMapName() {
		return mapName;
	}
	public PlayerWrapper getWrapper(Player player) {
		return players.get(player);
	}
	public boolean setTeam(Player player, Team team) {
		if(players.containsKey(player)) {
			PlayerWrapper wrapper = getWrapper(player);
			Team before = wrapper.getTeam();
			getWrapper(player).setTeam(team);
			EventRegisterer.callPlayerChangeTeamEvent(player, before, team);
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
				EventRegisterer.callPlayerChangeTeamEvent(player, Team.RED, Team.BLUE);
				return Team.BLUE;
			case BLUE:
				setTeam(player, Team.RED);
				EventRegisterer.callPlayerChangeTeamEvent(player, Team.BLUE, Team.RED);
				return Team.RED;
			}
		}
		return null;
	}
	public void openMapInventory(Player player) {
		mapInventory.open(player);
	}
	public void teleportPlayersToSpawn() {
		for(PlayerWrapper wrapper : players.values()) {
			map.teleportPlayer(wrapper.getTeam(), wrapper.getPlayer());
		}
	}
	public boolean start() {
		if(!running) {
			map = PreludeMap.load(mapName);
			map.enable();
			teleportPlayersToSpawn();
			return true;
		}else {
			return false;
		}
	}
	public boolean end() {
		if(running) {
			map.unload();
			return true;
		}else {
			return false;
		}
	}
	public boolean isPlaying(Player player) {
		return players.containsKey(player);
	}
	public ArrayList<Player> getPlayersInTeam(Team team) {
		ArrayList<Player> inTeam = new ArrayList<>();
		for(PlayerWrapper wrapper : players.values()) {
			if(wrapper.getTeam()==team) inTeam.add(wrapper.getPlayer());
		}
		return inTeam;
	}
	public int getNumberOfPlayerInTeam(Team team) {
		return getPlayersInTeam(team).size();
	}
	public boolean addPlayerInBestTeam(Player player) {
		if(!players.containsKey(player)) {
			
			int playersInBlue = getNumberOfPlayerInTeam(Team.BLUE);
			int playersInRed = getNumberOfPlayerInTeam(Team.RED);
			if(playersInBlue>playersInRed) {
				addPlayer(player, Team.RED);
			}else {
				addPlayer(player, Team.BLUE);
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getItem()!=null && e.getItem().getType()==JOIN_GAME_MATERIAL && e.getAction()!=Action.PHYSICAL) {
			addPlayerInBestTeam(e.getPlayer());
			e.getPlayer().sendMessage("joined");
		}
		if(e.getItem()!=null && e.getItem().getType()==LEAVE_GAME_MATERIAL && e.getAction()!=Action.PHYSICAL) {
			removePlayer(e.getPlayer());
			e.getPlayer().sendMessage("left");
		}
	}
}
