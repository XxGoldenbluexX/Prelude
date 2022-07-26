package fr.nekotine.prelude.ai;

import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

public class WitchBatFollowGoal implements Goal<Bat>{

	private final GoalKey<Bat> key;
	
	private final Bat bat;
	
	private final Player _followed;
	
	private final double _angle;
	
	/**
	 * Création du Goal
	 * @param plugin
	 * @param following Chauve souris qui suit le joueur
	 * @param followed Le joueur qui est suivi par la chauve souris
	 * @param angle L'angle de la chauve souris en radian. Les chauves souris sont disposées en cercle autour de la tête du joueur (0.5 block au dessus)
	 */
	public WitchBatFollowGoal(Plugin plugin, Bat following, Player followed, double angle) {
		key = GoalKey.of(Bat.class, new NamespacedKey(plugin, "with_bat_follow"));
		bat = following;
		_followed = followed;
		_angle = angle;
	}
	
	@Override
	public boolean shouldActivate() {
		return true;
	}
	
	@Override
	public boolean shouldStayActive() {
		return shouldActivate() && _followed.isValid();
	}

	@Override
	public void tick() {
		Vector followedToTargetLoc = new Vector(Math.cos(_angle),2.5,Math.sin(_angle));
		Location targetLocation = _followed.getLocation().clone().add(followedToTargetLoc);
		/* Le pathfinding prend trop de temps, c'est un problème si le joueur bouge
		targetLocation.getWorld().spawnParticle(Particle.COMPOSTER, targetLocation, 1);
		bat.getPathfinder().moveTo(targetLocation, 1.5D);
		Alternative: */
		bat.teleport(targetLocation, TeleportCause.PLUGIN);
	}
	
	@Override
	public void stop() {
		bat.getPathfinder().stopPathfinding();
	}
	
	@Override
	public @NotNull GoalKey<Bat> getKey() {
		return key;
	}

	@Override
	public @NotNull EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.MOVE);
	}
	
}
