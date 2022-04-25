package fr.nekotine.prelude.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MapChangeEvent extends Event{
	private final String before;
	private final String after;
	public MapChangeEvent(String before, String after) {
		this.before=before;
		this.after=after;
	}
	public String getBefore() {
		return before;
	}
	public String getAfter() {
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
