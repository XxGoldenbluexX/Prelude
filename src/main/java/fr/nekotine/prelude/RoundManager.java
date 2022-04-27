package fr.nekotine.prelude;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Team;

public class RoundManager implements Listener{
	private static final int POINT_RECQUIREMENT_TO_WIN = 5;
	private static final int POINTS_PER_KILL = 1;
	private static final int POINTS_PER_WIN = 2;
	private static final int POINTS_PER_LOSS = 1;
	private static final int POINTS_PER_LOSING_STREAK = 2;
	
	private static final int PREPARATION_PHASE_DURATION_TICKS = 20*15;
	
	private static final EffigyList DEFAULT_EFFIGY = EffigyList.TestEffigy1;
	
	private Team wonLastRound;
	private boolean inPreparationPhase = true;
	private int preparation_phase_duration_left = PREPARATION_PHASE_DURATION_TICKS;
	private int redScore = 0;
	private int blueScore = 0;
	
	public RoundManager() {
		EventRegisterer.registerEvent(this);
	}
	public void destroy() {
		EventRegisterer.unregisterEvent(this);
	}
	public void tick() {
		if(inPreparationPhase) {
			preparation_phase_duration_left--;
			if(preparation_phase_duration_left<=0) {
				preparationPhaseEnd();
			}
		}
	}
	private void preparationPhaseStart() {
		Main.getInstance().getMap().closeWalls();
		
		for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) wrapper.giveShopItem();
		
		preparation_phase_duration_left = PREPARATION_PHASE_DURATION_TICKS;
		inPreparationPhase = true;
	}
	private void preparationPhaseEnd() {
		inPreparationPhase = false;
		
		for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
			wrapper.removeShopItem();
			wrapper.closeShopInventory();
		}
		
		Main.getInstance().getMap().openWalls();
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getPlayer();
		PlayerWrapper wrapper = getWrapperOfPlayer(player);
		if(wrapper != null && wrapper.isAlive()) {
			wrapper.setAlive(false);
			wrapper.getPlayer().setGameMode(GameMode.SPECTATOR);
			
			PlayerWrapper wrapperOfKiller = getWrapperOfPlayer(player.getKiller());
			if(wrapperOfKiller != null) addMoney(wrapperOfKiller.getPlayer(), POINTS_PER_KILL);
			
			endRound();
		}
	}
	private void teleportPlayersToSpawn() {
		for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
			Main.getInstance().getMap().teleportPlayer(wrapper.getTeam(), wrapper.getPlayer());
		}
	}
	private void resetEffigy(Player player) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(player);
		if(!wrapper.isAlive()) wrapper.setEffigy(DEFAULT_EFFIGY);
	}
	public void startRound() {
		preparationPhaseStart();
		
		for(Player player : Main.getInstance().getPlayers()) {
			resetEffigy(player);
			player.setGameMode(GameMode.ADVENTURE);
		}
		
		teleportPlayersToSpawn();
	}
	private PlayerWrapper getWrapperOfPlayer(Player player) {
		if(Main.getInstance().isPlaying(player)) {
			return Main.getInstance().getWrapper(player);
		}
		return null;
	}
	private void increaseScore(Team team) {
		switch (team) {
		case RED:
			redScore++;
			break;
		case BLUE:
			blueScore++;
			break;
		}
	}
	private void addMoney(Player player, int toAdd) {
		Main.getInstance().getWrapper(player).addMoney(toAdd);;
	}
	private int getNumberOfPlayersAliveInTeam(Team team) {
		int count = 0;
		for(Player player : Main.getInstance().getPlayersInTeam(team)) {
			if(Main.getInstance().getWrapper(player).isAlive()) count++;
		}
		return count;
	}
	private void teamWinRound(Team team) {
		
		increaseScore(team);
		
		for(Player player : Main.getInstance().getPlayersInTeam(team)) {
			addMoney(player, POINTS_PER_WIN);
		}
	}
	private void teamLoseRound(Team team) {
		
		int points_won = POINTS_PER_LOSS;
		if(team!=wonLastRound) points_won = POINTS_PER_LOSING_STREAK;
		
		for(Player player : Main.getInstance().getPlayersInTeam(team)) {
			addMoney(player, points_won);
		}
	}
	private void endRound() {
		if(getNumberOfPlayersAliveInTeam(Team.RED) == 0) {
			giveRoundPoints(Team.BLUE);
			boolean ended = endGame();
			if(!ended) startRound();
			
		}else if(getNumberOfPlayersAliveInTeam(Team.BLUE) == 0) {
			giveRoundPoints(Team.RED);
			boolean ended = endGame();
			if(!ended) startRound();
		}
	}
	private boolean endGame() {
		if(redScore>=POINT_RECQUIREMENT_TO_WIN || blueScore>=POINT_RECQUIREMENT_TO_WIN) {
			wonLastRound = null;
			inPreparationPhase = true;
			preparation_phase_duration_left = PREPARATION_PHASE_DURATION_TICKS;
			redScore = 0;
			blueScore = 0;
			
			Main.getInstance().getMap().openWalls();
			Main.getInstance().end();
			return true;
		}
		return false;
	}
	private void giveRoundPoints(Team winner) {
		switch(winner) {
		case RED:
			teamWinRound(Team.RED);
			teamLoseRound(Team.BLUE);
			break;
		case BLUE:
			teamWinRound(Team.BLUE);
			teamLoseRound(Team.RED);
			break;
		}
	}
	
	
}
