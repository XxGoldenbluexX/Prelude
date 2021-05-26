package fr.nekotine.prelude;

import org.bukkit.Material;
import fr.nekotine.prelude.effigies.*;
public enum Effigy {
	Spider(Material.STONE, Spider.class);
	public final Material shopMaterial;
	public final Class<?> effigyClass;
	Effigy(Material shopMaterial, Class<?> effigyClass) {
		this.shopMaterial=shopMaterial;
		this.effigyClass=effigyClass;
	}
}
