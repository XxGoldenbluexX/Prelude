package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.core.util.UtilParticle;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Husk extends Effigy implements IProjectile{
	private static final int PRIMARY_COOLDOWN = 6 * 20;
	private static final int SECONDARY_COOLDOWN = 12 * 20;
	
	private static final int PASSIVE_WEAKNESS_DURATION = 2 * 20;
	private static final PotionEffect PASSIVE_WEAKNESS_EFFECT = new PotionEffect(PotionEffectType.WEAKNESS, PASSIVE_WEAKNESS_DURATION, 0, false, false, true);
	
	private static final int PRIMARY_BLINDNESS_DURATION = 2 * 20;
	private static final double PRIMARY_RANGE = 2;
	private static final double PRIMARY_DAMAGE = 1 * 2;
	private static final PotionEffect PRIMARY_BLINDNESS_EFFECT = new PotionEffect(PotionEffectType.BLINDNESS, PRIMARY_BLINDNESS_DURATION, 0, false, false, true);
	
	private static final int MAX_HUSK_COUNT = 4;
	private ArrayList<org.bukkit.entity.Husk> husks = new ArrayList<>();
	
	//
	
	public Husk(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}
	
	//
	
	@Override
	public void Faded(CustomProjectile arg0) {
	}
	@Override
	public void Hit(LivingEntity arg0, Block arg1, CustomProjectile arg2) {
	}
	@Override
	public void Triggered(CustomProjectile arg0) {
	}
	
	//
	
	@Override
	protected void castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		Tornado(getWrapper().getPlayer().getLocation());
		husks.forEach( husk -> Tornado(husk.getLocation()));	
	}
	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
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
	
	//
	
	@EventHandler
	public void OnDamage(LivingEntityDamageEvent e) {
		if(!IsPassiveApplicable(e)) return;
		
		e.GetDamaged().addPotionEffect(PASSIVE_WEAKNESS_EFFECT);
	}
	
	//
	
	private boolean IsPassiveApplicable(LivingEntityDamageEvent e) {
		return (getWrapper().getPlayer().equals(e.GetDamager()) || husks.contains(e.GetDamager())) && e.GetCause() == DamageCause.ENTITY_ATTACK;
	}
	private void Tornado(Location from) {
		UtilParticle.Vortex(from, PRIMARY_RANGE, 25, 2, 5, Particle.REDSTONE, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(191, 150, 61), 1));
		for(LivingEntity entity : UtilEntity.GetNearbyLivingEntities(from, PRIMARY_RANGE)) {
			if(entity instanceof Player && Main.getInstance().inSameTeam((Player)entity, getWrapper().getPlayer())) continue;
			
			entity.addPotionEffect(PRIMARY_BLINDNESS_EFFECT);
			Main.getInstance().getDamageModule().Damage(
					entity, 
					getWrapper().getPlayer(), 
					null, 
					DamageCause.CUSTOM, 
					PRIMARY_DAMAGE, 
					true, 
					true, 
					from);
		}
	}
}
