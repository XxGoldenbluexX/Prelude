package fr.nekotine.prelude.ai;

import java.util.EnumSet;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

import fr.nekotine.core.util.UtilEntity;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Team;

public class TargetNearestEnemiePlayersGoal implements Goal<Mob> {

	private final GoalKey<Mob> key;
	
	private final Mob _mob;
	
	private final Team _team;
	
	private final double _targetRange;
	
	public TargetNearestEnemiePlayersGoal(Plugin plugin, Mob mob, Team team, double targetRange) {
		key = GoalKey.of(Mob.class, new NamespacedKey(plugin, "target_nearest_enemie_player"));
		_mob = mob;
		_team = team;
		_targetRange = targetRange;
	}
	
	@Override
	public void tick() {
		double nearestDist = -1;
		Player nearest = null;
		for (LivingEntity e : UtilEntity.GetNearbyLivingEntities(_mob.getLocation(), _targetRange)) {
			if (e.isValid() && e.getType() == EntityType.PLAYER && e instanceof Player) {
				Player p = (Player) e;
				PlayerWrapper wrapper = Main.getInstance().getWrapper(p);
				if (wrapper != null && wrapper.getTeam() != _team) {
					if (nearest == null) {
						nearest = p;
						nearestDist = _mob.getLocation().distanceSquared(p.getLocation());
					}else {
						double near = _mob.getLocation().distanceSquared(p.getLocation());
						if (near < nearestDist){
							nearest = p;
							nearestDist = _mob.getLocation().distanceSquared(p.getLocation());
						}
					}
				}
			}
		}
		_mob.setTarget(nearest);
	}
	
	@Override
	public boolean shouldActivate() {
		return true;
	}

	@Override
	public @NotNull GoalKey<Mob> getKey() {
		return key;
	}

	@Override
	public @NotNull EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.TARGET);
	}

}
