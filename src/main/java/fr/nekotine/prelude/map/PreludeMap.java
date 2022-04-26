package fr.nekotine.prelude.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import fr.nekotine.prelude.utils.Serializer;
import fr.nekotine.prelude.utils.Team;

@SerializableAs(Serializer.MAP_SERIALIZATION_NAME)
public class PreludeMap implements ConfigurationSerializable{
	
	private static final String NAME_FIELD = "name";
	private static final String RED_SPAWN_LOCATION_FIELD = "redSpawn";
	private static final String BLUE_SPAWN_LOCATION_FIELD = "blueSpawn";
	private static final String WALL_COUNT_FIELD = "wallCount";
	private static final String YAML_EXTENSION = ".yml";
	private static File mapFolder;
	
	private final String name;
	private final Location redSpawnLocation;
	private final Location blueSpawnLocation;
	
	private ArrayList<Wall> walls = new ArrayList<>();
	
	public PreludeMap(String name, Location redSpawnLocation, Location blueSpawnLocation) {
		this.name = name;
		this.redSpawnLocation = redSpawnLocation;
		this.blueSpawnLocation = blueSpawnLocation;
	}
	
	public static String getSerializationName() {
		return Serializer.MAP_SERIALIZATION_NAME;
	}
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(NAME_FIELD, name);
		map.put(RED_SPAWN_LOCATION_FIELD, redSpawnLocation);
		map.put(BLUE_SPAWN_LOCATION_FIELD, blueSpawnLocation);
		
		int wallCount = walls.size();
		map.put(WALL_COUNT_FIELD, wallCount); 
		for(int i=0 ; i<wallCount; i++) {
			map.put(Serializer.WALL_SERIALIZATION_PREFIX+i, walls.get(i));
		}
		return map;
	}
	public static PreludeMap deserialize(Map<String, Object> args) {	
		PreludeMap map = new PreludeMap((String)args.get(NAME_FIELD),(Location)args.get(RED_SPAWN_LOCATION_FIELD), (Location)args.get(BLUE_SPAWN_LOCATION_FIELD));
	
		int wallCount = (int)args.get(WALL_COUNT_FIELD);
		for(int i=0; i<wallCount; i++) {
			Wall wall = (Wall)args.get(Serializer.WALL_SERIALIZATION_PREFIX+i);
			if(wall!=null) map.addWall(wall);
		}
		
		return map;
	}
	public static PreludeMap load(String mapName) {
		if (mapFolder==null || !mapFolder.exists()) return null;
		File f = new File(mapFolder,mapName+YAML_EXTENSION);
		if (f.exists()) {
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(f);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
				return null;
			}
			return (PreludeMap)config.get(Serializer.MAP_SERIALIZATION_NAME);
		}
		return null;
	}
	public static void save(PreludeMap map) {
		if (mapFolder==null || !mapFolder.exists()) return;
		String name = map.getName();
		File f = new File(mapFolder,name+YAML_EXTENSION);
		try {
			f.createNewFile();
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			config.set(Serializer.MAP_SERIALIZATION_NAME, map);
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void setMapFolder(File folder) {
		mapFolder = folder;
	}
	public static ArrayList<String> getMapList() {
		ArrayList<String> finalList=new ArrayList<String>();
		if (mapFolder==null || !mapFolder.exists()) return finalList;
		String[] list = mapFolder.list();
		for (String s : list) {
			if (s.contains(YAML_EXTENSION)) {
				finalList.add(s.replace(YAML_EXTENSION, ""));
			}
		}
		return finalList;
	}
	public static boolean remove(String mapName) {
		File f = new File(mapFolder,mapName+YAML_EXTENSION);
		if (f.exists()) {
			return f.delete();
		}
		return false;
	}
	
	public static boolean remove(PreludeMap map) {
		File f = new File(mapFolder,map.getName()+YAML_EXTENSION);
		if (f.exists()) {
			return f.delete();
		}
		map.unload();
		return false;
	}
	
	public String getName() {
		return name;
	}
	public Location getRedSpawnLocation() {
		return redSpawnLocation;
	}
	public Location getBlueSpawnLocation() {
		return blueSpawnLocation;
	}
	public void addWall(Wall wall) {
		walls.add(wall);
	}
	public void enable() {
		for(Wall wall : walls) {
			wall.enable();
		}
	}
	public void unload() {
		for(Wall wall : walls) {
			wall.unload();
		}
	}
	private void teleportPlayers(Location location, ArrayList<Player> players) {
		for(Player player : players) {
			player.teleport(location);
		}
	}
	private void teleportPlayer(Location location, Player player) {
		player.teleport(location);
	}
	public void teleportPlayers(Team team, ArrayList<Player> players) {
		switch(team) {
		case RED:
			teleportPlayers(redSpawnLocation, players);
			break;
		case BLUE:
			teleportPlayers(blueSpawnLocation, players);
			break;
		}
	}
	public void teleportPlayer(Team team, Player player) {
		switch(team) {
		case RED:
			teleportPlayer(redSpawnLocation, player);
			break;
		case BLUE:
			teleportPlayer(blueSpawnLocation, player);
			break;
		}
	}
	
}
