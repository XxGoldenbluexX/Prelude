package fr.nekotine.prelude.effigies;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.nekotine.core.damage.DamageManager;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.itemcharge.ISwordCharge;
import fr.nekotine.core.itemcharge.SwordChargeManager;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.projectile.ProjectileManager;
import fr.nekotine.core.util.CustomAction;
import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.core.util.UtilMobAi;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Slime extends Effigy implements ISwordCharge, IProjectile{
	public Slime(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}
	
	//
	
	private boolean canUsePassive = false;
	private static final double PASSIVE_PROBABILITY = 0.5;
	
	private static final int PRIMARY_COOLDOWN = 5 * 20;
	private static final int SECONDARY_COOLDOWN = 9 * 20;
	
	private static final long PRIMARY_CHARGE_DURATION = 2 * 1000;
	private static final long PRIMARY_MAX_SIZE = 3;
	private static final double PRIMARY_DAMAGE = 0.5 * 2;
	private static final long PRIMARY_AUDIO_BIP = PRIMARY_MAX_SIZE - 1;
	
	private static final double SPAWN_OFFSET = 2;
	private static final double HEAL_AMOUNT = 0.5 * 2;
	private static final float JUMP_VELOCITY = 1;
	private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 2, 1, false, false, true);
	
	//
	
	private final HealSlimeManager slimes = new HealSlimeManager(this);
	
	//
	
	@EventHandler
	public void onDamage(LivingEntityDamageEvent e) {
		if(!canUsePassive) return;
		if(!e.IsCancelled() && e.GetDamage() > 0 && e.GetDamaged().equals(getWrapper().getPlayer()) && Math.random() >= PASSIVE_PROBABILITY) 
			slimes.summon(e.GetDamaged().getLocation(), true);
	}	
	@EventHandler
	public void entityDamage(LivingEntityDamageEvent e) {
		if(getWrapper().getPlayer().equals(e.GetDamager()) && e.GetCause()==DamageCause.ENTITY_ATTACK && !UtilEntity.IsOnGround(e.GetDamager())) {
			e.GetDamaged().addPotionEffect(SLOW);
		}else if(e.GetCause() == DamageCause.ENTITY_ATTACK && e.GetDamager()!=null && Main.getInstance().getModuleManager().Get(ProjectileManager.class).Exist(e.GetDamager())) {
			e.SetCancelled(true);
		}
	}
	
	@Override
	protected void castPrimarySpell() {
		setAbilityLocked(Ability.PRIMARY, true);
		Main.getInstance().getModuleManager().Get(SwordChargeManager.class).AddSwordCharge(
				getWrapper().getPlayer(),
				"SlimePrimary",
				PRIMARY_CHARGE_DURATION,
				true,
				true,
				true,
				PRIMARY_AUDIO_BIP,
				CustomAction.RIGHT_CLICK,
				false,
				null,
				this);
	}
	@Override
	protected void castSecondarySpell() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getVelocity().setY(JUMP_VELOCITY));
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		getWrapper().getPlayer().playSound(getWrapper().getPlayer(), Sound.ENTITY_SLIME_JUMP, 1, 0);
	}
	@Override
	protected void roundEnd() {
		slimes.remove();
	}
	@Override
	protected void death() {
		slimes.remove();
	}
	@Override
	protected void roundStart() {
		canUsePassive = true;
	}
	
	//

	@Override
	public void Ended(Player arg0, String arg1) {
	}
	@Override
	public void Released(Player player, String arg1, long left) {
		int size = 1 + (int)Math.floor( ((double)( PRIMARY_CHARGE_DURATION - left) / PRIMARY_CHARGE_DURATION) * (PRIMARY_MAX_SIZE-1) );
		org.bukkit.entity.Slime slime = UtilEntity.SpawnSlime(getWrapper().getPlayer().getLocation().add(0, 2, 0), SpawnReason.CUSTOM, size);
		slime.setCollidable(false);
		slime.setInvulnerable(true);
		UtilMobAi.clearBrain(slime);
		
		getWrapper().getPlayer().playSound(getWrapper().getPlayer(), Sound.ENTITY_SLIME_JUMP, 1, 0);
		
		LivingEntity[] self = {player};
		Material[] barrier = {Material.BARRIER};
		Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
				slime,
				player,
				this,
				player.getEyeLocation().getDirection().multiply(size / 2f),
				10 * 1000,
				true,
				true,
				self,
				barrier);
		
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		setAbilityLocked(Ability.PRIMARY, false);
	}
	
	//
	
	@Override
	public void Faded(CustomProjectile proj) {
		proj.GetProjectile().remove();
	}
	@Override
	public void Triggered(CustomProjectile proj) {
	}
	@Override
	public void Hit(LivingEntity hitE, Block hitB, CustomProjectile proj) {
		if(getWrapper().getPlayer().equals(hitE)) {
			proj.SetCancelled(true);
			return;
		}
		
		getWrapper().getPlayer().playSound(getWrapper().getPlayer(), Sound.ENTITY_SLIME_ATTACK, 1, 0);
		
		if(hitE != null) {
			Main.getInstance().getModuleManager().Get(DamageManager.class).Damage(hitE, getWrapper().getPlayer(), null, DamageCause.PROJECTILE, PRIMARY_DAMAGE, false, true, null);
		}
		Location spawnLoc = proj.GetProjectile().getLocation();
		proj.GetProjectile().remove();
		slimes.summon(spawnLoc, false);
	}
	
	//
	
	private class HealSlimeManager implements IProjectile{
		private final Slime effigy;
		
		private HealSlimeManager(Slime effigy) {
			this.effigy=effigy;
		}
		
		public void summon(Location location, boolean offset) {
			if(offset) {
				Double invertX = Math.random();
				Double invertZ = Math.random();
				Double offsetX = Math.random()*SPAWN_OFFSET;
				Double offsetZ = Math.random()*SPAWN_OFFSET;
				if(invertX<0.5) offsetX=-offsetX;
				if(invertZ<0.5) offsetZ=-offsetZ;
				location.add(offsetX, 0, offsetZ);
			}
			while (location.getBlock().getBoundingBox().contains(location.toVector())) {
				location.add(0, 0.5, 0);
			}

			org.bukkit.entity.Slime slime = UtilEntity.SpawnSlime(location, SpawnReason.CUSTOM, 1);
			slime.setInvulnerable(true);
			slime.setCollidable(false);
			UtilMobAi.clearBrain(slime);
			
			Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
					slime,
					effigy.getWrapper().getPlayer(),
					this,
					new Vector(0, 0, 0),
					-1,
					true,
					false);
		}
		
		public void remove() {
			Main.getInstance().getModuleManager().Get(ProjectileManager.class).TriggerFromInterface(this);
		}
		
		@Override
		public void Faded(CustomProjectile proj) {
			proj.GetProjectile().remove();
		}
		@Override
		public void Triggered(CustomProjectile proj) {
			proj.GetProjectile().remove();
		}
		@Override
		public void Hit(LivingEntity hitE, Block arg1, CustomProjectile proj) {
			if(effigy.getWrapper().getPlayer().equals(hitE)) {
				if(hitE.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()-hitE.getHealth()>=HEAL_AMOUNT) {
					hitE.setHealth(hitE.getHealth() + HEAL_AMOUNT);
					proj.GetProjectile().remove();
				}else {
					proj.SetCancelled(true);
				}
			}else {
				proj.SetCancelled(true);
			}
		}
	}
}