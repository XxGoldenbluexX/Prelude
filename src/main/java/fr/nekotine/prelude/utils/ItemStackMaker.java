package fr.nekotine.prelude.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class ItemStackMaker {
	public static ItemStack make(Material mat, int quantity, String name, String... lore) {
		ItemStack item = new ItemStack(mat,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(ComponentMaker.getComponent(name));
		List<Component> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(ComponentMaker.getComponent(line));
			}
		}
		meta.lore(loreList);
		item.setItemMeta(meta);
		return item;
	}
}
