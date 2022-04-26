package fr.nekotine.prelude;

import org.bukkit.event.Listener;

import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.Team;

public class RoundManager implements Listener{
	private static final int POINT_RECQUIREMENT_TO_WIN = 5;
	
	private Team wonLastRound;
	private int redPoints = 0;
	private int bluePoints = 0;
	
	public RoundManager() {
		EventRegisterer.registerEvent(this);
	}
}
