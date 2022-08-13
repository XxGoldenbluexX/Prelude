package fr.nekotine.prelude;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.core.bowcharge.BowChargeModule;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.itemcharge.ItemChargeModule;
import fr.nekotine.core.projectile.ProjectileModule;
import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.ComponentMaker;
import fr.nekotine.prelude.utils.Disguiser;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.ItemStackMaker;
import fr.nekotine.prelude.utils.MessageSender;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public abstract class Effigy implements Listener {

	private final PlayerWrapper wrapper;
	private final EffigyList effigyType;
	private final MobDisguise disguise;
	
	private boolean primary_ability_locked = false;
	private boolean secondary_ability_locked = false;
	private boolean primary_ability_on_cooldown = false;
	private boolean secondary_ability_on_cooldown = false;
	private int primary_ability_total_cooldown_ticks = 0;
	private int secondary_ability_total_cooldown_ticks = 0;
	private int primary_ability_current_cooldown_ticks = 0;//current cooldown
	private int secondary_ability_current_cooldown_ticks = 0;
	
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
		
		wrapper.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(effigyType.getHealth());
		wrapper.getPlayer().setHealth(effigyType.getHealth());

		disguise = Disguiser.disguiseToAll(wrapper.getPlayer(), effigyType.getDisguiseType());
		EventRegisterer.registerEvent(this);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action a = event.getAction();
		Player p = event.getPlayer();
		if(p.equals(wrapper.getPlayer()) && weapon.equals(event.getItem())) {
			event.setCancelled(true);
			if (Main.getInstance().isRoundPlaying() && (a==Action.RIGHT_CLICK_AIR || a==Action.RIGHT_CLICK_BLOCK) && !primary_ability_locked && !primary_ability_on_cooldown) {
				if(castPrimarySpell()) {
					MessageSender.sendMessage(MessageSender.getSpell(effigyType.getPrimarySpellName()), p);
				}else {
					playErrorSound();
				}
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if(p.equals(wrapper.getPlayer()) && weapon.equals(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
			if (Main.getInstance().isRoundPlaying() && !secondary_ability_locked && !secondary_ability_on_cooldown && castSecondarySpell()) {
				if(castSecondarySpell()) {
					MessageSender.sendMessage(MessageSender.getSpell(effigyType.getSecondarySpellName()), p);
				}else {
					playErrorSound();
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onHit(LivingEntityDamageEvent e) {
		if(wrapper.getPlayer().equals(e.GetDamager()) && e.GetCause()==DamageCause.ENTITY_ATTACK) {
			e.SetDamage(effigyType.getDamage());
			e.SetIgnoreArmor(true);
		}
	}
	
	@EventHandler
	public void OnHitBlock(ProjectileHitEvent e) {
		if(wrapper.getPlayer().equals( e.getEntity().getShooter() ) && e.getEntity() instanceof Arrow && e.getHitBlock() != null) {
			e.getEntity().remove();
		}
	}
	
	public void tick() {
		wrapper.getPlayer().sendActionBar(ComponentMaker.getComponent(MessageSender.getCooldownTimer(this)));
		if(!primary_ability_locked && primary_ability_on_cooldown) {
			primary_ability_current_cooldown_ticks--;
			if(primary_ability_current_cooldown_ticks<=0) {
				primary_ability_on_cooldown = false;
			}
		}
		if(!secondary_ability_locked && secondary_ability_on_cooldown) {
			secondary_ability_current_cooldown_ticks--;
			if(secondary_ability_current_cooldown_ticks<=0) {
				secondary_ability_on_cooldown = false;
			}
		}
	}
	
	protected void destroy() {
		System.out.println("destroy");
		Main.getInstance().getModuleManager().Get(ItemChargeModule.class).DestroyFromPlayer(wrapper.getPlayer());
		Main.getInstance().getModuleManager().Get(BowChargeModule.class).DestroyFromPlayer(wrapper.getPlayer());
		Main.getInstance().getModuleManager().Get(ProjectileModule.class).TriggerFromSender(wrapper.getPlayer());
		removeWeapon();
		getWrapper().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		Disguiser.undisguise(wrapper.getPlayer());
		EventRegisterer.unregisterEvent(this);
	}
	
	public void setCooldown(Ability ability, int ticks_duration) {
		switch (ability) {
		case PRIMARY:
			primary_ability_current_cooldown_ticks = ticks_duration;
			primary_ability_total_cooldown_ticks = ticks_duration;
			primary_ability_on_cooldown = true;
			break;
		case SECONDARY:
			secondary_ability_current_cooldown_ticks = ticks_duration;
			secondary_ability_total_cooldown_ticks = ticks_duration;
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
	public boolean isOnCooldown(Ability ability) {
		switch(ability) {
		case PRIMARY:
			return primary_ability_on_cooldown;
		case SECONDARY:
			return secondary_ability_on_cooldown;
		default:
			return false;
		}
	}
	public int getCooldown(Ability ability) {
		switch(ability) {
		case PRIMARY:
			return primary_ability_current_cooldown_ticks;
		case SECONDARY:
			return secondary_ability_current_cooldown_ticks;
		default:
			return -1;
		}
	}
	public int getTotalCooldown(Ability ability) {
		switch(ability) {
		case PRIMARY:
			return primary_ability_total_cooldown_ticks;
		case SECONDARY:
			return secondary_ability_total_cooldown_ticks;
		default:
			return -1;
		}
	}
	public void setAbilityLocked(Ability ability, boolean locked) {
		switch(ability) {
		case PRIMARY:
			primary_ability_locked = locked;
			break;
		case SECONDARY:
			secondary_ability_locked = locked;
			break;
		}
	}
	public MobDisguise getDisguise() {
		return disguise;
	}
	
	//
	
	private void playErrorSound() {
		getWrapper().getPlayer().playSound(getWrapper().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0);
	}
	
	//
	
	protected abstract boolean castPrimarySpell();
	protected abstract boolean castSecondarySpell();
	protected abstract void roundEnd();
	protected abstract void death();
	protected abstract void roundStart();
	
}
