package fr.nekotine.prelude;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PreludeMain extends JavaPlugin implements Listener{
	public static PreludeMain main;
	public HashMap<Player, PlayerWrapper> playerWrappers = new HashMap<Player, PlayerWrapper>();
	private static final Material effigyBlockMaterial = Material.END_PORTAL_FRAME;
	private static final Material teamBlockMaterial = Material.COMPOSTER;
	
	@Override
	public void onEnable() {
		super.onEnable();
		main=this;
		Bukkit.getPluginManager().registerEvents(this, this);
		for(Player player : Bukkit.getOnlinePlayers()) {
			playerWrappers.put(player, new PlayerWrapper(player,1,1));
		}
	}
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock()!=null) {
			if (e.getClickedBlock().getType()==effigyBlockMaterial){
			}else if (e.getClickedBlock().getType()==teamBlockMaterial){
				new TeamInventory(playerWrappers.get(e.getPlayer()));
			}
		}
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		playerWrappers.put(e.getPlayer(), new PlayerWrapper(e.getPlayer(),1,1));
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		playerWrappers.remove(e.getPlayer());
	}
}
