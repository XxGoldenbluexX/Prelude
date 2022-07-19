package fr.nekotine.prelude.effigies;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.core.bowcharge.BowChargeManager;
import fr.nekotine.core.bowcharge.IBowCharge;
import fr.nekotine.core.damage.DamageManager;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.projectile.ProjectileManager;
import fr.nekotine.core.usable.Usable;
import fr.nekotine.core.usable.UsableManager;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Skeleton extends Effigy implements IBowCharge, IProjectile{
	private static final int PRIMARY_COOLDOWN = 4 * 20;
	private static final int SECONDARY_COOLDOWN = 10 * 20;
	
	private static final float ARROW_DAMAGE = 2;
	private static final long PASSIVE_CHARGE_TIME = 2500;
	private static final int PASSIVE_BONUS_ARROWS = 3;
	private static final int PASSIVE_SPREAD = 6;
	private static final long PASSIVE_AUDIO_BIP = PASSIVE_BONUS_ARROWS - 1;
	
	private static final float DAMAGE_MULTIPLYER = 2;
	
	private static final float SECONDARY_SPEED = 1.5f;
	private static final float SECONDARY_DAMAGE = 1;
	private static final double SECONDARY_RADIUS = 3;
	
	private static final Consumer<PlayerDropItemEvent> CANCEL_DROP_EVENT = new Consumer<PlayerDropItemEvent>() {
		@Override
		public void accept(PlayerDropItemEvent e) {
			e.setCancelled(true);
		}
	};
	private static final Consumer<EntityShootBowEvent> CANCEL_ARROW_CONSUMPTION = new Consumer<EntityShootBowEvent>() {
		@Override
		public void accept(EntityShootBowEvent e) {
			e.setConsumeItem(false);
		}
	};
	
	private boolean multiplyer_active = false;
	
	private final Usable bow;
	private final Usable arrow;
	
	//
	
	public Skeleton(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		this.bow = Main.getInstance().getModuleManager().Get(UsableManager.class).AddUsable(
				new ItemStack(Material.BOW), 
				getWrapper().getPlayer().getInventory());
		this.bow.SetUnbreakable(true);
		this.bow.HideUnbreakable(true);
		this.bow.OnDrop(CANCEL_DROP_EVENT);
		this.bow.SetEnchantedText(false);
		this.bow.OnBowShoot(CANCEL_ARROW_CONSUMPTION);
		
		this.arrow = Main.getInstance().getModuleManager().Get(UsableManager.class).AddUsable(
				new ItemStack(Material.ARROW), 
				getWrapper().getPlayer().getInventory());
		this.arrow.OnDrop(CANCEL_DROP_EVENT);
		
		this.bow.Give();
		this.arrow.Give();
		
		addCharge();
	}
	
	//

	@Override
	protected void castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		multiplyer_active = true;
		this.bow.SetEnchantedGlow(true);
		
	}
	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		WitherSkull head = (WitherSkull)getWrapper().getPlayer().getWorld().spawnEntity(getWrapper().getPlayer().getEyeLocation(), EntityType.WITHER_SKULL, SpawnReason.CUSTOM);
		head.setDirection(getWrapper().getPlayer().getEyeLocation().getDirection());
		head.setCharged(false);
		head.setInvulnerable(true);
		head.setShooter(getWrapper().getPlayer());

		Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
				head, 
				getWrapper().getPlayer(), 
				this, 
				getWrapper().getPlayer().getEyeLocation().getDirection().multiply(SECONDARY_SPEED),
				10 * 1000, 
				true,
				true);
	}
	@Override
	protected void roundEnd() {
		this.bow.Remove();
		this.arrow.Remove();
	}
	@Override
	protected void death() {
	}
	
	//
	
	@EventHandler
	public void onArrow(LivingEntityDamageEvent e) {
		if(!getWrapper().getPlayer().equals(e.GetDamager())) return;
		if(e.GetProjectile() == null) return;
		if(e.GetProjectile().getType() != EntityType.ARROW) return;

		e.SetDamage(ARROW_DAMAGE);
		if(multiplyer_active) {
			e.AddFinalMult(DAMAGE_MULTIPLYER);
			multiplyer_active = false;
			this.bow.SetEnchantedGlow(false);
		}
	}
	
	//
	
	@Override
	public void Ended(Player player, String chargeName) {
	}
	@Override
	public void Released(Player player, String chargeName, long left, Arrow arrow) {
		int nbArrows = (int)Math.floor( (((double)( PASSIVE_CHARGE_TIME - left)) / PASSIVE_CHARGE_TIME) * (PASSIVE_BONUS_ARROWS));
		for(int i=0 ; i < nbArrows ; i++) {
			Arrow bonusArrow = arrow.getWorld().spawnArrow(arrow.getLocation(), arrow.getVelocity(), (float)arrow.getVelocity().length(), PASSIVE_SPREAD);
			bonusArrow.setShooter(player);
		}
		
		addCharge();
	}
	@Override
	public void Cancelled(Player player, String chargeName, long left) {
		addCharge();
	}
	
	//

	@Override
	public void Hit(LivingEntity hitEntity, Block hitBlock, CustomProjectile projectile) {
		if(hitEntity != null && hitEntity.equals(getWrapper().getPlayer())) {
			projectile.SetCancelled(true);
			return;
		}
		projectile.GetProjectile().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, projectile.GetProjectile().getLocation(), 1);
		projectile.GetProjectile().getWorld().playSound(projectile.GetProjectile().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 3, 0);
		Main.getInstance().getModuleManager().Get(DamageManager.class).Explode(
				getWrapper().getPlayer(), 
				SECONDARY_RADIUS, 
				DamageCause.CUSTOM, 
				SECONDARY_DAMAGE, 
				true, 
				false, 
				projectile.GetProjectile().getLocation(), 
				false);
		
		projectile.GetProjectile().remove();
	}
	@Override
	public void Faded(CustomProjectile projectile) {
		projectile.GetProjectile().remove();
	}
	@Override
	public void Triggered(CustomProjectile projectile) {
		projectile.GetProjectile().remove();
	}
	
	//
	
	private void addCharge() {
		Main.getInstance().getModuleManager().Get(BowChargeManager.class).AddBowCharge(
			getWrapper().getPlayer(), 
			"SkeletonPassive", 
			PASSIVE_CHARGE_TIME, 
			false,
			true,
			true,
			PASSIVE_AUDIO_BIP,
			this);
	}
}
