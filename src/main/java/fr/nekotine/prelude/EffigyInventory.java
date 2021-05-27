package fr.nekotine.prelude;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class EffigyInventory implements Listener{
	private final Inventory inv;
	private final PlayerWrapper wrapper;
	private final ArrayList<EffigyList> effigies = new ArrayList<EffigyList>();
	public EffigyInventory(PlayerWrapper wrapper) {
		this.wrapper=wrapper;
		Bukkit.getPluginManager().registerEvents(this, PreludeMain.main);
		inv = Bukkit.createInventory(null, 9*6, Component.text("Effigies"));
		loadInventory();
		wrapper.player.openInventory(inv);
	}
	private void loadInventory() {
		effigies.clear();
		for(int tier=1;tier<=wrapper.tier;tier++) {
			effigies.addAll(EffigyList.getTier(tier));	
		}
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta meta = head.getItemMeta();
		meta.displayName(Component.text("Humain"));
		head.setItemMeta(meta);
		inv.setItem(0, head);
		for(int x=0;x<effigies.size();x++) {
			inv.setItem(x+1, createStack(wrapper,effigies.get(x)));
		}
	}
	private static ItemStack createStack(PlayerWrapper wrapper, EffigyList ef) {
		ItemStack is = new ItemStack(ef.shopMaterial,ef.tier);
		ItemMeta meta = is.getItemMeta();
		ChatColor effigyColor = ChatColor.WHITE;
		switch(ef.tier) {
		case 1:
			effigyColor = ChatColor.GREEN;
			break;
		case 2:
			effigyColor = ChatColor.BLUE;
			break;
		case 3:
			effigyColor = ChatColor.LIGHT_PURPLE;
			break;
		default:
			break;
		}
		meta.displayName(Component.text(effigyColor+ef.toString()));
		if(wrapper.effigy==ef) {
			List<Component> loreList = new ArrayList<>();
			loreList.add(Component.text(ChatColor.DARK_GREEN+""+ChatColor.UNDERLINE+"[Actuelle]"));
			meta.lore(loreList);
		}
		is.setItemMeta(meta);
		if(wrapper.effigy==ef) {
			is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			is.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		return is;
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if(e.getClickedInventory()==inv) {
			e.setCancelled(true);
			if(e.getRawSlot()==0) {
				wrapper.effigy=null;
				e.getWhoClicked().closeInventory();
			}else {
				wrapper.effigy=effigies.get(e.getRawSlot()-1);
			}
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
