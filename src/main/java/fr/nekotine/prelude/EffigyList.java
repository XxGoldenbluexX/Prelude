package fr.nekotine.prelude;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.nekotine.prelude.effigies.Blaze;
import fr.nekotine.prelude.effigies.Creeper;
import fr.nekotine.prelude.effigies.Hoglin;
import fr.nekotine.prelude.effigies.Husk;
import fr.nekotine.prelude.effigies.Pigman;
import fr.nekotine.prelude.effigies.Skeleton;
import fr.nekotine.prelude.effigies.Slime;
import fr.nekotine.prelude.effigies.SnowFox;
import fr.nekotine.prelude.effigies.Spider;
import fr.nekotine.prelude.effigies.Trader;
import fr.nekotine.prelude.effigies.Witch;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
public enum EffigyList {
	SPIDER(
			Spider.class,
			ChatColor.DARK_GRAY+"Araignée",
			Material.COBWEB,
			"Tir empoisonné",
			"Saut",
			7 * 2,
			1.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJiNjczMmVhYTk3ODg0NTg0NjhmNTk3Njk0Mzk4NjQ2NDk3NGFmMzVmZWFmYjRmY2FkMDVhN2EzMDhjMmE2NyJ9fX0=",
			1,
			DisguiseType.SPIDER,
			getPassivePrefix()+"S'accroupir permet de grimper les murs",
			getPrimaryPrefix()+"Tire une flêche empoisonnée",
			getSecondaryPrefix()+"Vous fait bondir"),
	SLIME(
			Slime.class,
			ChatColor.GREEN+"Slime",
			Material.SLIME_BALL,
			"Lance-Slime",
			"Propulsion",
			12 * 2,
			0.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFhZmZkMzFlZmMzN2JhODRmNTAxODczOTRkODY4ODM0NGNjZDA2Y2RjOTI2ZGRmY2YyZGYxMTY5ODZkY2E5In19fQ==",
			1,
			DisguiseType.SLIME,
			getPassivePrefix()+"Vous faire frapper peut créer des Slime qui soignent quand vous les ramassez",
			""+ChatColor.DARK_PURPLE+ChatColor.ITALIC+"Frapper en l'air un ennemi le ralenti",
			getPrimaryPrefix()+"Chargez pour lancer un Slime qui endommage les ennemis et fait apparaître un Slime de soin",
			getSecondaryPrefix()+"Vous propulse en l'air"),
	SKELETON(
			Skeleton.class,
			ChatColor.WHITE+"Squelette",
			Material.BONE,
			"Tir ameliore",
			"Aide du Wither",
			6 * 2,
			0.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAxMjY4ZTljNDkyZGExZjBkODgyNzFjYjQ5MmE0YjMwMjM5NWY1MTVhN2JiZjc3ZjRhMjBiOTVmYzAyZWIyIn19fQ==",
			1,
			DisguiseType.SKELETON,
			getPassivePrefix()+"Charger l'arc crée une salve de flèches",
			getPrimaryPrefix()+"Améliore le prochain tir d'arc",
			getSecondaryPrefix()+"Lance une tête de wither explosive"),
	WITCH(
			Witch.class,
			ChatColor.LIGHT_PURPLE+"Sorcière",
			Material.NETHER_WART,
			"Breuvage",
			"Sacrifice",
			8 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNlNjYwNDE1N2ZjNGFiNTU5MWU0YmNmNTA3YTc0OTkxOGVlOWM0MWUzNTdkNDczNzZlMGVlNzM0MjA3NGM5MCJ9fX0=",
			1,
			DisguiseType.WITCH,
			getPassivePrefix()+"Des chauves-souris apparaîssent passivement toutes les quelques secondes",
			getPrimaryPrefix()+"Lance une potion qui soigne les alliés et blesse les ennemis",
			getSecondaryPrefix()+"Sacrifie toutes vos chauves-souris pour faire apparaître des potions de soins & de vitesses pour les alliés...",
			""+ChatColor.GOLD+ChatColor.ITALIC+"ou de dégâts & de ralentissement pour les ennemis"),
	HUSK(
			Husk.class,
			ChatColor.YELLOW+"Husk",
			Material.ROTTEN_FLESH,
			"Tempête",
			"Cimetière",
			10 * 2,
			1.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZGE2YjhkMDZjZDI4ZDQ0MTM5OGI5Njc2NmMzYjRmMzcwZGU4NWM3ODk4MjA1ZTVjNDI5ZjE3OGEyNDU5NyJ9fX0=",
			1,
			DisguiseType.HUSK,
			getPassivePrefix()+"Vous et vos zombies infligez Faiblesse à chaque attaques",
			getPrimaryPrefix()+"Vous et vos zombies créez des tempête de sables qui aveuglent les ennemis",
			getSecondaryPrefix()+"Crée un cimetière qui fait apparaître des zombies dans une zone"),
	CREEPER(
			Creeper.class,
			ChatColor.DARK_GREEN+"Creeper",
			Material.GUNPOWDER,
			"Bond",
			"Explosion",
			7 * 2,
			1.25 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTliMDFkNzNhMWM5MmM0NDkxYjU3OWRmZDk3YTk5MjhhYzNjYzM2ZmRlNDMxZjlkYTA5MzJlMGY1ZWJhYjhjNSJ9fX0=",
			1,
			DisguiseType.CREEPER,
			getPassivePrefix()+"Vous créez une petite explosion lorsque vous vous faites frapper",
			getPrimaryPrefix()+"Vous fait bondir dans la direction souhaitée",
			getSecondaryPrefix()+"Charge une explosion infligeant de lourds dégâts"),
	PIGMAN(
			Pigman.class,
			ChatColor.GOLD+"Pigman",
			Material.RAW_GOLD,
			"Faux-filet",
			"Degustation",
			8 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkzNTg0MmFmNzY5MzgwZjc4ZThiOGE4OGQxZWE2Y2EyODA3YzFlNTY5M2MyY2Y3OTc0NTY2MjA4MzNlOTM2ZiJ9fX0=",
			1,
			DisguiseType.ZOMBIFIED_PIGLIN,
			getPassivePrefix()+"Frapper des ennemis octroie des côtelettes qui augmentent vos dégâts",
			getPrimaryPrefix()+"Lance une côtelette afin de frapper et de bondir sur un ennemi",
			getSecondaryPrefix()+"Consomme toutes vos côtelettes afin de vous régénérer"),
	BLAZE(
			Blaze.class,
			ChatColor.RED+"Blaze",
			Material.BLAZE_POWDER,
			"Lance-flamme",
			"Immolation",
			7 * 2,
			0.5 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIwNjU3ZTI0YjU2ZTFiMmY4ZmMyMTlkYTFkZTc4OGMwYzI0ZjM2Mzg4YjFhNDA5ZDBjZDJkOGRiYTQ0YWEzYiJ9fX0=",
			1,
			DisguiseType.BLAZE,
			getPassivePrefix()+"Re-appuyer 2 fois sur la touche de saut réalise un double saut, vous avez léviation permanent",
			getPrimaryPrefix()+"Lance une boule de feu stockée",
			getSecondaryPrefix()+"Consomme des points de vie pour faire apparaître un Blaze allié"),
	HOGLIN(
			Hoglin.class,
			ChatColor.LIGHT_PURPLE+"Hoglin",
			Material.PORKCHOP,
			"Charge",
			"Elan",
			12 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJiOWJjMGYwMWRiZDc2MmEwOGQ5ZTc3YzA4MDY5ZWQ3Yzk1MzY0YWEzMGNhMTA3MjIwODU2MWI3MzBlOGQ3NSJ9fX0=",
			1,
			DisguiseType.HOGLIN,
			getPassivePrefix()+"Vos alliés peuvent monter sur votre dos",
			getPrimaryPrefix()+"Charge en avant sur quelques blocs en projetant en l'air les personnes touchées",
			getSecondaryPrefix()+"Augmente votre vitesse et améliore votre prochaine attaque"),
	TRADER(
			Trader.class,
			ChatColor.AQUA+"Marchand",
			Material.EMERALD,
			"Lamasticot",
			"Echange",
			9 * 2,
			0.75 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=",
			1,
			DisguiseType.WANDERING_TRADER,
			getPassivePrefix()+"Votre lama soigne en zone les alliés et ralenti les ennemis",
			getPrimaryPrefix()+"Fait apparaître un lama",
			getSecondaryPrefix()+"Echange votre position avec votre lama, vous rendant invisible pendant quelques secondes"),
	SNOWFOX(
			SnowFox.class,
			ChatColor.WHITE+"Renard De Glace",
			Material.ICE,
			"Stalagmite",
			"Potit Ronard",
			8 * 2,
			1 * 2,
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRkNjQ1NTlmNjhmYmNlMzM2NjI0ZTEzNDI5NTBhZWE5ZjEzNTQ5NGMzZWMxNjIwOGIzZDNjY2Y5NGNmMzBlNCJ9fX0=",
			1,
			DisguiseType.FOX,
			getPassivePrefix()+"Taper des ennemis ralentis ou gelés inflige + de dégâts",
			getPrimaryPrefix()+"Fait pousser un stalagmite qui gele les ennemis touchés",
			getSecondaryPrefix()+"Lance un renard qui vous fait bondir s'il touche un ennemi");
	
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
	private final double health;
	private final double damage;
	private final String primarySpellName;
	private final String secondarySpellName;
	
	EffigyList(Class<? extends Effigy> effigyClass, String name, Material weaponMaterial, String primarySpellName, String secondarySpellName,
				int health, double damage, String urlToHead, int tier, DisguiseType disguiseType,
				String... description) {
		this.weaponMaterial=weaponMaterial;
		this.tier=tier;
		this.disguiseType = disguiseType;
		this.effigyClass=effigyClass;
		this.urlToHead=urlToHead;
		this.name=name;
		this.health=health;
		this.damage=damage;
		this.primarySpellName=primarySpellName;
		this.secondarySpellName = secondarySpellName;
		this.description = join(getStatString(), description);
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
	public double getHealth() {
		return health;
	}
	public double getDamage() {
		return damage;
	}
	public String getPrimarySpellName() {
		return primarySpellName;
	}
	public String getSecondarySpellName() {
		return secondarySpellName;
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
	
	//
	
	private static String[] join(String first, String... next) {
		String[] joined = new String[next.length + 1];
		joined[0] = first;
		for(int i = 0 ; i < next.length ; i++) {
			joined[i + 1] = next[i];
		}
		return joined;
	}
	private String getStatString() {
		return ChatColor.RED+"❤ * " + (health / 2) + ChatColor.WHITE+" | " + ChatColor.RED+"🗡 * " + (damage / 2) + "♥";
	}
	private static String getPassivePrefix() {
		return  ChatColor.DARK_PURPLE+"Passif" + ChatColor.WHITE+" ▶ " + ChatColor.DARK_PURPLE+ChatColor.ITALIC;
	}
	private static String getPrimaryPrefix() {
		return ChatColor.GOLD+"   ❶   " + ChatColor.WHITE+" ▶ " + ChatColor.GOLD+ChatColor.ITALIC;
	}
	private static String getSecondaryPrefix() {
		return ChatColor.GOLD+"   ❷   " + ChatColor.WHITE+" ▶ " + ChatColor.GOLD+ChatColor.ITALIC;
	}
}
