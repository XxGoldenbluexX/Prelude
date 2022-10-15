package fr.nekotine.prelude;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import fr.nekotine.core.bowcharge.BowChargeModule;
import fr.nekotine.core.charge.ChargeModule;
import fr.nekotine.core.damage.DamageModule;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.effect.CustomEffectModule;
import fr.nekotine.core.itemcharge.ItemChargeModule;
import fr.nekotine.core.lobby.GameModeIdentifier;
import fr.nekotine.core.lobby.LobbyModule;
import fr.nekotine.core.map.MapIdentifier;
import fr.nekotine.core.map.MapModule;
import fr.nekotine.core.module.ModuleManager;
import fr.nekotine.core.projectile.ProjectileModule;
import fr.nekotine.core.ticking.TickingModule;
import fr.nekotine.core.ticking.event.TickElapsedEvent;
import fr.nekotine.core.usable.UsableModule;
import fr.nekotine.core.visibility.EntityVisibilityModule;
import fr.nekotine.prelude.gamemode.GM_PreludeBo;
import fr.nekotine.prelude.inventories.MapInventory;
import fr.nekotine.prelude.map.PreludeMap;
import fr.nekotine.prelude.map.TestMap;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		super.onEnable();
		main=this;
		
		ModuleManager.Load(this,
				ChargeModule.class,
				TickingModule.class,
				ItemChargeModule.class,
				ProjectileModule.class,
				DamageModule.class,
				BowChargeModule.class,
				UsableModule.class,
				EntityVisibilityModule.class,
				CustomEffectModule.class,
				LobbyModule.class,
				MapModule.class
				);
		
		ModuleManager.EnableAll();
		
		GameModeIdentifier.registerGameMode(GM_PreludeBo.IDENTIFIER);
		
		MapModule.registerMapTypes(TestMap.IDENTIFIER);
		
		var mapModule = ModuleManager.GetModule(MapModule.class);
		
		mapModule.generateCommands();
		
		// TEST
		
		mapModule.addMap(new MapIdentifier(TestMap.IDENTIFIER, "testMap", "Displayed test map name", "Une map de test", Material.STONE));
		
		
		new CommandAPICommand("test").executes((sender, args) -> {
			TestMap testMap = (TestMap) TestMap.IDENTIFIER.generateTypedMap(MapModule.getMap("testMap"));
			sender.sendMessage(Component.text(testMap.getSpawnLocation().toString()));
		}).register();
		
		//
		
		EventRegisterer.registerEvent(this);
		
		Serializer.register();
		
		Commands.make().register();
		CommandAPI.onEnable(this);
		
		if (!getDataFolder().exists()) {//making dataFolder
			getDataFolder().mkdir();
		}
		File mapf = new File(getDataFolder(),"Maps");//making map Folder
		if (!mapf.exists()){
				mapf.mkdir();
		}
		PreludeMap.setMapFolder(mapf);
		
		Gameruler.enable();
		
		ArrayList<String> mapNameList = PreludeMap.getMapNameList();
		
		setMapName(mapNameList.isEmpty()?"null":mapNameList.get(0));
		
		roundManager = new RoundManager();
		mapInventory = new MapInventory();
		gameScoreboard = new GameScoreboard();
		bumperManager = new BumperManager();
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
		if (roundManager != null) roundManager.endGame();
		if (roundManager != null) roundManager.destroy();
		if (gameScoreboard != null) gameScoreboard.destroy();
		if (bumperManager != null) bumperManager.destroy();
		if (mapInventory != null) mapInventory.destroy();
		ModuleManager.DisableAll();
		
		end();
		
		EventRegisterer.unregisterEvent(this);
	}
	
	@EventHandler
	public void tick(TickElapsedEvent e) {
		if(running) {
			for(PlayerWrapper wrapper : getWrappers()) {
				wrapper.tick();
			}
			roundManager.tick();
		}
	}
	
	public boolean addPlayer(Player player, Team team) {
		if(!roundManager.isRoundPlaying() && !players.containsKey(player)) {
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
	@EventHandler(priority = EventPriority.LOW)
	public void OnDamage(LivingEntityDamageEvent e) {
		if(e.GetDamaged() instanceof Player && e.GetDamager() instanceof Player) {
			Player damaged = (Player)e.GetDamaged();
			Player damager = (Player)e.GetDamager();
			if(inSameTeam(damaged, damager) && e.GetDamage() >= 0) e.SetCancelled(true); 
		}
	}
	public boolean inSameTeam(Player player1, Player player2) {
		PlayerWrapper damaged = getWrapper(player1);
		PlayerWrapper damager = getWrapper(player2);
		
		return damaged != null && damager != null && damaged.getTeam()==damager.getTeam(); 
	}
	public BumperManager getBumperManager() {
		return bumperManager;
	}
	
	public ModuleManager getModuleManager() {
		return ModuleManager.getInstance();
	}
	public DamageModule getDamageModule() {
		return ModuleManager.GetModule(DamageModule.class);
	}
	public UsableModule getUsableModule() {
		return ModuleManager.GetModule(UsableModule.class);
	}
	public ProjectileModule getProjectileModule() {
		return ModuleManager.GetModule(ProjectileModule.class);
	}
	public ChargeModule getChargeModule() {
		return ModuleManager.GetModule(ChargeModule.class);
	}
	public BowChargeModule getBowChargeModule() {
		return ModuleManager.GetModule(BowChargeModule.class);
	}
	public ItemChargeModule getItemChargeModule() {
		return ModuleManager.GetModule(ItemChargeModule.class);
	}
	public EntityVisibilityModule getEntityVisibilityModule() {
		return ModuleManager.GetModule(EntityVisibilityModule.class);
	}
	public CustomEffectModule getCustomEffectModule() {
		return ModuleManager.GetModule(CustomEffectModule.class);
	}
}
