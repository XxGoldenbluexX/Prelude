package fr.nekotine.prelude.utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;

public class MessageSender {
	private static final String PRELUDE_MESSAGE_PREFIX = ChatColor.YELLOW+"[Prelude] ";
	private static final ChatColor RED_TEAM_COLOR = ChatColor.RED;
	private static final ChatColor BLUE_TEAM_COLOR = ChatColor.BLUE;
	private static final ChatColor DEFAULT_COLOR = ChatColor.WHITE;
	private static ChatColor getTeamColor(Team team) {
		switch(team) {
		case RED:
			return RED_TEAM_COLOR;
		case BLUE:
			return BLUE_TEAM_COLOR;
		default:
			return DEFAULT_COLOR;
		}
	}
	
	public static void sendMessage(String message, Player player) {
		player.sendMessage(message);
	}
	public static void sendMessages(String message, ArrayList<Player> players) {
		for(Player player : players) {
			sendMessage(message, player);
		}
	}
	public static void sendToAll(String message) {
		for(Player player : Main.getInstance().getPlayers()) {
			sendMessage(message, player);
		}
	}
	public static String getDeath(Player killed) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(killed);
		return PRELUDE_MESSAGE_PREFIX+getTeamColor(wrapper.getTeam())+killed.getName()+ChatColor.GRAY+" died";
	}
	public static String getDeath(Player killed, Player killer) {
		PlayerWrapper killedWrapper = Main.getInstance().getWrapper(killed);
		PlayerWrapper killerWrapper = Main.getInstance().getWrapper(killer);
		return PRELUDE_MESSAGE_PREFIX+getTeamColor(killedWrapper.getTeam())+killed.getName()+ChatColor.GRAY+" died to "
				+getTeamColor(killerWrapper.getTeam())+killer.getName();
	}
	public static String getAddMoney(int money) {
		return PRELUDE_MESSAGE_PREFIX + ChatColor.GREEN + "+" + money + ChatColor.GOLD+"$";
	}
	public static String getSpell(String spellName) {
		return PRELUDE_MESSAGE_PREFIX+ChatColor.GRAY+"Sort -> "+ChatColor.GREEN+spellName;
	}
	public static String ticksToTimeString(int ticks) {
		int secondes = ticks/20;
		int minutes = secondes / 60;
		int reste = secondes % 60;
		if(reste < 10) {
			return Integer.toString(minutes)+":"+"0"+Integer.toString(reste);
		}else {
			return Integer.toString(minutes)+":"+Integer.toString(reste);
		}
	}
	public static String getCooldownTimer(Effigy effigy) {
		String message = " | ";
		boolean primaryOnCD = effigy.isOnCooldown(Ability.PRIMARY);
		int primaryCD = effigy.getCooldown(Ability.PRIMARY);
		String primaryName = effigy.getEffigyType().getPrimarySpellName();
		
		boolean secondaryOnCD = effigy.isOnCooldown(Ability.SECONDARY);
		int secondaryCD = effigy.getCooldown(Ability.SECONDARY);
		String secondaryName = effigy.getEffigyType().getSecondarySpellName();
		
		if(secondaryOnCD) {
			message+= ChatColor.RED+"(2)"+ChatColor.GRAY+" ("+ticksToTimeString(secondaryCD)+") "+ChatColor.WHITE+"|";
		}else {
			
		}
		return "";
	}
}
