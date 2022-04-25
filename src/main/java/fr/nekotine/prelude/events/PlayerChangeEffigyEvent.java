package fr.nekotine.prelude.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.prelude.EffigyList;

public class PlayerChangeEffigyEvent extends Event{
	private final Player player;
	private final EffigyList before;
	private final EffigyList after;
	public PlayerChangeEffigyEvent(Player player, EffigyList before, EffigyList after) {
		this.player=player;
		this.before=before;
		this.after=after;
	}
	
	public Player getPlayer() {
		return player;
	}
	public EffigyList getBefore() {
		return before;
	}
	public EffigyList getAfter() {
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
