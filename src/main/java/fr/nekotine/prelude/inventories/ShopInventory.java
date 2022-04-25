package fr.nekotine.prelude.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.prelude.utils.ItemStackMaker;

public class ShopInventory extends BaseInventory{

	private static final String TITLE = "Boutique";
	private static final int SIZE = 9*6;
	
	private static final ItemStack VOID_GLASS = ItemStackMaker.make(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
	private static final int[] VOID_GLASS_SLOTS = {1, 10, 19};
	
	public ShopInventory(Player holder) {
		super(holder, TITLE, SIZE);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	}

}
