package fr.nekotine.prelude.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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
	public static ItemStack makeHead(String name, String url, int quantity, String... lore) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		
		headMeta.displayName(ComponentMaker.getComponent(name));
		List<Component> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(ComponentMaker.getComponent(line));
			}
		}
		headMeta.lore(loreList);
		
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
           
        }
        head.setItemMeta(headMeta);
        return head;
	}
}
