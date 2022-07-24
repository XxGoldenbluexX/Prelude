package fr.nekotine.prelude;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.MessageSender;
import fr.nekotine.prelude.utils.RoundState;
import fr.nekotine.prelude.utils.Team;

public class RoundManager implements Listener{
	private static final int STARTING_MONEY = 0;
	private static final int POINT_RECQUIREMENT_TO_WIN = 6;
	
	private static final int POINTS_PER_KILL = 1;
	private static final int POINTS_PER_WIN = 2;
	private static final int POINTS_PER_LOSS = 1;
	private static final int POINTS_PER_LOSING_STREAK = 2;
	
	private static final int PREPARATION_PHASE_DURATION_TICKS = 20*15;
	private static final int ENDING_PHASE_DURATION_TICKS = 20*10;
	
	private static final EffigyList DEFAULT_EFFIGY = EffigyList.SPIDER;
	
	private static final PotionEffect SATURATION_EFFECT = new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false);
	
	private Team wonLastRound;
	private RoundState roundState = RoundState.MENU;
	
	private int preparation_phase_duration_left = PREPARATION_PHASE_DURATION_TICKS;
	private int game_duration = 0;
	private int ending_phase_duration_left = ENDING_PHASE_DURATION_TICKS;
	
	private int redScore = 0;
	private int blueScore = 0;
	
	public RoundManager() {
		EventRegisterer.registerEvent(this);
	}
	public void destroy() {
		EventRegisterer.unregisterEvent(this);
	}
	public void tick() {
		if(getRoundState()==RoundState.PREPARATION) {
			preparation_phase_duration_left--;
			if(preparation_phase_duration_left % GameScoreboard.getUpdateDelayTicks() == 0) Main.getInstance().getScoreboard().updateStateDisplay();
			if(preparation_phase_duration_left<=0) {
				preparationPhaseEnd();
			}
		}else if(getRoundState()==RoundState.ENDING) {
			ending_phase_duration_left--;
			if(ending_phase_duration_left % GameScoreboard.getUpdateDelayTicks() == 0) Main.getInstance().getScoreboard().updateStateDisplay();
			if(ending_phase_duration_left<=0) {
				startRound();
			}
		}else if(getRoundState()==RoundState.PLAYING) {
			game_duration++;
			if(game_duration % GameScoreboard.getUpdateDelayTicks() == 0) Main.getInstance().getScoreboard().updateStateDisplay();
		}
	}
	private void preparationPhaseStart() {
		Main.getInstance().getMap().closeWalls();
		
		for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) wrapper.giveShopItem();
		
		preparation_phase_duration_left = PREPARATION_PHASE_DURATION_TICKS;
		
		setRoundState(RoundState.PREPARATION);
		
		System.out.println("preparation phase start");
	}
	private void setRoundState(RoundState state) {
		roundState=state;
		Main.getInstance().getScoreboard().updateStateDisplay();
	}
	public RoundState getRoundState() {
		return roundState;
	}
	private void preparationPhaseEnd() {
		game_duration = 0;
		setRoundState(RoundState.PLAYING);
		for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
			wrapper.removeShopItem();
			wrapper.closeShopInventory();
			wrapper.getEffigy().roundStart();
		}
		Main.getInstance().getMap().openWalls();
		
		System.out.println("preparation phase end");
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getPlayer();
		PlayerWrapper wrapper = getWrapperOfPlayer(player);
		if(Main.getInstance().isRunning() && getRoundState()==RoundState.PLAYING && wrapper != null && wrapper.isAlive()) {
			e.setCancelled(true);
			System.out.println("cancelmled");
			setAlive(player, false);
			
			wrapper.getPlayer().setGameMode(GameMode.SPECTATOR);
			PlayerWrapper wrapperOfKiller = getWrapperOfPlayer(player.getKiller());
			if(wrapperOfKiller != null && wrapperOfKiller.getTeam()==wrapper.getTeam()) {
				addMoney(wrapperOfKiller.getPlayer(), POINTS_PER_KILL);
				MessageSender.sendToAll(MessageSender.getDeath(player, player.getKiller()));
			}else {
				MessageSender.sendToAll(MessageSender.getDeath(player));
			}
			
			endRound();
		}
	}
	private void teleportPlayersToSpawn() {
		for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
			Main.getInstance().getMap().teleportPlayer(wrapper.getTeam(), wrapper.getPlayer());
		}
		System.out.println("teleported players");
	}
	private void setDefaultEffigy(Player player) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(player);
		wrapper.setEffigy(DEFAULT_EFFIGY);
	}
	private void setMoney(Player player, int money) {
		Main.getInstance().getWrapper(player).setMoney(money);
	}
	private void setAlive(Player player, boolean alive) {
		Main.getInstance().getWrapper(player).setAlive(alive);
		Main.getInstance().getScoreboard().updatePlayerAliveDisplay(player);
	}
	private void resetEffigy(Player player) {
		PlayerWrapper wrapper = Main.getInstance().getWrapper(player);
		if(!wrapper.isAlive()) {
			setDefaultEffigy(player);
		}else {
			wrapper.setEffigy(wrapper.getEffigy().getEffigyType());
		}
	}
	public void startGame() {
		wonLastRound = null;
		setRoundState(RoundState.PREPARATION);
		preparation_phase_duration_left = PREPARATION_PHASE_DURATION_TICKS;
		redScore = 0;
		blueScore = 0;
		
		for(Player player : Main.getInstance().getPlayers()) {
			player.getInventory().clear();
			setDefaultEffigy(player);
			setAlive(player, true);
			setMoney(player, STARTING_MONEY);
			player.addPotionEffect(SATURATION_EFFECT);
		}
		
		Main.getInstance().getBumperManager().activate(true);
		startRound();
	}
	public void startRound() {
		if(redScore>=POINT_RECQUIREMENT_TO_WIN || blueScore>=POINT_RECQUIREMENT_TO_WIN) {
			endGame();
		}else {
			for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
				wrapper.roundEnded();
			}
			preparationPhaseStart();
			
			for(Player player : Main.getInstance().getPlayers()) {
				resetEffigy(player);
				setAlive(player, true);
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				player.setGameMode(GameMode.ADVENTURE);
			}
			
			teleportPlayersToSpawn();
			
			System.out.println("round started");
		}
		
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
		Main.getInstance().getScoreboard().updateScoreDisplay();
		
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
	private void startEndingPhase() {
		ending_phase_duration_left = ENDING_PHASE_DURATION_TICKS;
		setRoundState(RoundState.ENDING);
	}
	private void endRound() {
		if(getNumberOfPlayersAliveInTeam(Team.RED) == 0) {
			giveRoundPoints(Team.BLUE);
			startEndingPhase();
			System.out.println("round ended win blue");
			
		}else if(getNumberOfPlayersAliveInTeam(Team.BLUE) == 0) {
			giveRoundPoints(Team.RED);
			startEndingPhase();
			System.out.println("round ended win red");
		}
	}
	public void endGame() {
		if(roundState!=RoundState.MENU) {
			for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
				wrapper.roundEnded();
			}
			Main.getInstance().getMap().openWalls();
			Main.getInstance().getBumperManager().activate(false);
			Main.getInstance().end();
				
			for(PlayerWrapper wrapper : Main.getInstance().getWrappers()) {
				wrapper.setAlive(true);
			}
				
			setRoundState(RoundState.MENU);
			System.out.println("game ended");
		}
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
	public boolean isRoundPlaying() {
		return roundState==RoundState.PLAYING;
	}
	public int getScore(Team team) {
		switch(team) {
		case RED:
			return redScore;
		case BLUE:
			return blueScore;
		default:
			return -1;
		}
	}
	public int getPreparationDurationLeft() {
		return preparation_phase_duration_left;
	}
	public int getEndingDurationLeft() {
		return ending_phase_duration_left;
	}
	public int getGameDuration() {
		return game_duration;
	}
	
	public static int getPointRecquirementToWin() {
		return POINT_RECQUIREMENT_TO_WIN;
	}
	
	
}
