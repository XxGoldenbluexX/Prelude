package fr.nekotine.prelude.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryMaker {
	public static Inventory makeInventory(Player owner, String title, int size) {
		return Bukkit.createInventory(owner, size, ComponentMaker.getComponent(title));
	}
}
