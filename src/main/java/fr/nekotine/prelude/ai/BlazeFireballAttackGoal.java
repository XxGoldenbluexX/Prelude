package fr.nekotine.prelude.ai;

import java.util.EnumSet;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

public class BlazeFireballAttackGoal implements Goal<Mob>{

	private final GoalKey<Mob> key;
	
	private final Mob _mob;
	
	private final int _cooldown;
	
	private final int _salveDelay;
	
	private final int _salveSize;
	
	private final Vector _launchOffset;
	
	private final double _fireballSpeed;
	
	private int salveDelayCountdown = 0;
	
	private int salveSizeCountDown = 0;
	
	private int cooldownCountdown = 0;
	
	public BlazeFireballAttackGoal(Plugin plugin, Mob shooter, int cooldown, int salveDelay, int salveSize, Vector launchOffset, double fireballSpeed) {
		key = GoalKey.of(Mob.class, new NamespacedKey(plugin, "blaze_fireball_attack"));
		_mob = shooter;
		_cooldown = cooldown;
		_salveDelay = salveDelay;
		_salveSize = salveSize;
		_launchOffset = launchOffset;
		_fireballSpeed = fireballSpeed;
	}
	
	@Override
	public void start() {
		salveDelayCountdown = _salveDelay;
		salveSizeCountDown = _salveSize;
	}
	
	@Override
	public void tick() {
		if (salveDelayCountdown > 0) {
			salveDelayCountdown--;
		}else {
			if (salveSizeCountDown > 0) {
				SmallFireball fireball = _mob.getLocation().getWorld().spawn(_mob.getLocation().clone().add(_launchOffset),
						SmallFireball.class, SpawnReason.CUSTOM);
				fireball.setDirection(_mob.getTarget().getLocation().clone().toVector().add(_mob.getTarget().getVelocity().setY(1)).subtract(
						_mob.getLocation().clone().toVector())
						.normalize().multiply(_fireballSpeed));
				fireball.setIsIncendiary(false);
				salveSizeCountDown--;
				// voir _mob.launchProjectile si le blaze se touche tt seul
			}else {
				cooldownCountdown = _cooldown;
			}
		}
	}
	
	@Override
	public void stop() {
		_mob.setTarget(null);
	}
	
	@Override
	public boolean shouldActivate() {
		if (cooldownCountdown > 0) {
			cooldownCountdown--;
			return false;
		}
		return _mob.isValid() && _mob.getTarget() != null && _mob.getTarget().isValid();
	}

	@Override
	public @NotNull GoalKey<Mob> getKey() {
		return key;
	}

	@Override
	public @NotNull EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
	}

}
