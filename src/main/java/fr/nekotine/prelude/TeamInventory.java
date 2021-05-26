package fr.nekotine.prelude;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class TeamInventory implements Listener {
	private final Inventory inv;
	private final PlayerWrapper wrapper;
	public TeamInventory(PlayerWrapper wrapper) {
		this.wrapper=wrapper;
		Bukkit.getPluginManager().registerEvents(this, PreludeMain.main);
		inv = Bukkit.createInventory(null, 9*6, Component.text("Equipes"));
		loadInventory();
		wrapper.player.openInventory(inv);
	}
	private void loadInventory() {
		for(int x=0;x<54;x++) {
			inv.setItem(x, createStack(wrapper,x+1));
		}
	}
	private static ItemStack createStack(PlayerWrapper wrapper, int count) {
		ItemStack is;
		if (wrapper.team==count) {
			is = new ItemStack(Material.LIME_WOOL, count);
		}else {
			is = new ItemStack(Material.WHITE_WOOL, count);
		}
		ItemMeta meta = is.getItemMeta();
		List<Component> loreList = new ArrayList<>();
		loreList.add(Component.text(ChatColor.RED+"Membres:"));
		for(PlayerWrapper wrappers : PreludeMain.main.playerWrappers.values()) {
			if(wrappers.team==count) {
				loreList.add(Component.text(ChatColor.GOLD+""+ChatColor.UNDERLINE+wrappers.player.getName()));
			}
		}
		meta.lore(loreList);
		is.setItemMeta(meta);
		return is;
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if(e.getClickedInventory()==inv) {
			e.setCancelled(true);
			wrapper.team=e.getRawSlot()+1;
			loadInventory();
		}
	}
	@EventHandler
	public void closeInventory(InventoryCloseEvent e) {
		if(e.getInventory()==inv) {
			HandlerList.unregisterAll(this);
		}
	}
}
