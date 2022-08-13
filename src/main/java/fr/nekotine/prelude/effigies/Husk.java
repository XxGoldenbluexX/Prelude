package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.ticking.event.TickElapsedEvent;
import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.core.util.UtilEvent;
import fr.nekotine.core.util.UtilMobAi;
import fr.nekotine.core.util.UtilParticle;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.ComponentMaker;

public class Husk extends Effigy implements IProjectile{
	private static final int PRIMARY_COOLDOWN = 6 * 20;
	private static final int SECONDARY_COOLDOWN = 12 * 20;
	
	private static final int PASSIVE_WEAKNESS_DURATION = 2 * 20;
	private static final PotionEffect PASSIVE_WEAKNESS_EFFECT = new PotionEffect(PotionEffectType.WEAKNESS, PASSIVE_WEAKNESS_DURATION, 0, false, false, true);
	
	private static final int PRIMARY_BLINDNESS_DURATION = 2 * 20;
	private static final double PRIMARY_RANGE = 2;
	private static final double PRIMARY_DAMAGE = 1 * 2;
	private static final PotionEffect PRIMARY_BLINDNESS_EFFECT = new PotionEffect(PotionEffectType.BLINDNESS, PRIMARY_BLINDNESS_DURATION, 0, false, false, true);
	
	private static final double SECONDARY_RANGE = 5;
	private static final int MAX_HUSK_COUNT = 4;
	private static final double HUSK_HEALTH = 10 * 2;
	private static final double HUSK_DAMAGE = 1 * 2;
	private Cemetery cemetery;
	
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
	protected boolean castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		Tornado(getWrapper().getPlayer().getLocation());
		
		if(cemetery != null) 
			cemetery.GetHusks().forEach( husk -> Tornado(husk.getLocation()));
		
		return true;
	}
	@Override
	protected boolean castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		Destroy();
		cemetery = new Cemetery(getWrapper().getPlayer().getLocation(), SECONDARY_RANGE, MAX_HUSK_COUNT, HUSK_HEALTH, HUSK_DAMAGE);
		
		return true;
	}
	@Override
	protected void roundEnd() {
		Destroy();
	}
	@Override
	protected void death() {
		Destroy();
	}
	@Override
	protected void destroy() {
		Destroy();
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
		return (getWrapper().getPlayer().equals(e.GetDamager()) || (cemetery != null && cemetery.GetHusks().contains(e.GetDamager())))
				&& e.GetCause() == DamageCause.ENTITY_ATTACK;
	}
	private void Tornado(Location from) {
		UtilParticle.Vortex(from, PRIMARY_RANGE, 25, 2, 5, Particle.REDSTONE, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(191, 150, 61), 1));
		for(LivingEntity entity : UtilEntity.GetNearbyLivingEntities(from, PRIMARY_RANGE)) {
			if(entity instanceof Player && Main.getInstance().inSameTeam((Player)entity, getWrapper().getPlayer())) continue;
			if(cemetery != null && cemetery.GetHusks().contains(entity)) continue;
			
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
	private void Destroy() {
		if(cemetery != null) {
			cemetery.Remove();
		}
	}
	
	//
	
	private class Cemetery implements Listener{
		private final ArrayList<Location> spawnable = new ArrayList<>();
		private final Location center;
		private final double range;
		private final int maxHuskCount;
		private final double huskHealth;
		private final double huskDamage;
		private ArrayList<org.bukkit.entity.Husk> husks = new ArrayList<>();
		
		//
		
		public Cemetery(Location center, double range, int maxHuskCount, double huskHealth, double huskDamage) {
			UtilEvent.Register(Main.getInstance(), this);
			
			this.center = center;
			this.range = range;
			this.maxHuskCount = maxHuskCount;
			this.huskHealth = huskHealth;
			this.huskDamage = huskDamage;
			Location start = center.clone().subtract(range, 0, range);
			
			for(int x = start.getBlockX() ; x <= center.getBlockX() + range ; x++) {
				for(int z = start.getBlockZ() ; z <= center.getBlockZ() + range ; z++) {
					Location spawnLocation = new Location(start.getWorld(), x, start.getY(), z);

					if(center.distanceSquared(spawnLocation) > range * range) continue;
					if(spawnLocation.getWorld().getBlockAt(spawnLocation).isSolid()) continue;
					if(GetHighestBlockUnderLocation(spawnLocation, range).isEmpty()) continue;
					
					spawnable.add(spawnLocation.add(0.5, 0, 0.5));
				}
			}

			for(int i = 0 ; i < maxHuskCount ; i++) {
				Summon();
			}
		}
		
		//
		
		public void Remove() {
			husks.forEach( husk -> husk.remove());
			UtilEvent.Unregister(this);
		}
		public ArrayList<org.bukkit.entity.Husk> GetHusks() {
			return husks;
		}
		
		//
		
		@EventHandler
		public void Tick(TickElapsedEvent e) {
			UtilParticle.Vortex(center, range, 50, 1, 2, Particle.SOUL, 1);
			
			if(husks.size() >= maxHuskCount) return;
			Summon();
		}
		@EventHandler
		public void OnDeath(EntityDeathEvent e) {
			if(!husks.contains(e.getEntity())) return;

			e.setDroppedExp(0);
			e.getDrops().clear();
			husks.remove(e.getEntity());
		}
		@EventHandler
		public void OnDamage(LivingEntityDamageEvent e) {
			if(husks.contains(e.GetDamaged()) && e.GetCause() == DamageCause.FALL) e.SetCancelled(true);
		}
		
		//
		
		private void Summon() {
			if(spawnable.size() == 0) return;
			
			int chosen = (int)Math.floor(Math.random() * spawnable.size());
			org.bukkit.entity.Husk husk = (org.bukkit.entity.Husk)center.getWorld().spawnEntity(new Location(getWrapper().getPlayer().getWorld(), 0, 0, 0), EntityType.HUSK, SpawnReason.CUSTOM);
			husk.customName(ComponentMaker.getComponent(getDisguise().getWatcher().getCustomName()));
			husk.setSilent(true);
			UtilMobAi.clearBrain(husk);
			husk.setAdult();
			husk.getEquipment().clear();
			husk.getEquipment().setItemInMainHand(new ItemStack(Material.ROTTEN_FLESH), true);
			husk.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(huskHealth);
			husk.setHealth(huskHealth);
			husk.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(huskDamage);
			husk.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
			
			husk.teleport(spawnable.get(chosen));
			husks.add(husk);
		}
		private Block GetHighestBlockUnderLocation(Location from, double maxHeight) {
			Location fromClone = from.clone();
			double minY = fromClone.getY() - maxHeight;
			
			while(fromClone.getY() >= minY && !fromClone.getBlock().getType().isSolid() && fromClone.getBlock().getType()!=Material.LAVA) 
				fromClone.subtract(0, 1, 0);
			
			return fromClone.getBlock();
		}
	}
}
