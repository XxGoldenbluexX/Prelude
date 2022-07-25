package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.core.charge.ICharge;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.usable.Usable;
import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.Disguiser;

public class Blaze extends Effigy implements IProjectile, ICharge{
	private static final int PRIMARY_COOLDOWN = 1 * 20;
	private static final int SECONDARY_COOLDOWN = 15 * 20;
	
	private static final long PASSIVE_CHARGE_TIME = 2 * 1000;
	private static final String PASSIVE_CHARGE_NAME = "BlazePassive";
	private static final PotionEffect PASSIVE_SLOW_FALLING = new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0, false, false, true);
	private static final float DOUBLE_JUMP_HEIGHT = 0.7f;
	
	private static final int MAX_FIREBALL = 5;
	private static final float FIREBALL_SPEED = 1.25f;
	private static final double FIREBALL_DAMAGE = 0.5 * 2;
	private static final int FIREBALL_FIRE_DURATION = 2 * 20 + 10;
	
	private static final int SECONDARY_DURATION = 5 * 1000;
	private static final int SECONDARY_BURN_DURATION = FIREBALL_FIRE_DURATION;
	private static final String SECONDARY_CHARGE_NAME = "BlazeSecondary";
	
	private static final Material FIREBALL_MATERIAL = Material.FIRE_CHARGE;
	private static final Material NO_FIREBALL_MATERIAL = Material.FIREWORK_STAR;
	
	private static final Material[] NONE = {};
	
	private Usable fireballs;
	
	//
	
	public Blaze(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		wrapper.getPlayer().addPotionEffect(PASSIVE_SLOW_FALLING);
		
		fireballs = Main.getInstance().getUsableModule().AddUsable(
				new ItemStack(NO_FIREBALL_MATERIAL), 
				getWrapper().getPlayer().getInventory());
		fireballs.SetName("Charges");
		fireballs.Give();
	}

	//
	
	@Override
	public void Faded(CustomProjectile arg0) {
	}
	@Override
	public void Hit(LivingEntity hitE, Block arg1, CustomProjectile proj) {
		 Main.getInstance().getDamageModule().Damage(
				hitE, 
				getWrapper().getPlayer(), 
				(Fireball)proj.GetProjectile(), 
				DamageCause.CUSTOM, 
				FIREBALL_DAMAGE, 
				true, 
				IsBurning(), 
				proj.GetProjectile().getLocation());
		
		hitE.setFireTicks(Math.max(hitE.getFireTicks(), FIREBALL_FIRE_DURATION));
		
		proj.GetProjectile().remove();
	}
	@Override
	public void Triggered(CustomProjectile arg0) {
	}

	//
	
	@Override
	protected void castPrimarySpell() {
		if(!CanUseSpell()) return;

		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);

		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 0);
		
		Fireball fireball = getWrapper().getPlayer().launchProjectile(Fireball.class);
		fireball.setInvulnerable(true);
		ArrayList<Player> inTeam = Main.getInstance().getPlayersInTeam(getWrapper().getTeam());
		 Main.getInstance().getProjectileModule().AddProjectile(
				fireball, 
				getWrapper().getPlayer(), 
				this, 
				getWrapper().getPlayer().getEyeLocation().getDirection().multiply(FIREBALL_SPEED), 
				10 * 1000, 
				true, 
				false,
				inTeam.toArray(new LivingEntity[inTeam.size()]),
				NONE);
		
		RemoveFireball();
	}
	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 0);
		
		 Main.getInstance().getChargeModule().AddCharge(
				getWrapper().getPlayer().getName(), 
				SECONDARY_CHARGE_NAME, 
				SECONDARY_DURATION, 
				false, 
				false, 
				0, 
				this);
		
		Disguiser.setBurning(getDisguise(), true);
	}
	@Override
	protected void roundEnd() {
	}
	@Override
	protected void death() {
	}
	@Override
	protected void roundStart() {
		AddCharge();
	}
	@Override
	protected void destroy() {
		getWrapper().getPlayer().removePotionEffect(PASSIVE_SLOW_FALLING.getType());
		fireballs.Remove();
		getWrapper().getPlayer().setAllowFlight(true);
		 Main.getInstance().getChargeModule().SetCancelled(getWrapper().getPlayer().getName(), PASSIVE_CHARGE_NAME, true);
		super.destroy();
	}
	
	//
	
	private boolean CanUseSpell() {
		return fireballs.GetMaterial()==FIREBALL_MATERIAL;
	}
	private void RemoveFireball() {
		if(!CanAddFireball()) AddCharge();
		
		if(fireballs.GetAmount() == 1) {
			fireballs.SetMaterial(NO_FIREBALL_MATERIAL);
			fireballs.SetAmount(1);
			fireballs.SetEnchantedGlow(false);
			return;
		}
		fireballs.AddAmount(-1);
	}
	private void AddFireball() {
		if(fireballs.GetMaterial() == NO_FIREBALL_MATERIAL) {
			fireballs.SetMaterial(FIREBALL_MATERIAL);
			fireballs.SetAmount(1);
			fireballs.SetEnchantedGlow(true);
			return;
		}
		fireballs.AddAmount(1);
	}
	private boolean CanAddFireball() {
		return fireballs.GetMaterial()==NO_FIREBALL_MATERIAL || fireballs.GetAmount() < MAX_FIREBALL;
	}
	private void AddCharge() {
		 Main.getInstance().getChargeModule().AddCharge(
				getWrapper().getPlayer().getName(), 
				PASSIVE_CHARGE_NAME, 
				PASSIVE_CHARGE_TIME, 
				true, 
				false, 
				0, 
				this);
	}
	private boolean IsBurning() {
		return  Main.getInstance().getChargeModule().Exist(getWrapper().getPlayer().getName(), SECONDARY_CHARGE_NAME);
	}
	
	//

	@Override
	public void Cancelled(String arg0, String arg1, long arg2) {
	}
	@Override
	public void Ended(String arg0, String chargeName) {
		if(chargeName == PASSIVE_CHARGE_NAME) {
			AddFireball();
			if(CanAddFireball()) AddCharge();
		}
		if(chargeName == SECONDARY_CHARGE_NAME) {
			getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 0);
			Disguiser.setBurning(getDisguise(), false);
		}
	}
	
	//
	
	@EventHandler
	public void OnDoubleJump(PlayerToggleFlightEvent e) {
		if(!getWrapper().getPlayer().equals(e.getPlayer())) return;
		
		e.setCancelled(true);
		getWrapper().getPlayer().setAllowFlight(false);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.3f, 1.5f);
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ITEM_HOE_TILL, 1, 0.1f);
		
		getWrapper().getPlayer().setVelocity(getWrapper().getPlayer().getVelocity().setY(DOUBLE_JUMP_HEIGHT));
	}
	@EventHandler
	public void OnMove(PlayerMoveEvent e) {
		if(getWrapper().getPlayer().equals(e.getPlayer()) && UtilEntity.IsOnGround(getWrapper().getPlayer())) getWrapper().getPlayer().setAllowFlight(true);
	}
	@EventHandler
	public void OnDamage(LivingEntityDamageEvent e) {
		if(!e.IsCancelled() && getWrapper().getPlayer().equals(e.GetDamaged()) && e.GetCause()==DamageCause.ENTITY_ATTACK && IsBurning() && e.GetDamager() != null) {
			
			e.GetDamager().setFireTicks(Math.max(e.GetDamager().getFireTicks(), SECONDARY_BURN_DURATION));
			getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 0);
		
		}
	}
}
