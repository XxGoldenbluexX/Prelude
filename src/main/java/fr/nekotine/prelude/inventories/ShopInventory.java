package fr.nekotine.prelude.inventories;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.events.PlayerChangeEffigyEvent;
import fr.nekotine.prelude.events.PlayerChangeMoneyEvent;
import fr.nekotine.prelude.utils.ItemStackMaker;
import fr.nekotine.prelude.utils.TagInjector;
import net.md_5.bungee.api.ChatColor;

public class ShopInventory extends BaseInventory{

	private static final String TITLE = "Boutique";
	private static final int SIZE = 9*6;
	
	private static final ItemStack VOID_GLASS = ItemStackMaker.make(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
	private static final int[] VOID_GLASS_SLOTS = {18, 27, 36};
	
	private static final ItemStack TIER_1_GLASS = ItemStackMaker.make(Material.CYAN_STAINED_GLASS_PANE, 1, ChatColor.GOLD+Integer.toString(EffigyList.getCostFromTier(1))+"$");
	private static final int[] TIER_1_GLASS_SLOTS = {1, 10};
	
	private static final ItemStack TIER_2_GLASS = ItemStackMaker.make(Material.PURPLE_STAINED_GLASS_PANE, 1, ChatColor.GOLD+Integer.toString(EffigyList.getCostFromTier(2))+"$");
	private static final int[] TIER_2_GLASS_SLOTS = {19, 28};
	
	private static final ItemStack TIER_3_GLASS = ItemStackMaker.make(Material.ORANGE_STAINED_GLASS_PANE, 1, ChatColor.GOLD+Integer.toString(EffigyList.getCostFromTier(3))+"$");
	private static final int[] TIER_3_GLASS_SLOTS = {37, 46};
	
	private static final ItemStack BACK_ITEM = ItemStackMaker.make(Material.BARRIER, 1, ChatColor.RED+"Retour");
	private static final int BACK_SLOT = 0;
	
	private static final int MONEY_SLOT = 45;
	
	private static final ChatColor DEFAULT_COLOR = ChatColor.RED;
	
	private static final int TIER_1_STARTING_SLOT = 2;
	private static final ChatColor TIER_1_COLOR = ChatColor.BLUE;
	
	private static final int TIER_2_STARTING_SLOT = 20;
	private static final ChatColor TIER_2_COLOR = ChatColor.LIGHT_PURPLE;
	
	private static final int TIER_3_STARTING_SLOT = 38;
	private static final ChatColor TIER_3_COLOR = ChatColor.GOLD;
	
	private static final int EFFIGY_SLOT = 9;
	
	public ShopInventory(Player holder, EffigyList defaultEffigy) {
		super(holder, TITLE, SIZE);
		
		setItems(VOID_GLASS, VOID_GLASS_SLOTS);
		setItems(TIER_1_GLASS, TIER_1_GLASS_SLOTS);
		setItems(TIER_2_GLASS, TIER_2_GLASS_SLOTS);
		setItems(TIER_3_GLASS, TIER_3_GLASS_SLOTS);
		
		setItem(BACK_ITEM, BACK_SLOT);
		
		setMoneyItem();
		placeEffigy(defaultEffigy, EFFIGY_SLOT);
		placeEffigies();
	}
	
	private void placeEffigies() {
		ArrayList<EffigyList> tier_1_effigies = EffigyList.getTier(1);
		ArrayList<EffigyList> tier_2_effigies = EffigyList.getTier(2);
		ArrayList<EffigyList> tier_3_effigies = EffigyList.getTier(3);
		
		placeEffigiesOfTier(tier_1_effigies, TIER_1_STARTING_SLOT);
		placeEffigiesOfTier(tier_2_effigies, TIER_2_STARTING_SLOT);
		placeEffigiesOfTier(tier_3_effigies, TIER_3_STARTING_SLOT);
	}
	private void placeEffigiesOfTier(ArrayList<EffigyList> effigies, int startingSlot) {
		for(EffigyList effigy : effigies) {
			
			if(startingSlot%9==0) startingSlot+=2;
			placeEffigy(effigy, startingSlot);
			
			startingSlot++;
			if(startingSlot>53) return;
		}
	}
	private void placeEffigy(EffigyList effigy, int slot) {
		ItemStack effigy_head = ItemStackMaker.makeHead(effigy.getUrlToHead(), getColorFromTier(effigy.getTier())+effigy.getName(), 1);
		TagInjector.injectEffigyListTag(effigy_head, effigy);
		setItem(effigy_head, slot);
	}
	private ChatColor getColorFromTier(int tier) {
		switch(tier) {
		case 1:
			return TIER_1_COLOR;
		case 2:
			return TIER_2_COLOR;
		case 3:
			return TIER_3_COLOR;
		default:
			return DEFAULT_COLOR;
		}
	}
	private void setMoneyItem() {
		int money = Main.getInstance().getWrapper(getHolder()).getMoney();
		ItemStack money_item = ItemStackMaker.make(Material.GOLD_INGOT, 1, ChatColor.GOLD+Integer.toString(money)+"$");
		setItem(money_item, MONEY_SLOT);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack clicked = e.getCurrentItem();
		EffigyList effigy = TagInjector.extractEffigyListTag(clicked);
		if(effigy!=null) Main.getInstance().getWrapper(getHolder()).buyEffigy(effigy);
	}
	
	@EventHandler
	public void onPlayerChangeEffigy(PlayerChangeEffigyEvent e) {
		if(e.getPlayer().equals(getHolder())) {
			placeEffigy(e.getAfter(), EFFIGY_SLOT);
		}
	}
	@EventHandler
	public void onMoneyChange(PlayerChangeMoneyEvent e) {
		if(e.getPlayer().equals(getHolder())) {
			setMoneyItem();
		}
	}

}
