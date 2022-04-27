package fr.nekotine.prelude.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import fr.nekotine.prelude.utils.Serializer;



@SerializableAs(Serializer.WALL_SERIALIZATION_NAME)
public class Wall implements ConfigurationSerializable{
	private static final Material WALL_MATERIAL = Material.TINTED_GLASS;
	private static final String FROM_LOCATION_FIELD = "from";
	private static final String TO_LOCATION_FIELD = "to";
	private static final String NAME_FIELD = "name";
	private boolean open;
	
	private final String name;
	private final Location from;
	private final Location to;
	public Wall(String name, Location from, Location to) {
		this.name=name;
		this.from=from;
		this.to=to;
		
		setOpen(true);
	}
	
	public static String getSerializationName() {
		return Serializer.WALL_SERIALIZATION_NAME;
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(NAME_FIELD, name);
		map.put(FROM_LOCATION_FIELD, from);
		map.put(TO_LOCATION_FIELD, to);
		return map;
	}
	public static Wall deserialize(Map<String, Object> args) {
		return new Wall((String)args.get(NAME_FIELD),(Location)args.get(FROM_LOCATION_FIELD),(Location)args.get(TO_LOCATION_FIELD));
	}

	public Location getFrom() {
		return from;
	}
	public Location getTo() {
		return to;
	}
	public String getName() {
		return name;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
		if (open) {
			clearWall();
		}else {
			buildWall();
		}
	}
	public void enable() {
		setOpen(false);
	}
	public void unload() {
		setOpen(true);
	}
	
	private void fillSpace(Material wallMaterial) {
		int x1=from.getBlockX();
		int y1=from.getBlockY();
		int z1=from.getBlockZ();
		int x2=to.getBlockX();
		int y2=to.getBlockY();
		int z2=to.getBlockZ();
		int xx=Math.max(x1,x2);
		int yy=Math.max(y1,y2);
		int zz=Math.max(z1,z2);
		int x=Math.min(x1,x2);
		World w = from.getWorld();
		for (;x<=xx;x++) {
			int y=Math.min(y1,y2);
			for (;y<=yy;y++) {
				int z=Math.min(z1,z2);
				for (;z<=zz;z++) {
					w.getBlockAt(x, y, z).setType(wallMaterial);
				}
			}
		}
	}
	private void buildWall() {
		fillSpace(WALL_MATERIAL);
	}
	private void clearWall() {
		fillSpace(Material.AIR);
	}

}
