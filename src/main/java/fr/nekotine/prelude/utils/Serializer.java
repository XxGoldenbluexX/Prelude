package fr.nekotine.prelude.utils;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import fr.nekotine.prelude.map.Wall;

public class Serializer {
	public static final String WALL_SERIALIZATION_NAME = "Wall";
	public static final String MAP_SERIALIZATION_NAME = "Map";
	public static final String WALL_SERIALIZATION_PREFIX = WALL_SERIALIZATION_NAME+"_";
	public static final void register() {
		ConfigurationSerialization.registerClass(Wall.class, "Map");
		ConfigurationSerialization.registerClass(Wall.class, "Wall");
	}
}
