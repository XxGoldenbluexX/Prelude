package fr.nekotine.prelude.effigies;

import org.bukkit.Location;
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
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Slime extends Effigy implements ISwordCharge, IProjectile{
	public Slime(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}
	private static final int PRIMARY_COOLDOWN = 300;
	private static final int SECONDARY_COOLDOWN = 300;
	
	private static final long PRIMARY_CHARGE_DURATION = 3 * 1000;
	private static final long PRIMARY_MAX_SIZE = 3;
	private static final double PRIMARY_DAMAGE_PER_SIZE = 2;
	
	private static final double SPAWN_OFFSET=2;
	private static final int HEAL_AMOUNT=1;
	private static final float JUMP_VELOCITY=1;
	private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 2, 1, false, false, false);
	
	
	private final HealSlimeManager slimes = new HealSlimeManager(this);
	
	@EventHandler
	public void onDamage(LivingEntityDamageEvent e) {
		if(e.GetDamaged().equals(getWrapper().getPlayer())) {
			slimes.summon(e.GetDamaged().getLocation(), true);
		}
	}	
	@EventHandler
	public void entityDamage(LivingEntityDamageEvent e) {
		if(getWrapper().getPlayer().equals(e.GetDamager())
			&& e.GetCause()==DamageCause.ENTITY_ATTACK
			&& !UtilEntity.IsOnSolidBlock(e.GetDamager())){
			e.GetDamaged().addPotionEffect(SLOW);
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
	public void Ended(Player arg0, String arg1) {
	}
	@Override
	public void Released(Player player, String arg1, long left) {
		int size = (int)Math.ceil( (( PRIMARY_CHARGE_DURATION - left) / PRIMARY_CHARGE_DURATION) * PRIMARY_MAX_SIZE );
		org.bukkit.entity.Slime slime = UtilEntity.SpawnSlime(getWrapper().getPlayer().getLocation(), SpawnReason.CUSTOM, size);
		
		Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
				slime,
				player,
				this,
				player.getEyeLocation().getDirection().multiply(1.5),
				10 * 1000,
				true,
				false);
		
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		setAbilityLocked(Ability.PRIMARY, false);
	}
	
	@Override
	public void Faded(CustomProjectile arg0) {
		System.out.println("faded");
	}
	@Override
	public void Triggered(CustomProjectile arg0) {
		System.out.println("trigger");
	}
	@Override
	public void Hit(LivingEntity hitE, Block hitB, CustomProjectile proj) {
		System.out.println("hit");
		if(hitE != null) {
			double damage = ((org.bukkit.entity.Slime)proj.GetProjectile()).getSize() * PRIMARY_DAMAGE_PER_SIZE;
			Main.getInstance().getModuleManager().Get(DamageManager.class).Damage(hitE, getWrapper().getPlayer(), null, DamageCause.PROJECTILE, damage, false, true);
		}
		slimes.summon(proj.GetProjectile().getLocation(), false);
		proj.GetProjectile().remove();
	}
	
	//
	
	private static class HealSlimeManager implements IProjectile{
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
			slime.setAI(false);
			slime.setCollidable(false);
			
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
			Main.getInstance().getModuleManager().Get(ProjectileManager.class).TriggerFromSender(effigy.getWrapper().getPlayer());
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
				}
			}else {
				proj.SetTriggered(false);
			}
		}
	}
}