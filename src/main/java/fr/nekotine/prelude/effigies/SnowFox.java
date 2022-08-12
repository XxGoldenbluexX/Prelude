package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Fox.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.effect.CustomEffect;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.util.UtilMath;
import fr.nekotine.core.util.UtilMobAi;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;
import fr.nekotine.prelude.utils.FreezeEffect;
import fr.nekotine.prelude.utils.InvulnerableEffect;
import me.libraryaddict.disguise.disguisetypes.watchers.FoxWatcher;

public class SnowFox extends Effigy implements IProjectile{
	private static final int PRIMARY_COOLDOWN = 12 * 20;
	private static final int SECONDARY_COOLDOWN = 7 * 20;
	
	private static final CustomEffect FREEZE_EFFECT = new CustomEffect(new FreezeEffect(), 0, 2 * 20);
	private static final CustomEffect INVULNERABLE_EFFECT = new CustomEffect(new InvulnerableEffect(), 0, FREEZE_EFFECT.getDuration());
	private static final PotionEffect SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, 1, 2 * 20);
	
	private static final double PASSIVE_SLOW_BONUS_DAMAGE = 1 * 2;
	private static final double PASSIVE_FREEZE_BONUS_DAMAGE = 2 * 2;
	
	private static final int PRIMARY_BLOCK_RANGE = 100;
	//
	
	public SnowFox(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		((FoxWatcher)getDisguise().getWatcher()).setType(Type.SNOW);
	}
	
	//
	
	@Override
	protected void castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		Block targeted = getWrapper().getPlayer().getTargetBlockExact(PRIMARY_BLOCK_RANGE);
	}
	@Override
	protected void castSecondarySpell() {
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		Fox baby = (Fox)getWrapper().getPlayer().getWorld().spawnEntity(new Location(getWrapper().getPlayer().getWorld(), 0, 0, 0), EntityType.FOX, SpawnReason.CUSTOM);
		baby.setBaby();
		baby.setInvulnerable(true);
		baby.setCollidable(false);
		baby.setFoxType(Type.SNOW);
		UtilMobAi.clearBrain(baby);
		
		baby.teleport(getWrapper().getPlayer().getEyeLocation());
		
		ArrayList<Player> inTeam = Main.getInstance().getPlayersInTeam(getWrapper().getTeam());
		Main.getInstance().getProjectileModule().AddProjectile(
				baby, 
				getWrapper().getPlayer(), 
				this, 
				getWrapper().getPlayer().getEyeLocation().getDirection(), 
				10 * 1000, 
				true, 
				false, 
				inTeam.toArray(new LivingEntity[inTeam.size()]), 
				null);
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
		if(!getWrapper().getPlayer().equals(e.GetDamager())) return;
		
		if(e.GetDamaged().hasPotionEffect(SLOW_EFFECT.getType())) {
			e.AddBaseMod(PASSIVE_SLOW_BONUS_DAMAGE);
		}
		
		//Freeze effect
	}

	//
	
	@Override
	public void Faded(CustomProjectile proj) {
		proj.GetProjectile().remove();
	}
	@Override
	public void Hit(LivingEntity hitE, Block arg1, CustomProjectile proj) {
		hitE.playEffect(EntityEffect.HURT);
		hitE.addPotionEffect(SLOW_EFFECT);
		getWrapper().getPlayer().setVelocity(UtilMath.GetTrajectory3d(getWrapper().getPlayer().getLocation(), hitE.getLocation()));
		proj.GetProjectile().remove();
	}
	@Override
	public void Triggered(CustomProjectile arg0) {
	}
}
