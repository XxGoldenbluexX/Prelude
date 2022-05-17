package fr.nekotine.prelude;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import fr.nekotine.prelude.utils.Team;
import fr.nekotine.prelude.utils.MessageSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class GameScoreboard {
	private static final int SCOREBOARD_TIME_UPDATE_DELAY_TICKS = 20;
	
	private static final ChatColor RED_COLOR = ChatColor.RED;
	private static final ChatColor BLUE_COLOR = ChatColor.BLUE;
	
	private static final String RED_SCORE_PREFIX = "[R]";
	private static final String BLUE_SCORE_PREFIX = "[B]";
	private static final String POINT_CHARACTER ="⬢";
	private static final String NO_POINT_CHARACTER ="⬡";
	
	private static final ChatColor STATE_COLOR = ChatColor.YELLOW;
	private static final ChatColor TIME_COLOR = ChatColor.GRAY;
	private static final String MENU_DISPLAY = "Menu";
	private static final String PREPARATION_DISPLAY = "Préparation";
	private static final String PLAYING_DISPLAY = "En jeu";
	private static final String ENDING_DISPLAY = "Distribution des points";
	
	private static final ChatColor ALIVE_COLOR = ChatColor.GREEN;
	private static final ChatColor DEAD_COLOR = ChatColor.RED;
	private static final String ALIVE_PREFIX = "[❤]";
	private static final String DEAD_PREFIX = "[☠]";
	
	private final Scoreboard scoreboard;
	private final Objective objective;
	private final org.bukkit.scoreboard.Team redTeam;
	private final org.bukkit.scoreboard.Team blueTeam;
	
	private int playersInBlueTeam = 0;
	private int playersInRedTeam = 0;
	private String blueTeamEntry;
	private String redTeamEntry;
	private String stateEntry;
	
	public GameScoreboard() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		redTeam = scoreboard.registerNewTeam("Red");
		blueTeam = scoreboard.registerNewTeam("Blue");
		
		redTeam.setCanSeeFriendlyInvisibles(true);
		blueTeam.setCanSeeFriendlyInvisibles(true);
		
		redTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY,org.bukkit.scoreboard.Team.OptionStatus.NEVER);
		blueTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY,org.bukkit.scoreboard.Team.OptionStatus.NEVER);
		
		redTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE,org.bukkit.scoreboard.Team.OptionStatus.NEVER);
		blueTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE,org.bukkit.scoreboard.Team.OptionStatus.NEVER);
		
		objective = scoreboard.registerNewObjective("sidebar", "dummy",Component.text(ChatColor.BOLD+"Prelude").color((TextColor) NamedTextColor.GOLD));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		ArrayList<Player> bluePlayers = Main.getInstance().getPlayersInTeam(Team.BLUE);
		for(Player player : bluePlayers) {
			addPlayer(player);
		}
		
		ArrayList<Player> redPlayers = Main.getInstance().getPlayersInTeam(Team.RED);
		for(Player player : redPlayers) {
			addPlayer(player);
		}
		
		updateScoreDisplay();
		updateStateDisplay();
		addSpaces();
	}
	public void destroy() {
		objective.setDisplaySlot(null);
		objective.unregister();
		
		redTeam.unregister();
		blueTeam.unregister();
	}
	private String getScorePrefix(Team team) {
		switch(team) {
		case RED:
			return RED_SCORE_PREFIX;
		case BLUE:
			return BLUE_SCORE_PREFIX;
		default:
			return "";
		}
	}
	private ChatColor getTeamColor(Team team) {
		switch(team) {
		case RED:
			return RED_COLOR;
		case BLUE:
			return BLUE_COLOR;
		default:
			return ChatColor.WHITE;
		}
	}
	
	
	public void addPlayer(Player player) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(player);
		
		String playerString = "";
		if(wrapper.isAlive()) {
			playerString = ALIVE_COLOR+(ALIVE_PREFIX + " " + wrapper.getPlayer().getName());
		}else {
			playerString = DEAD_COLOR+(DEAD_PREFIX + " " + wrapper.getPlayer().getName());
		}
		
		player.setScoreboard(scoreboard);
		
		switch(wrapper.getTeam()) {
		case RED:
			addToScore(playersInBlueTeam + 3, 1);
			
			wrapper.setScoreboardEntry(playerString);
			objective.getScore(playerString).setScore(playersInBlueTeam + 3);
			
			redTeam.addPlayer(player);
			playersInRedTeam++;
			break;
		case BLUE:
			addToScore(0, 1);
			
			wrapper.setScoreboardEntry(playerString);
			objective.getScore(playerString).setScore(1);
			
			blueTeam.addPlayer(player);
			playersInBlueTeam++;
			break;
		}
	}
	public void removePlayer(Player player) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(player);
		int score = objective.getScore(wrapper.getScoreboardEntry()).getScore();
		addToScore(score+1, -1);
		
		objective.getScore(wrapper.getScoreboardEntry()).resetScore();
		wrapper.setScoreboardEntry(null);
		
		switch(wrapper.getTeam()) {
		case RED:
			playersInRedTeam--;
			break;
		case BLUE:
			playersInBlueTeam--;
			break;
		}
	}
	public void updatePlayerDisplay(Player player) {
		removePlayer(player);
		addPlayer(player);
	}
	private void addToScore(int minimumScore, int toAdd) {
		for(String entry : scoreboard.getEntries()) {
			int score = objective.getScore(entry).getScore();
			if(score>=minimumScore) {
				objective.getScore(entry).setScore(score + toAdd);
			}
		}
	}
	private void addSpaces() {
		int position = playersInBlueTeam + 1 + 1;
		objective.getScore("").setScore(position);
		
		position = playersInBlueTeam + 1 + 1 + playersInRedTeam + 1 + 1;
		objective.getScore(" ").setScore(position);
	}
	public void updateScoreDisplay() {
		updateScoreDisplay(Team.RED);
		updateScoreDisplay(Team.BLUE);
	}
	private void updateScoreDisplay(Team team) {
		String scoreString = getTeamColor(team)+getScorePrefix(team) + " ";
		int score = Main.getInstance().getRoundManager().getScore(team);
		
		for(int r=1; r<=score; r++) {
			scoreString+=POINT_CHARACTER;
		}
		
		scoreString = getTeamColor(team)+scoreString;
		
		for(int r=1; r<=RoundManager.getPointRecquirementToWin() - score; r++) {
			scoreString+=NO_POINT_CHARACTER;
		}
		
		
		int position;
		switch(team) {
		case RED:
			position = playersInBlueTeam + 1 + playersInRedTeam + 1 + 1;
			
			if(redTeamEntry!=null) scoreboard.resetScores(redTeamEntry);
			redTeamEntry = scoreString;
			objective.getScore(redTeamEntry).setScore(position);
			break;
		case BLUE:
			position = playersInBlueTeam + 1;
			
			if(blueTeamEntry!=null) scoreboard.resetScores(blueTeamEntry);
			blueTeamEntry = scoreString;
			objective.getScore(blueTeamEntry).setScore(position);
			break;
		}
	}
	public void updateStateDisplay() {
		int position = playersInBlueTeam + 1 + 1 + playersInRedTeam + 1 + 1 + 1;
		
		String stateString = "Etat: ";
		String timeString;
		
		switch(Main.getInstance().getRoundManager().getRoundState()) {
		case MENU:
			stateString += STATE_COLOR+MENU_DISPLAY;
			break;
		case PREPARATION:
			timeString = MessageSender.ticksToTimeString(Main.getInstance().getRoundManager().getPreparationDurationLeft());
			
			stateString += STATE_COLOR+PREPARATION_DISPLAY;
			stateString += TIME_COLOR+" ("+timeString+")";
			break;
		case PLAYING:
			timeString = MessageSender.ticksToTimeString(Main.getInstance().getRoundManager().getGameDuration());
			stateString += STATE_COLOR+PLAYING_DISPLAY;
			stateString += TIME_COLOR+" ("+timeString+")";
			break;
		case ENDING:
			timeString = MessageSender.ticksToTimeString(Main.getInstance().getRoundManager().getEndingDurationLeft());
			stateString += STATE_COLOR+ENDING_DISPLAY;
			stateString += TIME_COLOR+" ("+timeString+")";
			break;
		}
		
		if(stateEntry != null) scoreboard.resetScores(stateEntry);
		stateEntry = stateString;
		objective.getScore(stateEntry).setScore(position);
	}
	public void updatePlayerAliveDisplay(Player player) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(player);
		String scoreboardEntry = wrapper.getScoreboardEntry();
		int score = objective.getScore(scoreboardEntry).getScore();
		
		String playerString = "";
		if(wrapper.isAlive()) {
			playerString = ALIVE_COLOR+(ALIVE_PREFIX + " " + wrapper.getPlayer().getName());
		}else {
			playerString = DEAD_COLOR+(DEAD_PREFIX + " " + wrapper.getPlayer().getName());
		}
		
		scoreboard.resetScores(wrapper.getScoreboardEntry());
		wrapper.setScoreboardEntry(playerString);
		objective.getScore(playerString).setScore(score);
	}
	
	public static int getUpdateDelayTicks() {
		return SCOREBOARD_TIME_UPDATE_DELAY_TICKS;
	}
}
