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

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.PreludeMain;

public class Spider extends Effigy{
	private final int levitationDuration=5;
	private final double spell1DamageMultiplyer=2;
	private BukkitTask runnable;
	private boolean reinforcedAuto=false;
	public Spider(PlayerWrapper w) {
		super(w,3000,3000);
	}

	@Override
	protected void castSpell1() {
		reinforcedAuto=true;
	}
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent e) {
		if(e.getCause()==DamageCause.ENTITY_ATTACK && e.getDamager().equals(getWrapper().getPlayer()) && reinforcedAuto) {
			e.setDamage(e.getDamage()*spell1DamageMultiplyer);
			reinforcedAuto=false;
		}
	}

	@Override
	protected void castSpell2() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getEyeLocation().getDirection().multiply(1));
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
				}.runTaskTimer(PreludeMain.main, 0, levitationDuration);
			}else {
				runnable.cancel();
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
	
}
