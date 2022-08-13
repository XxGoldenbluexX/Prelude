package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.core.bowcharge.IBowCharge;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.usable.Usable;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Skeleton extends Effigy implements IBowCharge, IProjectile{
	private static final int PRIMARY_COOLDOWN = 4 * 20;
	private static final int SECONDARY_COOLDOWN = 10 * 20;
	
	private static final float ARROW_DAMAGE = 1 * 2;
	private static final long PASSIVE_CHARGE_TIME = 2500;
	private static final int PASSIVE_BONUS_ARROWS = 3;
	private static final int PASSIVE_SPREAD = 5;
	private static final long PASSIVE_AUDIO_BIP = PASSIVE_BONUS_ARROWS - 1;
	
	private static final float DAMAGE_MULTIPLYER = 2;
	
	private static final float SECONDARY_SPEED = 1.5f;
	private static final double SECONDARY_DAMAGE = 1 * 2;
	private static final double SECONDARY_RADIUS = 3;
	
	private boolean multiplyer_active = false;
	private boolean canShoot = false;
	
	private final Usable bow;
	private final Usable arrow;
	
	//
	
	public Skeleton(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		bow = new Usable(Main.getInstance().getUsableModule(), new ItemStack(Material.BOW)) {
			@Override
			protected void OnDrop(PlayerDropItemEvent e) {
				e.setCancelled(true);
			}
			@Override
			protected void OnBowShoot(EntityShootBowEvent e) {
				e.setConsumeItem(false);
			}
		};
		bow.SetUnbreakable(true);
		bow.HideUnbreakable(true);
		bow.SetEnchantedText(false);
		bow.SetName("Arc");
		bow.Give(getWrapper().getPlayer().getInventory());
		bow.register();
		bow.Give(getWrapper().getPlayer().getInventory());
		
		arrow = new Usable(Main.getInstance().getUsableModule(), new ItemStack(Material.ARROW)) {
			@Override
			protected void OnDrop(PlayerDropItemEvent e) {
				e.setCancelled(true);
			}
			@Override
			protected void OnBowShoot(EntityShootBowEvent e) {
				e.setConsumeItem(false);
			}
		};
		arrow.SetName("Fleche");
		arrow.register();
		arrow.Give(getWrapper().getPlayer().getInventory());
		
		addCharge();
	}
	
	//

	@Override
	protected boolean castPrimarySpell() {
		if(multiplyer_active) return false;
		
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		multiplyer_active = true;
		this.bow.SetEnchantedGlow(true);
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ITEM_CROSSBOW_QUICK_CHARGE_1, 1, 0);
		
		return true;
	}
	@Override
	protected boolean castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		Player player = getWrapper().getPlayer();
		WitherSkull head = player.launchProjectile(WitherSkull.class);
		head.setDirection(player.getEyeLocation().getDirection());
		head.setCharged(false);
		head.setInvulnerable(true);
		head.setShooter(player);

		Main.getInstance().getProjectileModule().AddProjectile(
				head, 
				player, 
				this, 
				player.getEyeLocation().getDirection().multiply(SECONDARY_SPEED),
				10 * 1000, 
				true,
				true);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_WITHER_SHOOT, 1, 0);
		
		return true;
	}
	@Override
	protected void roundEnd() {
	}
	@Override
	protected void death() {
	}
	@Override
	protected void destroy() {
		this.bow.Remove();
		this.arrow.Remove();
		bow.unregister();
		arrow.unregister();
		super.destroy();
	}
	@Override
	protected void roundStart() {
		canShoot = true;
	}
	
	//
	
	@EventHandler
	public void onArrow(LivingEntityDamageEvent e) {
		if(e.IsCancelled()) return;
		if(!getWrapper().getPlayer().equals(e.GetDamager())) return;
		if(e.GetProjectile() == null) return;
		if(e.GetProjectile().getType() != EntityType.ARROW) return;
		
		e.SetDamage(Math.min(e.GetDamage(), ARROW_DAMAGE));
		if(multiplyer_active) {
			e.AddFinalMult(DAMAGE_MULTIPLYER);
			multiplyer_active = false;
			this.bow.SetEnchantedGlow(false);
		}
	}
	@EventHandler
	public void OnHit(ProjectileHitEvent e) {
		if(!multiplyer_active) return;
		if(!getWrapper().getPlayer().equals( e.getEntity().getShooter()) ) return;
		if(!(e.getEntity() instanceof Arrow)) return;
		if(e.getHitBlock() == null) return;
		
		multiplyer_active = false;
		this.bow.SetEnchantedGlow(false);
	}
	@EventHandler
	public void OnShoot(EntityShootBowEvent e) {
		if(canShoot) return;
		if(!getWrapper().getPlayer().equals(e.getEntity())) return;
		e.setCancelled(true);
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
		ArrayList<Player> inTeam = Main.getInstance().getPlayersInTeam(getWrapper().getTeam());
		Main.getInstance().getDamageModule().Explode(
				getWrapper().getPlayer(), 
				SECONDARY_RADIUS, 
				DamageCause.CUSTOM, 
				SECONDARY_DAMAGE, 
				true, 
				false, 
				projectile.GetProjectile().getLocation(), 
				inTeam.toArray(new LivingEntity[inTeam.size()]));
		
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
		Main.getInstance().getBowChargeModule().AddBowCharge(
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
