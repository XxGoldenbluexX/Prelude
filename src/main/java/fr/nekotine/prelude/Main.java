package fr.nekotine.prelude;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import fr.nekotine.prelude.inventories.MapInventory;
import fr.nekotine.prelude.map.PreludeMap;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Gameruler;
import fr.nekotine.prelude.utils.RoundState;
import fr.nekotine.prelude.utils.Serializer;
import fr.nekotine.prelude.utils.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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
	private boolean running = false;
	private PreludeMap map;
	
	private MapInventory mapInventory;
	private RoundManager roundManager;
	private GameScoreboard gameScoreboard;
	private BumperManager bumperManager;
	private BukkitTask ticker;
	
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
		
		Gameruler.enable();
		
		setMapName(PreludeMap.getMapNameList().get(0));
		
		roundManager = new RoundManager();
		mapInventory = new MapInventory();
		gameScoreboard = new GameScoreboard();
		bumperManager = new BumperManager();
		
		ticker = (new BukkitRunnable() {
				public void run() {
					tick();
				}
			}).runTaskTimer(getInstance(), 0L, 1L);
		
	}
	
	@EventHandler
	public void onArrowLand(ProjectileHitEvent evt) {
		if (evt.getEntityType() == EntityType.ARROW) {
			evt.getEntity().remove();
		}
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		CommandAPI.onLoad(new CommandAPIConfig());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (getRoundManager().getRoundState()==RoundState.MENU) {
			TextComponent message = 
					Component.text("["+ChatColor.AQUA+"Prelude"+ChatColor.WHITE+"]"+
			ChatColor.GOLD+"La partie manque de monde! ("+players.size()+" joueurs en attente)");
			message = message.hoverEvent(HoverEvent.showText(Component.text(ChatColor.GOLD+"Cliquez pour rejoindre")))
			.clickEvent(ClickEvent.runCommand("/prelude game join "));
			event.getPlayer().sendMessage(message);
		}
	}
	
	@Override
	public void onDisable() {
		Gameruler.disable();
		roundManager.endGame();
		roundManager.destroy();
		gameScoreboard.destroy();
		bumperManager.destroy();
		mapInventory.destroy();
		
		end();
		
		ticker.cancel();
		
		EventRegisterer.unregisterEvent(this);
	}
	
	public void tick() {
		if(running) {
			for(PlayerWrapper wrapper : getWrappers()) {
				wrapper.tick();
			}
			roundManager.tick();
		}
	}
	
	public boolean addPlayer(Player player, Team team) {
		if(!roundManager.isRoundPlaying() && !players.containsKey(player)) {
			System.out.println("player added");
			players.put(player, new PlayerWrapper(player, team));
			
			gameScoreboard.addPlayer(player);
			return true;
		}
		return false;
	}
	public boolean removePlayer(Player player) {
		if(players.containsKey(player)) {
			gameScoreboard.removePlayer(player);
			
			players.get(player).destroy();
			players.remove(player);
	
			return true;
		}
		return false;
	}
	public void setMapName(String mapName) {
		String before = this.mapName;
		this.mapName=mapName;
		System.out.println("map set");
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
			
			gameScoreboard.removePlayer(player);
			Team before = wrapper.getTeam();
			getWrapper(player).setTeam(team);
			gameScoreboard.addPlayer(player);
			
			System.out.println("team set");
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
	public boolean start() {
		if(!running) {
			map = PreludeMap.load(mapName);
			map.enable();

			roundManager.startGame();

			closeMenus();
			
			running = true;
			System.out.println("game started");
			return true;
		}else {
			return false;
		}
	}
	public void closeMenus() {
		for(PlayerWrapper wrapper : getWrappers()) {
			wrapper.closeMenuInventory();
		}
		mapInventory.closeInventoryForAll();
	}
	public boolean end() {
		if(running) {
			map.unload();
			running = false;

			System.out.println("game ended");
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
	public PreludeMap getMap() {
		return map;
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
	public Set<Player> getPlayers(){
		return players.keySet();
	}
	public Collection<PlayerWrapper> getWrappers(){
		return players.values();
	}
	public boolean isRunning() {
		return running;
	}
	public boolean isRoundPlaying() {
		return roundManager.isRoundPlaying();
	}
	public RoundManager getRoundManager() {
		return roundManager;
	}
	public GameScoreboard getScoreboard() {
		return gameScoreboard;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getItem()!=null && e.getItem().getType()==JOIN_GAME_MATERIAL && e.getAction()!=Action.PHYSICAL) {
			addPlayerInBestTeam(e.getPlayer());
		}
		if(e.getItem()!=null && e.getItem().getType()==LEAVE_GAME_MATERIAL && e.getAction()!=Action.PHYSICAL) {
			removePlayer(e.getPlayer());
		}
	}
	@EventHandler
	public void onDisconnext(PlayerQuitEvent e) {
		if(players.containsKey(e.getPlayer())) removePlayer(e.getPlayer());
	}
	public BumperManager getBumperManager() {
		return bumperManager;
	}
}
