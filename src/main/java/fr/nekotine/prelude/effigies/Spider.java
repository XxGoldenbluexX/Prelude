package fr.nekotine.prelude.effigies;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.nekotine.prelude.Main;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Spider extends Effigy{
	
	private final int levitationDuration=5;
	private final double spell1DamageMultiplyer=2;
	private BukkitTask runnable;
	private boolean reinforcedAuto=false;
	public Spider(PlayerWrapper w, EffigyList effigyType) {
		super(w, effigyType);
	}
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent e) {
		if(e.getCause()==DamageCause.ENTITY_ATTACK && e.getDamager().equals(getWrapper().getPlayer()) && reinforcedAuto) {
			e.setDamage(e.getDamage()*spell1DamageMultiplyer);
			reinforcedAuto=false;
		}
	}
	
	@EventHandler
	public void onSneaking(PlayerToggleSneakEvent e) {
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
		reinforcedAuto=true;
		setCooldown(Ability.PRIMARY, 200);
	}

	@Override
	protected void castSecondarySpell() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getEyeLocation().getDirection().multiply(1));
		setCooldown(Ability.SECONDARY, 200);
	}

	@Override
	protected void roundEnd() {
	}
	@Override
	protected void death() {
	}
	
}