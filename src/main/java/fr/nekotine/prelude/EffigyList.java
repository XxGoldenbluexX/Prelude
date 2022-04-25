package fr.nekotine.prelude;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.Material;

import fr.nekotine.prelude.effigies.Slime;
import fr.nekotine.prelude.effigies.Spider;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
public enum EffigyList {
	
	Spider(
			Spider.class,
			Material.COBWEB,
			Main.getQuestionMarkHeadUrl(),
			1,
			DisguiseType.SPIDER),
	Slime(
			Slime.class,
			Material.SLIME_BALL, 
			Main.getQuestionMarkHeadUrl(),
			1, 
			DisguiseType.SLIME
			);
	private final Material weaponMaterial;
	private final String urlToHead;
	private final int tier;
	private final DisguiseType disguiseType;
	private final Class<? extends Effigy> effigyClass;
	
	EffigyList(Class<? extends Effigy> effigyClass, Material weaponMaterial, String urlToHead, int tier, DisguiseType disguiseType) {
		this.weaponMaterial=weaponMaterial;
		this.tier=tier;
		this.disguiseType = disguiseType;
		this.effigyClass=effigyClass;
		this.urlToHead=urlToHead;
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
	
	public static Effigy buildEffigy(PlayerWrapper w, EffigyList effigy) {
		try {
			return (Effigy)effigy.getEffigyClass().getConstructor(PlayerWrapper.class, EffigyList.class)
			.newInstance(effigy.getEffigyClass(), effigy.getWeaponMaterial());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
