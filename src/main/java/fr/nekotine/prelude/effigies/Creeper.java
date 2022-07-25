package fr.nekotine.prelude.effigies;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import fr.nekotine.core.charge.ICharge;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.Disguiser;

public class Creeper extends Effigy implements ICharge{
	private static final int PRIMARY_COOLDOWN = 50;
	private static final int SECONDARY_COOLDOWN = 9 * 20;
	
	private static final double PASSIVE_DAMAGE = 0.75 * 2;
	private static final double PASSIVE_RADIUS = 2;
	
	private static final float PRIMARY_DASH = 0.75f;
	
	private float walkSpeed;
	private static final String SECONDARY_CHARGE_NAME = "CreeperSecondary";
	private static final long SECONDARY_CHARGE_TIME = 1500;
	private static final long SECONDARY_CHARGE_AUDIO_BIP = 2;
	private static final double SECONDARY_DAMAGE = 4.25 * 2;
	private static final double SECONDARY_SELF_DAMAGE = 0.5 * 2;
	private static final double SECONDARY_RADIUS = 4;
	
	//
	
	public Creeper(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}

	//
	
	@Override
	protected void castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_CREEPER_DEATH, 1, 0);
		getWrapper().getPlayer().setVelocity(getWrapper().getPlayer().getEyeLocation().getDirection().multiply(PRIMARY_DASH));
	}
	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		walkSpeed = getWrapper().getPlayer().getWalkSpeed();
		getWrapper().getPlayer().setWalkSpeed(0);
		
		Disguiser.setIgnitedCreeper(getDisguise(), true);
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 0);
		 Main.getInstance().getChargeModule().AddCharge(
				getWrapper().getPlayer().getName(), 
				SECONDARY_CHARGE_NAME, 
				SECONDARY_CHARGE_TIME, 
				true, 
				true, 
				SECONDARY_CHARGE_AUDIO_BIP, 
				this);
	}
	@Override
	protected void roundEnd() {
		CancelCharge();
		getWrapper().getPlayer().setWalkSpeed(walkSpeed);
	}
	@Override
	protected void death() {
		CancelCharge();
		getWrapper().getPlayer().setWalkSpeed(walkSpeed);
	}
	@Override
	protected void roundStart() {
	}
	
	//
	
	@EventHandler
	public void OnDamage(LivingEntityDamageEvent e) {
		if(e.IsCancelled()) return;
		//Thorns
		if(getWrapper().getPlayer().equals(e.GetDamaged()) && !e.GetDamaged().equals(e.GetDamager()) && e.GetCause()==DamageCause.ENTITY_ATTACK) {
			getWrapper().getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, getWrapper().getPlayer().getLocation().add(0, 0.5, 0), 1);
			getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 0.5f, 1);
			 Main.getInstance().getDamageModule().Explode(
					getWrapper().getPlayer(), 
					PASSIVE_RADIUS, 
					DamageCause.THORNS, 
					PASSIVE_DAMAGE, 
					true, 
					false, 
					getWrapper().getPlayer().getLocation(), 
					true);
		}
		//AA lors du secondaire
		if(getWrapper().getPlayer().equals(e.GetDamager()) && e.GetCause()==DamageCause.ENTITY_ATTACK && IsCharging()) {
			e.SetCancelled(true);
		}
	}
	@EventHandler
	public void OnJump(PlayerJumpEvent e) {
		if(getWrapper().getPlayer().equals(e.getPlayer()) && IsCharging()) e.setCancelled(true);
	}

	//
	
	@Override
	public void Cancelled(String arg0, String arg1, long arg2) {
	}
	@Override
	public void Ended(String arg0, String arg1) {
		Disguiser.setIgnitedCreeper(getDisguise(), false);
		
		getWrapper().getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, getWrapper().getPlayer().getLocation(), 1);
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 3, 0);
		 Main.getInstance().getDamageModule().Explode(
				getWrapper().getPlayer(), 
				SECONDARY_RADIUS, 
				DamageCause.CUSTOM, 
				SECONDARY_DAMAGE, 
				true, 
				true, 
				getWrapper().getPlayer().getLocation(), 
				true);
		
		 Main.getInstance().getDamageModule().Damage(
				getWrapper().getPlayer(),
				null,
				null, 
				DamageCause.CUSTOM, 
				SECONDARY_SELF_DAMAGE, 
				true, 
				false, 
				null);
		
		getWrapper().getPlayer().setWalkSpeed(walkSpeed);
	}
	
	//
	
	private void CancelCharge() {
		 Main.getInstance().getChargeModule().SetCancelled(getWrapper().getPlayer().getName(), SECONDARY_CHARGE_NAME, true);
	}
	private boolean IsCharging() {
		return Main.getInstance().getChargeModule().Exist(getWrapper().getPlayer().getName(), SECONDARY_CHARGE_NAME);
	}
}
