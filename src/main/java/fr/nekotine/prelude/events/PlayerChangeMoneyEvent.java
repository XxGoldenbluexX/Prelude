package fr.nekotine.prelude.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeMoneyEvent extends Event{
	private final Player player;
	private final int before;
	private final int after;
	public PlayerChangeMoneyEvent(Player player, int before, int after) {
		this.player=player;
		this.before=before;
		this.after=after;
	}
	public Player getPlayer() {
		return player;
	}
	public int getBefore() {
		return before;
	}
	public int getAfter() {
		return after;
	}
	
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}
