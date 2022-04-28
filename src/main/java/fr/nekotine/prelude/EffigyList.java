package fr.nekotine.prelude;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.nekotine.prelude.effigies.TestEffigy;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
public enum EffigyList {
	TestEffigy1(
			TestEffigy.class,
			ChatColor.RED+"Test",
			Material.BEDROCK,
			Main.getQuestionMarkHeadUrl(),
			1,
			DisguiseType.ARMOR_STAND
			),
	TestEffigy2(
			TestEffigy.class,
			"Test",
			Material.BEDROCK,
			Main.getQuestionMarkHeadUrl(),
			2,
			DisguiseType.ARMOR_STAND
			),
	TestEffigy3(
			TestEffigy.class,
			"Test",
			Material.BEDROCK,
			Main.getQuestionMarkHeadUrl(),
			3,
			DisguiseType.ARMOR_STAND
			);
	/*
	Spider(
			Spider.class,
			"Araignée",
			Material.COBWEB,
			Main.getQuestionMarkHeadUrl(),
			1,
			DisguiseType.SPIDER),
	Slime(
			Slime.class,
			"Slime",
			Material.SLIME_BALL, 
			Main.getQuestionMarkHeadUrl(),
			1, 
			DisguiseType.SLIME
			);
	*/
	
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
	
	EffigyList(Class<? extends Effigy> effigyClass, String name, Material weaponMaterial, String urlToHead, int tier, DisguiseType disguiseType,
				String... description) {
		this.weaponMaterial=weaponMaterial;
		this.tier=tier;
		this.disguiseType = disguiseType;
		this.effigyClass=effigyClass;
		this.urlToHead=urlToHead;
		this.name=name;
		this.description=description;
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
