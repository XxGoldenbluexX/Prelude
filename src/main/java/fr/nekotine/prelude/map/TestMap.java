package fr.nekotine.prelude.map;

import org.bukkit.Location;

import fr.nekotine.core.map.GameMap;
import fr.nekotine.core.map.MapIdentifier;
import fr.nekotine.core.map.MapTypeIdentifier;
import fr.nekotine.core.map.annotation.ComposingMap;
import fr.nekotine.core.map.component.PlaceMapElement;

public class TestMap extends GameMap{

	public static MapTypeIdentifier IDENTIFIER = new MapTypeIdentifier("TestMap", TestMap.class) {

		@Override
		public GameMap generateTypedMap(MapIdentifier id) {
			return new TestMap(id);
		}
		
	};
	
	public TestMap(MapIdentifier id) {
		super(id);
	}
	
	@ComposingMap(Name = "Spawn")
	private PlaceMapElement spawnPlace = new PlaceMapElement(this, "Spawn");
	
	public Location getSpawnLocation() {
		return spawnPlace.getValue();
	}

}
