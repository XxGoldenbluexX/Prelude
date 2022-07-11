package fr.nekotine.prelude.effigies;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import fr.nekotine.core.bowcharge.IBowCharge;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.projectile.ProjectileManager;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Skeleton extends Effigy implements IBowCharge, IProjectile{
	private final int PRIMARY_COOLDOWN = 1;
	private final int SECONDARY_COOLDOWN = 1;
	
	private final float ARROW_DAMAGE = 2;
	private final float DAMAGE_MULTIPLYER = 2;
	private final float SECONDARY_DAMAGE = 2;
	
	private boolean multiplyer_active = false;
	
	public Skeleton(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}

	@Override
	protected void castPrimarySpell() {
		multiplyer_active = true;
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
	}

	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		WitherSkull head = (WitherSkull)getWrapper().getPlayer().getWorld().spawnEntity(getWrapper().getPlayer().getEyeLocation(), EntityType.WITHER_SKULL, SpawnReason.CUSTOM);
		head.setDirection(getWrapper().getPlayer().getEyeLocation().getDirection());
		head.setCharged(true);
		head.setInvulnerable(true);
		head.setShooter(getWrapper().getPlayer());

		Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
				head, 
				getWrapper().getPlayer(), 
				this, 
				new Vector(0, 0, 0),
				10 * 1000, 
				true,
				true);
	}

	@Override
	protected void roundEnd() {
	}
	
	@Override
	protected void death() {
	}
	
	@EventHandler
	public void onArrow(LivingEntityDamageEvent e) {
		if(!getWrapper().getPlayer().equals(e.GetDamager())) return;
		if(e.GetProjectile() == null) return;
		if(e.GetProjectile().getType() != EntityType.ARROW) return;
		
		e.SetDamage(ARROW_DAMAGE);
		if(multiplyer_active) {
			e.AddFinalMult(DAMAGE_MULTIPLYER);
			multiplyer_active = false;
		}
		
	}

	@Override
	public void Ended(Player player, String chargeName) {
	}
	@Override
	public void Released(Player player, String chargeName, long left, Arrow arrow) {
	}
	@Override
	public void Cancelled(Player player, String chargeName, long left) {
	}

	@Override
	public void Hit(LivingEntity hitEntity, Block hitBlock, CustomProjectile projectile) {
		
		//explosion
		
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
}
