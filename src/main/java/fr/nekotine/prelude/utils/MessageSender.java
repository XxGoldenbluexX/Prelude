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
	private static final int NUMBER_OF_BARS = 10;
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
	public static String getAddMoney(int money, int before) {
		int after = money + before;
		String message = PRELUDE_MESSAGE_PREFIX;
		
		if(money >= 0) {
			message += ChatColor.GREEN+"+"+money;
		}else {
			message += ChatColor.RED+Integer.toString(money);
		}
		return message + ChatColor.GOLD+" $" + 
				ChatColor.GRAY+" ("+before+" -> "+ after +")";
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
		boolean primaryOnCD = effigy.isOnCooldown(Ability.PRIMARY);
		int primaryCD = effigy.getCooldown(Ability.PRIMARY);
		int primaryBaseCD = effigy.getTotalCooldown(Ability.PRIMARY);
		String primaryName = effigy.getEffigyType().getPrimarySpellName();
		String primaryMessage = "";
		if(primaryOnCD) {
			primaryMessage += "|";
			float ratio = primaryCD / primaryBaseCD;
			int colored = Math.round(ratio * NUMBER_OF_BARS);
			for(int i = 1 ; i <= colored ; i++) {
				primaryMessage+=ChatColor.GREEN+":";
			}
			for(int i = 1 ; i <= NUMBER_OF_BARS - colored ; i++) {
				primaryMessage+=ChatColor.GRAY+":";
			}
			primaryMessage += ChatColor.WHITE+"|";
			
			primaryMessage+= ChatColor.GRAY+" ("+ticksToTimeString(primaryCD)+"s) "+ChatColor.RED+"(1)";
		}else {
			primaryMessage+= ChatColor.AQUA+"["+primaryName+"]" + ChatColor.GREEN+"(1)";
		}
		
		boolean secondaryOnCD = effigy.isOnCooldown(Ability.SECONDARY);
		int secondaryCD = effigy.getCooldown(Ability.SECONDARY);
		int secondaryBaseCD = effigy.getTotalCooldown(Ability.SECONDARY);
		String secondaryName = effigy.getEffigyType().getSecondarySpellName();
		String secondaryMessage = "";
		
		
		if(secondaryOnCD) {
			secondaryMessage+= ChatColor.RED+"(2)"+ChatColor.GRAY+" ("+ticksToTimeString(secondaryCD)+"s) "+ChatColor.WHITE+"|";
			float ratio = secondaryCD / secondaryBaseCD;
			int colored = Math.round(ratio * NUMBER_OF_BARS);
			for(int i = 1 ; i <= NUMBER_OF_BARS - colored ; i++) {
				secondaryMessage+=ChatColor.GRAY+":";
			}
			for(int i = 1 ; i <= colored ; i++) {
				secondaryMessage+=ChatColor.GREEN+":";
			}
			secondaryMessage += ChatColor.WHITE+"|";
		}else {
			secondaryMessage+= ChatColor.GREEN+"(2)" + ChatColor.AQUA+"["+secondaryName+"]";
		}
		
		int primaryMessageLength = primaryMessage.length();
		int secondaryMessageLength = secondaryMessage.length();
		if(primaryMessageLength < secondaryMessageLength) {
			int difference = secondaryMessageLength - primaryMessageLength;
			primaryMessage = " ".repeat(difference) + primaryMessage;
		}else {
			int difference = primaryMessageLength - secondaryMessageLength;
			secondaryMessage = secondaryMessage + " ".repeat(difference);
		}
		String message = primaryMessage + " | " + secondaryMessage;
		return message;
	}
}
