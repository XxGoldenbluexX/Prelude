package fr.nekotine.prelude;

import java.util.ArrayList;

import org.bukkit.Material;
import fr.nekotine.prelude.effigies.*;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
public enum EffigyList {
	Spider(Material.COBWEB,1,Spider.class,new MobDisguise(DisguiseType.SPIDER)),
	Zombie(Material.ROTTEN_FLESH, 1,Zombie.class,new MobDisguise(DisguiseType.ZOMBIE));
	
	private final Material shopMaterial;
	private final Class<?> effigyClass;
	private final int tier;
	private final MobDisguise disguise;
	
	EffigyList(Material shopMaterial, int tier, Class<?> effigyClass, MobDisguise disguise) {
		this.shopMaterial=shopMaterial;
		this.effigyClass=effigyClass;
		this.tier=tier;
		this.disguise = disguise;
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
	public static int getMaxTier() {
		int max=1;
		for(EffigyList eff : values()) {
			if(eff.getTier()>max) {
				max=eff.getTier();
			}
		}
		return max;
	}
	public Material getShopMaterial() {
		return shopMaterial;
	}
	public Class<?> getEffigyClass() {
		return effigyClass;
	}
	public MobDisguise getDisguise() {
		return disguise;
	}
	public int getTier() {
		return tier;
	}
}
