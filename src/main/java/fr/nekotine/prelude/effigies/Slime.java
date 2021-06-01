package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.PreludeMain;

public class Slime extends Effigy{
	private static final double spawnOffset=2;
	private static final int healAmout=1;
	private static final float jumpVelocity=1;
	private static final PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 2, 1, false, false, false);
	private final ArrayList<HealSlime> slimes = new ArrayList<>();
	public Slime(PlayerWrapper w) {
		super(w, 3000, 3000);
	}

	@Override
	protected void castSpell1() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getVelocity().add(new Vector(0, jumpVelocity, 0)));
	}

	@Override
	protected void castSpell2() {
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity().equals(getWrapper().getPlayer())) {
			new HealSlime(getWrapper());
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(e.getEntity().equals(getWrapper().getPlayer())) {
			for(HealSlime slime : slimes) {
				slime.destroy();
			}
		}
	}
	
	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager().equals(getWrapper().getPlayer()) 
			&& e.getCause()==DamageCause.ENTITY_ATTACK 
			&& e.getEntity() instanceof Player
			&& !getWrapper().getPlayer().getLocation().subtract(0, 1, 0).getBlock().isSolid()) {
			Player hit = (Player)e.getEntity();
			hit.addPotionEffect(slow);
		}
	}
	private static class HealSlime implements Listener{
		private final org.bukkit.entity.Slime slime;
		private final PlayerWrapper p;
		
		private HealSlime(PlayerWrapper p) {
			Bukkit.getPluginManager().registerEvents(this, PreludeMain.main);
			this.p=p;
			Double invertX = Math.random();
			Double invertZ = Math.random();
			Double offsetX = Math.random()*spawnOffset;
			Double offsetZ = Math.random()*spawnOffset;
			if(invertX<0.5) offsetX=-offsetX;
			if(invertZ<0.5) offsetZ=-offsetZ;
			Location slimeLocation = p.getPlayer().getLocation().add(offsetX, 0, offsetZ);
			while (slimeLocation.getBlock().getBoundingBox().contains(slimeLocation.toVector())) {
				slimeLocation.add(0, 0.5, 0);
			}
			slime = (org.bukkit.entity.Slime)p.getPlayer().getWorld().spawnEntity(slimeLocation, EntityType.SLIME, SpawnReason.SLIME_SPLIT);
			slime.setInvulnerable(true);
			slime.setSize(1);
			slime.setAI(false);
			slime.setCollidable(false);
		}
		
		@EventHandler
		public void playerMoveEvent(PlayerMoveEvent e) {
			if(e.getPlayer().equals(p.getPlayer())) {
				if(e.getTo().distanceSquared(slime.getLocation())<=1 
				&& p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()-p.getPlayer().getHealth()>=healAmout) {
					p.getPlayer().setHealth(p.getPlayer().getHealth()+healAmout);
					destroy();
				}
			}
		}
		
		public void destroy() {
			HandlerList.unregisterAll(this);
			((fr.nekotine.prelude.effigies.Slime)p.getEffigy()).slimes.remove(this);
			slime.remove();
		}
	}
}
