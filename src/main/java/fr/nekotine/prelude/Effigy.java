package fr.nekotine.prelude;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public abstract class Effigy implements Listener {

	private final PlayerWrapper wrapper;
	private long cd1;//current cooldown
	private long cd2;
	private int cooldown1;//cooldown time in ms
	private int cooldown2;
	
	public PlayerWrapper getWrapper() {
		return wrapper;
	}
	
	public Effigy(PlayerWrapper w,int cooldown1, int cooldown2) {
		wrapper=w;
		cd1 = System.currentTimeMillis();
		cd2 = System.currentTimeMillis();
		this.cooldown1 = cooldown1;
		this.cooldown2 = cooldown2;
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (p.equals(wrapper.getPlayer())) {
			if (System.currentTimeMillis()>cd2) {
				event.setCancelled(true);
				cd2 = System.currentTimeMillis()+cooldown2;
				castSpell2();
			}else {
				p.sendMessage(Component.text("Votre sort 2 est en recharge").color(TextColor.color(255, 0, 0)));
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action a = event.getAction();
		Player p = event.getPlayer();
		if (p.equals(wrapper.getPlayer()) && (a==Action.RIGHT_CLICK_AIR || a==Action.RIGHT_CLICK_BLOCK)) {
			if (System.currentTimeMillis()>cd1) {
				event.setCancelled(true);
				cd1 = System.currentTimeMillis()+cooldown1;
				castSpell1();
			}else {
				p.sendMessage(Component.text("Votre sort 1 est en recharge").color(TextColor.color(255, 0, 0)));
			}
		}
	}
	
	@EventHandler
	public void onDamageTaken(EntityDamageByEntityEvent e) {
		if(e.getEntity().equals(wrapper.getPlayer()) && e.getDamager() instanceof Player) {
			if(PreludeMain.main.playerWrappers.get((Player)e.getDamager()).getTeam()==wrapper.getTeam()) {
				e.setCancelled(true);
			}
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
