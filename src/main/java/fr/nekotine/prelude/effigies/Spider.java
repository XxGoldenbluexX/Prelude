package fr.nekotine.prelude.effigies;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Spider extends Effigy{
	
	private boolean canClimb = false;
	private final int levitationDuration=5;
	private final double spell1Damage=2;
	private BukkitTask runnable;
	private PotionEffect poisonEffect;
	public Spider(PlayerWrapper w, EffigyList effigyType) {
		super(w, effigyType);
		poisonEffect = new PotionEffect(PotionEffectType.POISON, 40, 0);
	}
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent e) {
		if(e.getCause()==DamageCause.PROJECTILE && e.getDamager() instanceof Arrow && ((Arrow)e.getDamager()).getShooter().equals(getWrapper().getPlayer())) {
			e.setDamage(spell1Damage);
			if (e.getEntity() instanceof LivingEntity) {
				LivingEntity victim = (LivingEntity) e.getEntity();
				victim.addPotionEffect(poisonEffect);
			}
		}
	}
	
	@EventHandler
	public void onSneaking(PlayerToggleSneakEvent e) {
		if(!canClimb) return;
		if(e.getPlayer().equals(getWrapper().getPlayer())) {
			if(e.isSneaking()) {
				runnable = new BukkitRunnable() {
					@Override
					public void run() {
						if(isStickingToBlock()) {
							getWrapper().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, levitationDuration+1, 3, false, false, false));
						}
					}
				}.runTaskTimer(Main.getInstance(), 0, levitationDuration);
			}else {
				if(runnable != null) runnable.cancel();
			}
		}
	}
	
	private boolean isStickingToBlock() {
		Location ploc = getWrapper().getPlayer().getLocation();
		for(int n=-1;n<=1;n=n+2) {
			if(ploc.clone().add(n, 0, 0).getBlock().isSolid() || ploc.clone().add(0, 0, n).getBlock().isSolid()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void castPrimarySpell() {
		Player p = getWrapper().getPlayer();
		Location loc = p.getEyeLocation();
		final Arrow arrow = loc.getWorld().spawnArrow(loc, loc.getDirection(), 0.9f, 0);
		getWrapper().getPlayer().playSound(getWrapper().getPlayer(), Sound.ENTITY_SPIDER_HURT, 1, 0);
		arrow.setColor(Color.GREEN);
		arrow.setShooter(p);
		setCooldown(Ability.PRIMARY, 200);
	}

	@Override
	protected void castSecondarySpell() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getEyeLocation().getDirection());
		getWrapper().getPlayer().playSound(getWrapper().getPlayer(), Sound.ENTITY_SPIDER_AMBIENT, 1, 0);
		setCooldown(Ability.SECONDARY, 200);
	}

	@Override
	protected void roundEnd() {
	}
	@Override
	protected void death() {
	}
	@Override
	protected void roundStart() {
		canClimb = true;
	}
	
}