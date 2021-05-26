package fr.nekotine.prelude;

import java.util.ArrayList;

import org.bukkit.Material;
import fr.nekotine.prelude.effigies.*;
public enum Effigy {
	Spider(Material.COBWEB,1,Spider.class),
	Zombie(Material.ROTTEN_FLESH, 1,Zombie.class);
	public final Material shopMaterial;
	public final Class<?> effigyClass;
	public final int tier;
	Effigy(Material shopMaterial, int tier, Class<?> effigyClass) {
		this.shopMaterial=shopMaterial;
		this.effigyClass=effigyClass;
		this.tier=tier;
	}
	public static ArrayList<Effigy> getTier(int tier){
		ArrayList<Effigy> effigies = new ArrayList<Effigy>();
		for(Effigy eff : values()) {
			if(eff.tier==tier) {
				effigies.add(eff);
			}
		}
		return effigies;
	}
	public static int getMaxTier() {
		int max=1;
		for(Effigy eff : values()) {
			if(eff.tier>max) {
				max=eff.tier;
			}
		}
		return max;
	}
}
