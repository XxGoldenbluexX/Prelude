package fr.nekotine.prelude.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;

public class EffigyListTagType implements PersistentDataType<String, EffigyList>{
	private static final EffigyListTagType instance = new EffigyListTagType();
	public static EffigyListTagType getInstance() {
		return instance;
	}
	
	private static final String namespacedKey = "EffigyTag";
	public static NamespacedKey getNamespacedKey() {
		return new NamespacedKey(Main.getInstance(), namespacedKey);
	}
	@Override
	public Class<String> getPrimitiveType() {
		return String.class;
	}

	@Override
	public Class<EffigyList> getComplexType() {
		return EffigyList.class;
	}

	@Override
	public String toPrimitive(EffigyList complex, PersistentDataAdapterContext context) {
		return complex.toString();
	}
	
	@Override
	public EffigyList fromPrimitive(String primitive, PersistentDataAdapterContext context) {
		return EffigyList.valueOf(primitive);
	}
}
