package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.nekotine.core.charge.ChargeManager;
import fr.nekotine.core.charge.ICharge;
import fr.nekotine.core.damage.DamageManager;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.projectile.ProjectileManager;
import fr.nekotine.core.ticking.event.TickElapsedEvent;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Hoglin extends Effigy implements IProjectile,ICharge{
	private static final int PRIMARY_COOLDOWN = 13 * 20;
	private static final int SECONDARY_COOLDOWN = 8 * 20;
	
	private static final PotionEffect PASSIVE_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false, false);
	
	private static final double PRIMARY_DAMAGE = 1 * 2;
	private static final long PRIMARY_DURATION = 750;
	private static final float PRIMARY_SPEED = 0.4f;
	private static final float PRIMARY_BUMP_HEIGHT = 0.75f;
	private Vector primaryVelocity;
	private ArrayList<LivingEntity> primaryDamaged = new ArrayList<>();
	
	private static final double SECONDARY_BONUS_DAMAGE = 0.5 * 2;
	private static final long SECONDARY_DURATION = 2 * 1000;
	private static final String SECONDARY_CHARGE_NAME = "HoglinSecondary";
	private static final PotionEffect SECONDARY_SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, 2 * 20, 0, false, false, true);
	private static final PotionEffect SECONDARY_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false, true);
	
	//
	
	public Hoglin(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		wrapper.getPlayer().addPotionEffect(PASSIVE_SPEED_EFFECT);
	}

	//
	
	@Override
	protected void castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED, 1, 0);
		primaryVelocity = getWrapper().getPlayer().getEyeLocation().getDirection().multiply(PRIMARY_SPEED).setY(0);
		primaryDamaged.clear();
		Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
				getWrapper().getPlayer(),
				getWrapper().getPlayer(),
				this, 
				primaryVelocity,
				PRIMARY_DURATION,
				true, 
				false);
	}
	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_HOGLIN_ANGRY, 1, 0);
		getWrapper().getPlayer().addPotionEffect(SECONDARY_SPEED_EFFECT);
		Main.getInstance().getModuleManager().Get(ChargeManager.class).AddCharge(
				getWrapper().getPlayer().getName(), 
				SECONDARY_CHARGE_NAME, 
				SECONDARY_DURATION, 
				true, 
				false, 
				0, 
				this);
	}
	@Override
	protected void roundEnd() {
	}
	@Override
	protected void death() {
	}
	@Override
	protected void roundStart() {
	}
	@Override
	protected void destroy() {
		getWrapper().getPlayer().removePotionEffect(PASSIVE_SPEED_EFFECT.getType());
		super.destroy();
	}
	
	//
	
	@EventHandler
	public void OnInterract(PlayerInteractAtEntityEvent e) {
		if(getWrapper().getPlayer().equals(e.getRightClicked()) && Main.getInstance().inSameTeam(getWrapper().getPlayer(), e.getPlayer())) 
			getWrapper().getPlayer().addPassenger(e.getPlayer());
	}
	@EventHandler
	public void OnDamage(LivingEntityDamageEvent e) {
		if(getWrapper().getPlayer().equals(e.GetDamager()) && e.GetCause()==DamageCause.ENTITY_ATTACK && IsSecondaryActive()) {
			e.GetDamaged().addPotionEffect(SECONDARY_SLOW_EFFECT);
			e.AddBaseMod(SECONDARY_BONUS_DAMAGE);
			Main.getInstance().getModuleManager().Get(ChargeManager.class).SetCancelled(getWrapper().getPlayer().getName(), SECONDARY_CHARGE_NAME, true);
			getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_HOGLIN_ANGRY, 1, 0);
		}
	}
	@EventHandler
	public void OnTick(TickElapsedEvent e) {
		if(Main.getInstance().getModuleManager().Get(ProjectileManager.class).Exist(getWrapper().getPlayer())) {
			//getWrapper().getPlayer().teleport(getWrapper().getPlayer().getLocation().setDirection(primaryVelocity));
			getWrapper().getPlayer().setVelocity(primaryVelocity.setY(getWrapper().getPlayer().getVelocity().getY()));
		}	
	}
	
	//
	
	private boolean IsSecondaryActive() {
		return Main.getInstance().getModuleManager().Get(ChargeManager.class).Exist(getWrapper().getPlayer().getName(), SECONDARY_CHARGE_NAME);
	}
	
	//

	@Override
	public void Faded(CustomProjectile arg0) {
	}
	@Override
	public void Hit(LivingEntity hitE, Block arg1, CustomProjectile proj) {
		proj.SetCancelled(true);
		if(primaryDamaged.contains(hitE)) return;
		
		hitE.setVelocity(hitE.getVelocity().setY(PRIMARY_BUMP_HEIGHT));
		Main.getInstance().getModuleManager().Get(DamageManager.class).Damage(
				hitE, 
				getWrapper().getPlayer(), 
				null, 
				DamageCause.CUSTOM, 
				PRIMARY_DAMAGE, 
				true, 
				false, 
				null);
		primaryDamaged.add(hitE);
		
	}
	@Override
	public void Triggered(CustomProjectile arg0) {
	}
	
	//
	
	@Override
	public void Cancelled(String arg0, String arg1, long arg2) {
		getWrapper().getPlayer().removePotionEffect(SECONDARY_SPEED_EFFECT.getType());
		getWrapper().getPlayer().addPotionEffect(PASSIVE_SPEED_EFFECT);
	}
	@Override
	public void Ended(String arg0, String arg1) {
		getWrapper().getPlayer().removePotionEffect(SECONDARY_SPEED_EFFECT.getType());
		getWrapper().getPlayer().addPotionEffect(PASSIVE_SPEED_EFFECT);
	}
}
