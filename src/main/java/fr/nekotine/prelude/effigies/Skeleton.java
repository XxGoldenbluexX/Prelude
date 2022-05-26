package fr.nekotine.prelude.effigies;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import net.minecraft.world.entity.Entity;

public class Skeleton extends Effigy{
	private final int PRIMARY_COOLDOWN = 1;
	private final int SECONDARY_COOLDOWN = 1;
	
	private final float ARROW_DAMAGE = 2;
	private final float DAMAGE_MULTIPLYER = 2;
	private final float SECONDARY_DAMAGE = 2;
	
	private boolean multiplyer_active = false;
	private Projectile arrow;
	private Entity head = null;
	public Skeleton(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}

	@Override
	protected void castPrimarySpell() {
	}

	@Override
	protected void castSecondarySpell() {
	}

	@Override
	protected void roundEnd() {
	}
	
	@Override
	protected void death() {
	}
	
	@EventHandler
	public void onArrow(ProjectileHitEvent e) {
		if(e.getHitEntity() != null && e.getHitEntity() instanceof Player) {
			PlayerWrapper hit = Main.getInstance().getWrapper((Player)e.getHitEntity());
		}
		
	}
}
