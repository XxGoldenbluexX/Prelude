package fr.nekotine.prelude.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.events.PlayerChangeTeamEvent;
import fr.nekotine.prelude.utils.ItemStackMaker;
import net.md_5.bungee.api.ChatColor;

public class MenuInventory extends BaseInventory{
	
	private static final String TITLE = "Menu";
	private static final int SIZE = 9*3;
	
	private static final ItemStack VOID_GLASS = ItemStackMaker.make(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
	
	private static final ItemStack MAP_ITEM = ItemStackMaker.make(Material.BOOK, 1, ChatColor.AQUA+"Carte");
	private static final int MAP_SLOT = 11;
	
	private static final ItemStack START_ITEM = ItemStackMaker.make(Material.SUNFLOWER, 1, ChatColor.GOLD+"Lancer");
	private static final int START_SLOT = 13;
	
	private static final ItemStack RED_TEAM_ITEM = ItemStackMaker.make(Material.RED_CONCRETE, 1, ChatColor.AQUA+"Equipe Rouge");
	private static final ItemStack BLUE_TEAM_ITEM = ItemStackMaker.make(Material.BLUE_CONCRETE, 1, ChatColor.AQUA+"Equipe Bleue");
	private static final int TEAM_SLOT = 15;

	private final PlayerWrapper wrapper;
	
	public MenuInventory(Player holder) {
		super(holder, TITLE, SIZE);
		wrapper = Main.getInstance().getWrapper(getHolder());
		
		fillVoidGlass();
		
		setItem(MAP_ITEM, MAP_SLOT);
		setItem(START_ITEM, START_SLOT);
		setTeamItem();
	}
	private void fillVoidGlass() {
		for(int i=0; i<SIZE; i++) {
			setItem(VOID_GLASS, i);
		}
	}
	private void setTeamItem() {
		switch(wrapper.getTeam()) {
		case RED:
			setItem(RED_TEAM_ITEM, TEAM_SLOT);
			break;
		case BLUE:
			setItem(BLUE_TEAM_ITEM, TEAM_SLOT);
			break;
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e) {
		switch(e.getSlot()) {
		case MAP_SLOT:
			Main.getInstance().openMapInventory(getHolder());
			break;
		case START_SLOT:
			Main.getInstance().start();
			break;
		case TEAM_SLOT:
			Main.getInstance().swapTeam(getHolder());
			break;
		}
	}
	
	@EventHandler
	public void onTeamChange(PlayerChangeTeamEvent e) {
		if(e.getPlayer().equals(getHolder())) {
			setTeamItem();
		}
	}

}
