package fr.nekotine.prelude.utils;

import org.bukkit.entity.Player;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig.NotifyBar;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;

public class Disguiser {
	public static void disguiseToAll(Player toDisguise, DisguiseType disguiseType) {
		MobDisguise dg = new MobDisguise(disguiseType);
		
		dg.setSelfDisguiseVisible(false);
		dg.setNotifyBar(NotifyBar.NONE);
		dg.getWatcher().setCustomName(ComponentMaker.getText(toDisguise.displayName()));
		dg.getWatcher().setCustomNameVisible(true);
		if(disguiseType==DisguiseType.SLIME) ((SlimeWatcher)dg.getWatcher()).setSize(2);;
		
		DisguiseAPI.disguiseToAll(toDisguise, dg);
	}
	public static void undisguise(Player player) {
		DisguiseAPI.undisguiseToAll(player);
	}
}
