package fr.nekotine.prelude.utils;

import org.bukkit.entity.Player;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig.NotifyBar;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public class Disguiser {
	public static void disguiseToAll(Player toDisguise, DisguiseType disguiseType) {
		MobDisguise dg = new MobDisguise(disguiseType);
		
		dg.setSelfDisguiseVisible(false);
		dg.setNotifyBar(NotifyBar.NONE);
		
		DisguiseAPI.disguiseToAll(toDisguise, dg);
	}
	public static void undisguise(Player player) {
		DisguiseAPI.undisguiseToAll(player);
	}
}
