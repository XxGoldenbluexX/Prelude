package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.EventRegisterer;

public class Slime extends Effigy{
	public Slime(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}
	private static final int PRIMARY_COOLDOWN = 300;
	
	private static final double SPAWN_OFFSET=2;
	private static final int HEAL_AMOUNT=1;
	private static final float JUMP_VELOCITY=1;
	private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 2, 1, false, false, false);
	
	private final ArrayList<HealSlime> slimes = new ArrayList<>();
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity().equals(getWrapper().getPlayer())) {
			new HealSlime(this);
		}
	}	
	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager().equals(getWrapper().getPlayer()) 
			&& e.getCause()==DamageCause.ENTITY_ATTACK 
			&& e.getEntity() instanceof Player
			&& !getWrapper().getPlayer().getLocation().subtract(0, 1, 0).getBlock().isSolid()) {
			Player hit = (Player)e.getEntity();
			hit.addPotionEffect(SLOW);
		}
	}
	
	@Override
	protected void castPrimarySpell() {
	}
	@Override
	protected void castSecondarySpell() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getVelocity().setY(JUMP_VELOCITY));
		setCooldown(Ability.SECONDARY, PRIMARY_COOLDOWN);
	}
	@Override
	protected void roundEnd() {
		for(HealSlime slime : slimes) {
			slime.destroyWithoutRemove();
		}
		slimes.clear();
	}
	@Override
	protected void death() {
		for(HealSlime slime : slimes) {
			slime.destroyWithoutRemove();
		}
		slimes.clear();
	}
	
	public void removeHealSlime(HealSlime slime) {
		slimes.remove(slime);
	}
	
	private static class HealSlime implements Listener{
		private final org.bukkit.entity.Slime slime;
		private final Slime effigy;
		
		private HealSlime(Slime effigy) {
			this.effigy=effigy;
			
			Double invertX = Math.random();
			Double invertZ = Math.random();
			Double offsetX = Math.random()*SPAWN_OFFSET;
			Double offsetZ = Math.random()*SPAWN_OFFSET;
			if(invertX<0.5) offsetX=-offsetX;
			if(invertZ<0.5) offsetZ=-offsetZ;
			Location slimeLocation = effigy.getWrapper().getPlayer().getLocation().add(offsetX, 0, offsetZ);
			while (slimeLocation.getBlock().getBoundingBox().contains(slimeLocation.toVector())) {
				slimeLocation.add(0, 0.5, 0);
			}
			
			slime = (org.bukkit.entity.Slime)effigy.getWrapper().getPlayer().getWorld().spawnEntity(slimeLocation, EntityType.SLIME, SpawnReason.SLIME_SPLIT);
			slime.setInvulnerable(true);
			slime.setSize(1);
			slime.setAI(false);
			slime.setCollidable(false);
			
			EventRegisterer.registerEvent(this);
		}
		
		@EventHandler
		public void playerMoveEvent(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			if(p.equals(effigy.getWrapper().getPlayer())) {
				if(e.getTo().distanceSquared(slime.getLocation())<=1 
				&& p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()-p.getHealth()>=HEAL_AMOUNT) {
					
					p.setHealth(effigy.getWrapper().getPlayer().getHealth()+HEAL_AMOUNT);
					destroy();
				}
			}
		}
		
		public void destroyWithoutRemove() {
			slime.remove();
			EventRegisterer.unregisterEvent(this);
		}
		private void destroy() {
			slime.remove();
			effigy.removeHealSlime(this);
			EventRegisterer.unregisterEvent(this);
		}
	}
}