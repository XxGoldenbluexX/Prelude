package fr.nekotine.prelude;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.prelude.utils.EventRegisterer;

public class BumperManager implements Listener{
	private boolean active = false;
	private final HashMap<Material, Float> bumpers = new HashMap<>();

	public BumperManager() {
		EventRegisterer.registerEvent(this);
		add(Material.SLIME_BLOCK, 1.25f);
		add(Material.HONEY_BLOCK, 2f);
	}
	public void destroy() {
		EventRegisterer.unregisterEvent(this);
	}
	public void activate(boolean active) {
		this.active = active;
	}
	
	//
	
	private void add(Material material, float up_boost) {
		bumpers.put(material, up_boost);
	}
	private Float getBoost(Material material) {
		return bumpers.get(material);
	}
	private boolean exist(Material material) {
		return bumpers.containsKey(material);
	}
	
	//
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!active) return;
		if(!UtilEntity.IsOnGround(e.getPlayer())) return;
		
		Material type = e.getTo().clone().subtract(0, 1, 0).getBlock().getType();
		
		
		if(exist(type)) {
			e.getPlayer().setVelocity(e.getPlayer().getVelocity().setY(getBoost(type)));
		}
	}
}
