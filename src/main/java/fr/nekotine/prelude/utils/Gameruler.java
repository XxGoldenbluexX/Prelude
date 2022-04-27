package fr.nekotine.prelude.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public class Gameruler {
	public static void enable() {
		World world = Bukkit.getWorlds().get(0);
		world.setGameRule(GameRule.FALL_DAMAGE, false);
		world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
	}
	public static void disable() {
		World world = Bukkit.getWorlds().get(0);
		world.setGameRule(GameRule.FALL_DAMAGE, true);
		world.setGameRule(GameRule.NATURAL_REGENERATION, true);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
	}
}
