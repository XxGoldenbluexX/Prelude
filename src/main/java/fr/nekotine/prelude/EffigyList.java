package fr.nekotine.prelude;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.nekotine.prelude.effigies.Creeper;
import fr.nekotine.prelude.effigies.Skeleton;
import fr.nekotine.prelude.effigies.Slime;
import fr.nekotine.prelude.effigies.Spider;
import fr.nekotine.prelude.effigies.Witch;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
public enum EffigyList {
	SPIDER(
			Spider.class,
			ChatColor.DARK_GRAY+"Araignee",
			Material.COBWEB,
			"Tir empoisonne",
			"Saut",
			14,
			1.5,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJiNjczMmVhYTk3ODg0NTg0NjhmNTk3Njk0Mzk4NjQ2NDk3NGFmMzVmZWFmYjRmY2FkMDVhN2EzMDhjMmE2NyJ9fX0=",
			1,
			DisguiseType.SPIDER
			),
	SLIME(
			Slime.class,
			ChatColor.GREEN+"Slime",
			Material.SLIME_BALL,
			"Lance-Slime",
			"Propulsion",
			24,
			1,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFhZmZkMzFlZmMzN2JhODRmNTAxODczOTRkODY4ODM0NGNjZDA2Y2RjOTI2ZGRmY2YyZGYxMTY5ODZkY2E5In19fQ==",
			1,
			DisguiseType.SLIME
			),
	SKELETON(
			Skeleton.class,
			ChatColor.WHITE+"Squelette",
			Material.BONE,
			"Tir ameliore",
			"Aide du Wither",
			12,
			1,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ3YjFkNGVhYmYzNTM1MDM4MmI0NjU2NDk5NjRhNGY1YWQ4MWZiYzBjOWY0MTQ5NjM0ODI5ZGI4M2Q2OWEzIn19fQ==",
			1,
			DisguiseType.SKELETON
			),
	WITCH(
			Witch.class,
			ChatColor.LIGHT_PURPLE+"Sorciere",
			Material.NETHER_WART,
			"Breuvage",
			"Sacrifice",
			16,
			2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNlNjYwNDE1N2ZjNGFiNTU5MWU0YmNmNTA3YTc0OTkxOGVlOWM0MWUzNTdkNDczNzZlMGVlNzM0MjA3NGM5MCJ9fX0=",
			1,
			DisguiseType.WITCH
			),
	CREEPER(
			Creeper.class,
			ChatColor.GREEN+"Creeper",
			Material.GUNPOWDER,
			"Bond",
			"Explosion",
			14,
			1.25,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTliMDFkNzNhMWM5MmM0NDkxYjU3OWRmZDk3YTk5MjhhYzNjYzM2ZmRlNDMxZjlkYTA5MzJlMGY1ZWJhYjhjNSJ9fX0=",
			2,
			DisguiseType.CREEPER
			);
	
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
