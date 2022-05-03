package fr.nekotine.prelude.utils;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class MessageSender {
	//private static String PRELUDE_MESSAGE_PREFIX = ChatColor.YELLOW+"[Prelude] ";
	
	public static void sendMessage(String message, Player player) {
		player.sendMessage(message);
	}
	public static void sendMessages(String message, ArrayList<Player> players) {
		for(Player player : players) {
			sendMessage(message, player);
		}
	}
}
