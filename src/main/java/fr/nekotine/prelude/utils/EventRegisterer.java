package fr.nekotine.prelude.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.PluginManager;

import fr.nekotine.prelude.Main;

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
}
