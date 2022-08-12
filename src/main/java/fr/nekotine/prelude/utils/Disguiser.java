package fr.nekotine.prelude.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.nekotine.prelude.Main;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig.NotifyBar;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;

public class Disguiser {
	public static MobDisguise disguiseToAll(Player toDisguise, DisguiseType disguiseType) {
		MobDisguise dg = new MobDisguise(disguiseType);
		
		dg.setSelfDisguiseVisible(false);
		dg.setNotifyBar(NotifyBar.NONE);
		
		dg.getWatcher().setCustomName(Main.getInstance().getScoreboard().getTeamColor(Main.getInstance().getWrapper(toDisguise).getTeam()) + ComponentMaker.getText(toDisguise.displayName()));
		dg.getWatcher().setCustomNameVisible(true);
		
		DisguiseAPI.disguiseToAll(toDisguise, dg);
		
		return dg;
	}
	public static void undisguise(Player player) {
		DisguiseAPI.undisguiseToAll(player);
	}
	public static void setIgnitedCreeper(MobDisguise creeperDisguise, boolean ignited) {
		CreeperWatcher cw = (CreeperWatcher)creeperDisguise.getWatcher();
		cw.setIgnited(ignited);		
	}
	public static void setBurning(MobDisguise disguise, boolean burning) {
		disguise.getWatcher().setBurning(burning);	
	}
	public static void setGlow(MobDisguise disguise, boolean glow) {
		disguise.getWatcher().setGlowing(true);
	}
	public static void setGlowColor(MobDisguise disguise, ChatColor color) {
		disguise.getWatcher().setGlowColor(color);
	}
}
