package fr.nekotine.prelude.effigies;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TraderLlama;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.core.charge.ICharge;
import fr.nekotine.core.ticking.event.TickElapsedEvent;
import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.core.util.UtilMobAi;
import fr.nekotine.core.util.UtilParticle;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Trader extends Effigy implements ICharge{
	private static final int PRIMARY_COOLDOWN = 5 * 20;
	private static final int SECONDARY_COOLDOWN = 20 * 20;
	
	private TraderLlama lama;
	private static final long LLAMA_HEALTH = 10;
	
	private static final long PASSIVE_COOLDOWN = 2 * 1000;
	private static final double PASSIVE_HEAL = 0.5 * 2;
	private static final PotionEffect PASSIVE_SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, 3 * 20, 1, false, false, true);
	private static final String PASSIVE_CHARGE_NAME = "TraderPassive";
	private static final long PASSIVE_RADIUS = 4;
	
	private static final long PRIMARY_HEAL = 1 * 2;
	private static final long PRIMARY_DAMAGE = 1 * 2;
	
	private static final String SECONDARY_CHARGE_NAME = "TraderSecondary";
	private static final long SECONDARY_DURATION = 5 * 1000;
	
	//
	
	public Trader(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}

	//
	
	@Override
	protected void castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_WANDERING_TRADER_YES, 1, 0);
		
		if(lama != null && lama.isValid()) {
			lama.setKiller(null);
			lama.setHealth(0);
		}else {
			AddPassiveCharge();
		}
		lama = (TraderLlama)getWrapper().getPlayer().getWorld().spawnEntity(getWrapper().getPlayer().getLocation(), EntityType.TRADER_LLAMA);
		ConfigureLlama();
	}
	@Override
	protected void castSecondarySpell() {
		if(lama == null || !lama.isValid()) return;
		
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_WANDERING_TRADER_DISAPPEARED, 1, 0);
		
		/*for(Player player : Main.getInstance().getPlayers()) {
			if(Main.getInstance().inSameTeam(player, getWrapper().getPlayer())) continue;
			
			Main.getInstance().getModuleManager().Get(EntityVisibilityModule.class).hideFrom(getWrapper().getPlayer(), player);
		}*/
		
		Location temp = lama.getLocation();
		lama.teleport(getWrapper().getPlayer());
		getWrapper().getPlayer().teleport(temp);
		
		
		
		AddSecondaryCharge();
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
		CancelPassiveCharge();
		if(lama != null) lama.remove();
		super.destroy();
	}

	//
	
	@Override
	public void Cancelled(String arg0, String arg1, long arg2) {
	}
	@Override
	public void Ended(String arg0, String chargeName) {
		if(chargeName == PASSIVE_CHARGE_NAME) {
			if(!lama.isValid()) return;
			
			lama.getWorld().playSound(lama, Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 0);
			
			
			
			for(Entity near : UtilEntity.GetNearbyLivingEntities(lama.getLocation(), PASSIVE_RADIUS)) {
				if(near.equals(lama) || !(near instanceof LivingEntity)) continue;
				
				if(near instanceof Player && Main.getInstance().inSameTeam((Player)near, getWrapper().getPlayer())) {
					Main.getInstance().getDamageModule().Damage(
							(Player)near,
							getWrapper().getPlayer(), 
							null,
							DamageCause.CUSTOM, 
							-PASSIVE_HEAL, 
							true, 
							false, 
							null);
				}else {
					((LivingEntity)near).addPotionEffect(PASSIVE_SLOW_EFFECT);
				}
			}
			
			AddPassiveCharge();
			
		}else {
			/*for(Player player : Main.getInstance().getPlayers()) {
				if(Main.getInstance().inSameTeam(player, getWrapper().getPlayer())) continue;
				
				Main.getInstance().getModuleManager().Get(EntityVisibilityModule.class).showFrom(getWrapper().getPlayer(), player);
			}*/
			getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_WANDERING_TRADER_REAPPEARED, 1, 0);
		}
		
	}
	
	//
	
	private void AddPassiveCharge() {
		Main.getInstance().getChargeModule().AddCharge(
				getWrapper().getPlayer().getName(), 
				PASSIVE_CHARGE_NAME, 
				PASSIVE_COOLDOWN, 
				true, 
				false, 
				0,
				this);
	}
	private void AddSecondaryCharge() {
		Main.getInstance().getChargeModule().AddCharge(
				getWrapper().getPlayer().getName(), 
				SECONDARY_CHARGE_NAME, 
				SECONDARY_DURATION, 
				false, 
				true, 
				4,
				this);
	}
	private void ConfigureLlama() {
		lama.setAgeLock(true);
		UtilMobAi.clearBrain(lama);
		lama.setColor(Color.BROWN);
		UtilEntity.SetMaxHealth(lama, LLAMA_HEALTH);
	}
	private void CancelPassiveCharge() {
		Main.getInstance().getChargeModule().SetCancelled(getWrapper().getPlayer().getName(), PASSIVE_CHARGE_NAME, true);
	}
	
	//
	
	@EventHandler
	public void OnDeath(EntityDeathEvent e) {
		if(e.getEntity().equals(lama)) {
			e.setDroppedExp(0);
			e.getDrops().clear();
			
			if(lama.getKiller() != null) {
				long damage = PRIMARY_DAMAGE;
				Player killer = lama.getKiller();
				if(killer != null && Main.getInstance().inSameTeam(killer, getWrapper().getPlayer())) {
					damage = -PRIMARY_HEAL;
				}
				
				Main.getInstance().getDamageModule().Damage(
						lama.getKiller(),
						getWrapper().getPlayer(), 
						null,
						DamageCause.CUSTOM, 
						damage, 
						true, 
						false, 
						null);
				
				CancelPassiveCharge();
			}
		}
	}
	@EventHandler
	public void OnTick(TickElapsedEvent e) {
		if(lama == null || !lama.isValid() || !UtilEntity.IsOnGround(lama)) return; 
			
		UtilParticle.Circle2D(lama.getLocation(), PASSIVE_RADIUS, 25, Particle.FIREWORKS_SPARK, 1);
	}
}
