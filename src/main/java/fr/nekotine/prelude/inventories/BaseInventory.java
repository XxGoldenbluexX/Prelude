package fr.nekotine.prelude.inventories;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.InventoryMaker;

public abstract class BaseInventory implements Listener{
	private final Inventory inventory;
	public BaseInventory(Player holder, String title, int size) {
		this.inventory = InventoryMaker.makeInventory(holder, title, size);
		EventRegisterer.registerEvent(this);
	}
	public void destroy() {
		EventRegisterer.unregisterEvent(this);
	}
	public void setItem(ItemStack item, int slot) {
		inventory.setItem(slot, item);
	}
	public void setItems(ItemStack item, int[] slots) {
		for(int slot : slots) {
			setItem(item, slot);
		}
	}
	public void closeInventory(HumanEntity player) {
		player.closeInventory();
	}
	public void closeInventoryForAll() {
		inventory.close();
	}
	public Player getHolder() {
		return (Player)inventory.getHolder();
	}
	public void open(Player player) {
		player.openInventory(inventory);
	}
	
	public abstract void onInventoryClick(InventoryClickEvent e);
	
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e) {
		if(inventory.equals(e.getClickedInventory())) {
			e.setCancelled(true);
			onInventoryClick(e);
		}
	}
	
	

}
