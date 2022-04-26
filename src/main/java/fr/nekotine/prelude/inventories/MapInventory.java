package fr.nekotine.prelude.inventories;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.events.MapChangeEvent;
import fr.nekotine.prelude.map.YamlReader;
import fr.nekotine.prelude.utils.ComponentMaker;
import fr.nekotine.prelude.utils.EventRegisterer;
import fr.nekotine.prelude.utils.ItemStackMaker;
import net.md_5.bungee.api.ChatColor;

public class MapInventory extends BaseInventory{
	private static final String TITLE = "Carte";
	private static final int SIZE = 9*3;
	
	private final int mapNumber;
	
	private static final ItemStack VOID_GLASS = ItemStackMaker.make(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
	private static final int[] VOID_GLASS_SLOTS = {1, 10, 19};
	
	private static final ItemStack BACK_ITEM = ItemStackMaker.make(Material.BARRIER, 1, ChatColor.RED+"Retour");
	private static final int BACK_SLOT = 0;
	
	private static final int CURRENT_MAP_SLOT = 9;
	
	private static final ItemStack RANDOMIZE_MAP_ITEM = ItemStackMaker.make(Material.NOTE_BLOCK, 1, ChatColor.LIGHT_PURPLE+"Aleatoire");
	private static final int RANDOMIZE_MAP_SLOT = 18;
	public MapInventory() {
		super(null, TITLE, SIZE);
		setItems(VOID_GLASS, VOID_GLASS_SLOTS);
		
		setItem(BACK_ITEM, BACK_SLOT);
		setItem(RANDOMIZE_MAP_ITEM, RANDOMIZE_MAP_SLOT);
		
		ArrayList<String> mapNameList = YamlReader.getMapNameList();
		mapNumber = mapNameList.size();
		placeMapItems(mapNameList);
		
		setCurrentMapItem(Main.getInstance().getMapName());
	}
	
	private void placeMapItems(ArrayList<String> mapNameList) {
		int slot = 2;
		for(String mapName : mapNameList) {
			ItemStack mapItem = ItemStackMaker.make(Material.PAPER, 1, ChatColor.AQUA+mapName);
			if(slot%9==0) slot+=2;
			setItem(mapItem, slot);
			slot++;
			if(slot>26) return;
		}
	}
	private void randomizeMap(InventoryView inventoryView) {
		int chosenMapNumber = (int) Math.round(Math.random()*mapNumber);
		int chosenMapSlot = 2 + chosenMapNumber;
		if(chosenMapSlot>8) chosenMapSlot+=2;
		if(chosenMapSlot>17) chosenMapSlot+=2;
		
		EventRegisterer.callInventoryClickEvent(inventoryView, SlotType.CONTAINER, chosenMapSlot, ClickType.UNKNOWN, InventoryAction.COLLECT_TO_CURSOR);
	}
	private void setCurrentMapItem(String mapName) {
		ItemStack current_map_item = ItemStackMaker.make(Material.BOOK, 1, ChatColor.AQUA+mapName);
		setItem(current_map_item, CURRENT_MAP_SLOT);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getCurrentItem() !=null && e.getCurrentItem().getType()==Material.PAPER) {
			String mapName = ComponentMaker.getText(e.getCurrentItem().displayName());
			Main.getInstance().setMapName(mapName);
		}else {
			switch(e.getSlot()) {
			case BACK_SLOT:
				Main.getInstance().getWrapper((Player) e.getWhoClicked()).openMenuInventory();
				return;
			case RANDOMIZE_MAP_SLOT:
				randomizeMap(e.getView());
				return;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onMapChange(MapChangeEvent e) {
		setCurrentMapItem(e.getAfter());
	}
}
