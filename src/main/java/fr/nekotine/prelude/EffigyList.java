package fr.nekotine.prelude;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.nekotine.prelude.effigies.Blaze;
import fr.nekotine.prelude.effigies.Creeper;
import fr.nekotine.prelude.effigies.Hoglin;
import fr.nekotine.prelude.effigies.Husk;
import fr.nekotine.prelude.effigies.Pigman;
import fr.nekotine.prelude.effigies.Skeleton;
import fr.nekotine.prelude.effigies.Slime;
import fr.nekotine.prelude.effigies.Spider;
import fr.nekotine.prelude.effigies.Trader;
import fr.nekotine.prelude.effigies.Witch;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
public enum EffigyList {
	SPIDER(
			Spider.class,
			ChatColor.DARK_GRAY+"Araignee",
			Material.COBWEB,
			"Tir empoisonne",
			"Saut",
			7 * 2,
			1.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJiNjczMmVhYTk3ODg0NTg0NjhmNTk3Njk0Mzk4NjQ2NDk3NGFmMzVmZWFmYjRmY2FkMDVhN2EzMDhjMmE2NyJ9fX0=",
			1,
			DisguiseType.SPIDER),
	SLIME(
			Slime.class,
			ChatColor.GREEN+"Slime",
			Material.SLIME_BALL,
			"Lance-Slime",
			"Propulsion",
			12 * 2,
			0.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFhZmZkMzFlZmMzN2JhODRmNTAxODczOTRkODY4ODM0NGNjZDA2Y2RjOTI2ZGRmY2YyZGYxMTY5ODZkY2E5In19fQ==",
			1,
			DisguiseType.SLIME),
	SKELETON(
			Skeleton.class,
			ChatColor.WHITE+"Squelette",
			Material.BONE,
			"Tir ameliore",
			"Aide du Wither",
			6 * 2,
			0.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAxMjY4ZTljNDkyZGExZjBkODgyNzFjYjQ5MmE0YjMwMjM5NWY1MTVhN2JiZjc3ZjRhMjBiOTVmYzAyZWIyIn19fQ==",
			1,
			DisguiseType.SKELETON),
	WITCH(
			Witch.class,
			ChatColor.LIGHT_PURPLE+"Sorciere",
			Material.NETHER_WART,
			"Breuvage",
			"Sacrifice",
			8 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNlNjYwNDE1N2ZjNGFiNTU5MWU0YmNmNTA3YTc0OTkxOGVlOWM0MWUzNTdkNDczNzZlMGVlNzM0MjA3NGM5MCJ9fX0=",
			1,
			DisguiseType.WITCH),
	HUSK(
			Husk.class,
			ChatColor.YELLOW+"Husk",
			Material.ROTTEN_FLESH,
			"Tempête",
			"Cimetière",
			10 * 2,
			1.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZGE2YjhkMDZjZDI4ZDQ0MTM5OGI5Njc2NmMzYjRmMzcwZGU4NWM3ODk4MjA1ZTVjNDI5ZjE3OGEyNDU5NyJ9fX0=",
			1,
			DisguiseType.HUSK),
	CREEPER(
			Creeper.class,
			ChatColor.DARK_GREEN+"Creeper",
			Material.GUNPOWDER,
			"Bond",
			"Explosion",
			7 * 2,
			1.25 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTliMDFkNzNhMWM5MmM0NDkxYjU3OWRmZDk3YTk5MjhhYzNjYzM2ZmRlNDMxZjlkYTA5MzJlMGY1ZWJhYjhjNSJ9fX0=",
			1,
			DisguiseType.CREEPER),
	PIGMAN(
			Pigman.class,
			ChatColor.GOLD+"Pigman",
			Material.RAW_GOLD,
			"Faux-filet",
			"Degustation",
			8 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkzNTg0MmFmNzY5MzgwZjc4ZThiOGE4OGQxZWE2Y2EyODA3YzFlNTY5M2MyY2Y3OTc0NTY2MjA4MzNlOTM2ZiJ9fX0=",
			1,
			DisguiseType.ZOMBIFIED_PIGLIN),
	BLAZE(
			Blaze.class,
			ChatColor.RED+"Blaze",
			Material.BLAZE_POWDER,
			"Lance-flamme",
			"Immolation",
			7 * 2,
			0.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIwNjU3ZTI0YjU2ZTFiMmY4ZmMyMTlkYTFkZTc4OGMwYzI0ZjM2Mzg4YjFhNDA5ZDBjZDJkOGRiYTQ0YWEzYiJ9fX0=",
			1,
			DisguiseType.BLAZE),
	HOGLIN(
			Hoglin.class,
			ChatColor.LIGHT_PURPLE+"Hoglin",
			Material.PORKCHOP,
			"Charge",
			"Elan",
			12 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJiOWJjMGYwMWRiZDc2MmEwOGQ5ZTc3YzA4MDY5ZWQ3Yzk1MzY0YWEzMGNhMTA3MjIwODU2MWI3MzBlOGQ3NSJ9fX0=",
			1,
			DisguiseType.HOGLIN),
	TRADER(
			Trader.class,
			ChatColor.AQUA+"Marchand",
			Material.EMERALD,
			"Lamasticot",
			"Echange",
			9 * 2,
			0.75 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=",
			1,
			DisguiseType.WANDERING_TRADER);
	
	private final static int TIER_1_EFFIGY_COST = 0;
	private final static int TIER_2_EFFIGY_COST = 6;
	private final static int TIER_3_EFFIGY_COST = 16;
	
	private final Material weaponMaterial;
	private final String urlToHead;
	private final int tier;
	private final DisguiseType disguiseType;
	private final Class<? extends Effigy> effigyClass;
	private final String name;
	private final String[] description;
	private final double health;
	private final double damage;
	private final String primarySpellName;
	private final String secondarySpellName;
	
	EffigyList(Class<? extends Effigy> effigyClass, String name, Material weaponMaterial, String primarySpellName, String secondarySpellName,
				int health, double damage, String urlToHead, int tier, DisguiseType disguiseType,
				String... description) {
		this.weaponMaterial=weaponMaterial;
		this.tier=tier;
		this.disguiseType = disguiseType;
		this.effigyClass=effigyClass;
		this.urlToHead=urlToHead;
		this.name=name;
		this.description=description;
		this.health=health;
		this.damage=damage;
		this.primarySpellName=primarySpellName;
		this.secondarySpellName = secondarySpellName;
	}
	
	public static ArrayList<EffigyList> getTier(int tier){
		ArrayList<EffigyList> effigies = new ArrayList<EffigyList>();
		for(EffigyList eff : values()) {
			if(eff.tier==tier) {
				effigies.add(eff);
			}
		}
		return effigies;
	}
	
	public Material getWeaponMaterial() {
		return weaponMaterial;
	}
	public DisguiseType getDisguiseType() {
		return disguiseType;
	}
	public Class<? extends Effigy> getEffigyClass(){
		return effigyClass;
	}
	public int getTier() {
		return tier;
	}
	public String getUrlToHead() {
		return urlToHead;
	}
	public String getName() {
		return name;
	}
	public int getCost() {
		return getCostFromTier(getTier());
	}
	public double getHealth() {
		return health;
	}
	public double getDamage() {
		return damage;
	}
	public String getPrimarySpellName() {
		return primarySpellName;
	}
	public String getSecondarySpellName() {
		return secondarySpellName;
	}
	public static int getCostFromTier(int tier) {
		switch(tier) {
		case 1:
			return TIER_1_EFFIGY_COST;
		case 2:
			return TIER_2_EFFIGY_COST;
		case 3:
			return TIER_3_EFFIGY_COST;
		default:
			return Integer.MAX_VALUE;
		}
	}
	
	public static Effigy buildEffigy(PlayerWrapper w, EffigyList effigy) {
		try {
			System.out.println("Building effigy");
			return (Effigy)effigy.getEffigyClass().getConstructor(PlayerWrapper.class, EffigyList.class)
			.newInstance(w, effigy);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getDescription() {
		return description;
	}
}
