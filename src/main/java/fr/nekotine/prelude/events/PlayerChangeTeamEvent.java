package fr.nekotine.prelude.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.prelude.utils.Team;

public class PlayerChangeTeamEvent extends Event{
	private final Team before;
	private final Team after;
	private final Player player;
	public PlayerChangeTeamEvent(Player player, Team before, Team after) {
		this.player=player;
		this.before=before;
		this.after=after;
	}
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public Team getBefore() {
		return before;
	}
	public Team getAfter() {
		return after;
	}
	public Player getPlayer() {
		return player;
	}
}
