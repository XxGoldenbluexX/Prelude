package fr.nekotine.prelude;

import java.util.ArrayList;

import org.bukkit.Material;
import fr.nekotine.prelude.effigies.*;
import me.libraryaddict.disguise.DisguiseConfig.NotifyBar;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
public enum EffigyList {
	Spider(Material.COBWEB,1,new MobDisguise(DisguiseType.SPIDER)),
	Zombie(Material.ROTTEN_FLESH, 1,new MobDisguise(DisguiseType.ZOMBIE));
	
	private final Material shopMaterial;
	private final int tier;
	private final MobDisguise disguise;
	
	EffigyList(Material shopMaterial, int tier, MobDisguise disguise) {
		this.shopMaterial=shopMaterial;
		this.tier=tier;
		this.disguise = disguise;
		disguise.setSelfDisguiseVisible(false);
		disguise.setNotifyBar(NotifyBar.NONE);
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
	public MobDisguise getDisguise() {
		return disguise;
	}
	public int getTier() {
		return tier;
	}
	public static Effigy buildEffigy(PlayerWrapper w,EffigyList effigy) {
		Effigy builded = null;
		switch(effigy) {
		case Spider:
			builded = new Spider(w);
		case Zombie:
			break;
		default:
			break;
		}
		return builded;
	}
}
