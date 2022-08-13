package fr.nekotine.prelude.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.nekotine.core.effect.CustomEffectType;
import fr.nekotine.core.util.UtilEvent;
import fr.nekotine.prelude.Main;
import io.papermc.paper.event.entity.EntityMoveEvent;

public class FreezeEffect implements CustomEffectType, Listener{
	private LivingEntity holder;
	
	//
	
	@Override
	public boolean haveAmplifier() {
		return false;
	}
	@Override
	public void onAmplifierChange(LivingEntity entity, int arg1, int arg2) {
	}
	@Override
	public void onApply(LivingEntity holder) {
		this.holder = holder;
		UtilEvent.Register(Main.getInstance(), this);
	}
	@Override
	public void onUnapply(LivingEntity arg0) {
		UtilEvent.Unregister(this);
	}
	
	//
	
	@EventHandler
	public void OnMove(EntityMoveEvent e) {
		if(e.getEntity().equals(holder)) e.setCancelled(true);
	}
}
