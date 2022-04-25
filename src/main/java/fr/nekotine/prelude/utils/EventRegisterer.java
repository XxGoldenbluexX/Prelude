package fr.nekotine.prelude.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.PluginManager;

import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.events.PlayerChangeEffigyEvent;
import fr.nekotine.prelude.events.PlayerChangeTeamEvent;

public class EventRegisterer {
	private static final PluginManager PM = Bukkit.getPluginManager();
	public static void unregisterEvent(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
	public static void registerEvent(Listener listener) {
		PM.registerEvents(listener, Main.getInstance());
	}
	public static void callInventoryClickEvent(InventoryView inventoryView, SlotType slotType, int slot, ClickType clickType, InventoryAction inventoryAction) {
		new InventoryClickEvent(inventoryView, slotType, slot, clickType, inventoryAction).callEvent();
	}
	public static void callPlayerChangeTeamEvent(Player player, Team before, Team after) {
		new PlayerChangeTeamEvent(player, before, after).callEvent();
	}
	public static void callPlayerChangeEffigyEvent(Player player, EffigyList before, EffigyList after) {
		new PlayerChangeEffigyEvent(player, before, after).callEvent();
	}
}
