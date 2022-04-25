package fr.nekotine.prelude.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class ComponentMaker {
	public static Component getComponent(String text) {
		return Component.text(text);
	}
	public static String getText(Component component) {
		return ((TextComponent)component).content();
	}
}
