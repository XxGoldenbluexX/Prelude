package fr.nekotine.prelude.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.effect.CustomEffectType;
import fr.nekotine.core.util.UtilEvent;
import fr.nekotine.prelude.Main;

public class InvulnerableEffect implements CustomEffectType, Listener{
	@Override
	public boolean haveAmplifier() {
		return false;
	}
	@Override
	public void onAmplifierChange(LivingEntity arg0, int arg1, int arg2) {
	}
	@Override
	public void onApply(LivingEntity arg0) {
		UtilEvent.Register(Main.getInstance(), this);
	}
	@Override
	public void onUnapply(LivingEntity arg0) {
		UtilEvent.Unregister(this);
	}
	
	//
	
	@EventHandler
	public void OnDamage(LivingEntityDamageEvent e) {
	}
}
