package fr.nekotine.prelude;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.Disguiser;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.ItemStackMaker;

public abstract class Effigy implements Listener {

	private final PlayerWrapper wrapper;
	private final EffigyList effigyType;
	
	private boolean primary_ability_on_cooldown = false;
	private boolean secondary_ability_on_cooldown = false;
	private long primary_ability_current_cooldown_ticks = 0;//current cooldown
	private long secondary_ability_current_cooldown_ticks = 0;
	
	private final ItemStack weapon;
	public PlayerWrapper getWrapper() {
		return wrapper;
	}
	public EffigyList getEffigyType() {
		return effigyType;
	}
	
	public Effigy(PlayerWrapper wrapper, EffigyList effigyType) {
		this.wrapper=wrapper;
		this.effigyType=effigyType;
		
		weapon = ItemStackMaker.make(effigyType.getWeaponMaterial(), 1, effigyType.getName(), effigyType.getDescription());
		giveWeapon();
		
		Disguiser.disguiseToAll(wrapper.getPlayer(), effigyType.getDisguiseType());
		EventRegisterer.registerEvent(this);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action a = event.getAction();
		Player p = event.getPlayer();
		if(p.equals(wrapper.getPlayer()) && weapon.equals(event.getItem())) {
			event.setCancelled(true);
			if (Main.getInstance().isRoundPlaying() && (a==Action.RIGHT_CLICK_AIR || a==Action.RIGHT_CLICK_BLOCK) && !primary_ability_on_cooldown) {
				castPrimarySpell();
			}
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		System.out.println("Player drop");
		Player p = event.getPlayer();
		if(p.equals(wrapper.getPlayer()) && weapon.equals(event.getItemDrop().getItemStack())) {
			System.out.println("Its him");
			event.setCancelled(true);
			if (Main.getInstance().isRoundPlaying() && !secondary_ability_on_cooldown) {
				castSecondarySpell();
			}
		}
	}
	
	
	
	public void tick() {
		if(primary_ability_on_cooldown) {
			primary_ability_current_cooldown_ticks--;
			if(primary_ability_current_cooldown_ticks<=0) {
				primary_ability_on_cooldown = false;
			}
		}
		if(secondary_ability_on_cooldown) {
			secondary_ability_current_cooldown_ticks--;
			if(secondary_ability_current_cooldown_ticks<=0) {
				secondary_ability_on_cooldown = false;
			}
		}
	}
	
	public void destroy() {
		System.out.println("destroy");
		removeWeapon();
		Disguiser.undisguise(wrapper.getPlayer());
		EventRegisterer.unregisterEvent(this);
	}
	
	public void setCooldown(Ability ability, int ticks_duration) {
		switch (ability) {
		case PRIMARY:
			primary_ability_current_cooldown_ticks = ticks_duration;
			primary_ability_on_cooldown = true;
			break;
		case SECONDARY:
			secondary_ability_current_cooldown_ticks = ticks_duration;
			secondary_ability_on_cooldown = true;
			break;
		}
	}
	
	public void removeWeapon() {
		wrapper.getPlayer().getInventory().removeItemAnySlot(weapon);
	}
	public void giveWeapon() {
		wrapper.getPlayer().getInventory().addItem(weapon);
	}
	
	protected abstract void castPrimarySpell();
	protected abstract void castSecondarySpell();
	protected abstract void roundEnd();
}
