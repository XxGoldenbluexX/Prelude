package fr.nekotine.prelude.utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
}
