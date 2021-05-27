package fr.nekotine.prelude;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Effigy implements Listener {

	private final PlayerWrapper wrapper;
	private long cd1;//current cooldown
	private long cd2;
	private int cooldown1;//cooldown time in ms
	private int cooldown2;
	
	public Effigy(PlayerWrapper w) {
		wrapper=w;
		cd1 = System.currentTimeMillis();
		cd2 = System.currentTimeMillis();
	}
	
	public void onPlayerDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().equals(wrapper.getPlayer()) && System.currentTimeMillis()>cd2) {
			event.setCancelled(true);
			cd2 = System.currentTimeMillis()+cooldown2;
			castSpell2();
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action a = event.getAction();
		if (event.getPlayer().equals(wrapper.getPlayer()) && (a==Action.RIGHT_CLICK_AIR || a==Action.RIGHT_CLICK_BLOCK) && System.currentTimeMillis()>cd1) {
			event.setCancelled(true);
			cd1 = System.currentTimeMillis()+cooldown1;
			castSpell1();
		}
	}
	
	protected abstract void castSpell1();
	protected abstract void castSpell2();

	public int getCooldown1() {
		return cooldown1;
	}

	public void setCooldown1(int cooldown1) {
		this.cooldown1 = cooldown1;
	}
	
	public int getCooldown2() {
		return cooldown2;
	}

	public void setCooldown2(int cooldown2) {
		this.cooldown2 = cooldown2;
	}
	
}
