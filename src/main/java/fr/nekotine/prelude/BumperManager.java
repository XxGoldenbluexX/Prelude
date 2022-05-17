package fr.nekotine.prelude;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import fr.nekotine.prelude.utils.EventRegisterer;

public class BumperManager implements Listener{
	private boolean active = false;
	private final Material[] BUMPERS = {Material.SLIME_BLOCK};
	private final float UP_BOOST = 1;
	public BumperManager() {
		EventRegisterer.registerEvent(this);
	}
	public void destroy() {
		EventRegisterer.unregisterEvent(this);
	}
	public void activate(boolean active) {
		this.active = active;
	}
	
	private boolean isBumper(Material mat) {
		for(Material bumper : BUMPERS) {
			if(bumper==mat) return true;
		}
		return false;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(active && isBumper(e.getTo().subtract(0, 1, 0).getBlock().getType())) {
			e.getPlayer().getVelocity().add(new Vector(0, UP_BOOST, 0));
		}
	}
}
