package fr.nekotine.prelude.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.prelude.EffigyList;

public class TagInjector {
	public static void injectEffigyListTag(ItemStack itemStack, EffigyList effigy) {
		ItemMeta meta = itemStack.getItemMeta();
		
		meta.getPersistentDataContainer().set(EffigyListTagType.getNamespacedKey(), EffigyListTagType.getInstance(), effigy);
		itemStack.setItemMeta(meta);
		
		//pas besoin de return je crois
	}
	public static EffigyList extractEffigyListTag(ItemStack itemStack) {
		EffigyList effigy = (EffigyList) itemStack.getItemMeta().getPersistentDataContainer().get(EffigyListTagType.getNamespacedKey(), EffigyListTagType.getInstance());
		return effigy;
	}
}


