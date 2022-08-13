package fr.nekotine.prelude.effigies;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
	
	private static final CustomEffect FREEZE_EFFECT = new CustomEffect(new FreezeEffect(), 0, 3 * 20);
	private static final CustomEffect INVULNERABLE_EFFECT = new CustomEffect(new InvulnerableEffect(), 0, FREEZE_EFFECT.getDuration());
	private static final PotionEffect SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, 1, 3 * 20);
	
	private static final double PASSIVE_SLOW_BONUS_DAMAGE = 1 * 2;
	private static final double PASSIVE_FREEZE_BONUS_DAMAGE = 2 * 2;
	
	private static final int PRIMARY_BLOCK_RANGE = 100;
	private static final int PRIMARY_HEIGHT = 4;
	//
	
	public SnowFox(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		((FoxWatcher)getDisguise().getWatcher()).setType(Type.SNOW);
	}
	
	//
	
	@Override
	protected boolean castPrimarySpell() {
		Block targeted = getWrapper().getPlayer().getTargetBlockExact(PRIMARY_BLOCK_RANGE);
		if(targeted == null) return false;
		
		Location start = targeted.getLocation();
		
		for(int i = 0 ; i < PRIMARY_HEIGHT ; i++) {
			start.add(0, 1, 0);
			
			if(start.getBlock().getType()==Material.AIR) start.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
			//poser le tempblock sur start
			
			GetEntitiesInBlock(start).stream()
				.filter(entity -> !Main.getInstance().getPlayersInTeam(getWrapper().getTeam()).contains(entity))
				.forEach(entity -> 
					{Main.getInstance().getCustomEffectModule().addEffect(entity, FREEZE_EFFECT);
					Main.getInstance().getCustomEffectModule().addEffect(entity, INVULNERABLE_EFFECT);
					entity.playEffect(EntityEffect.HURT);});
		}
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_FOX_AGGRO, 1, 0);
		getWrapper().getPlayer().getWorld().playSound(start, Sound.BLOCK_GLASS_PLACE, 1, 0);
		
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		return true;
	}
	@Override
	protected boolean castSecondarySpell() {
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
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_FOX_SCREECH, 1, 0);
		
		return true;
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
		if(/* has effect*/ true) {
			e.AddBaseMod(PASSIVE_FREEZE_BONUS_DAMAGE);
			Main.getInstance().getCustomEffectModule().removeEffect(e.GetDamaged(), FREEZE_EFFECT);
			Main.getInstance().getCustomEffectModule().removeEffect(e.GetDamaged(), INVULNERABLE_EFFECT);
		}
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
	
	//
	
	private Collection<LivingEntity> GetEntitiesInBlock(Location blockLocation){
		return blockLocation.clone().add(0.5, 0.5, 0.5).getNearbyLivingEntities(0.5f);
	}
}
