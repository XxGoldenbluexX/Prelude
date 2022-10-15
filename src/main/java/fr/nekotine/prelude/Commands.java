package fr.nekotine.prelude;

import org.bukkit.Location;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import fr.nekotine.prelude.map.PreludeMap;
import fr.nekotine.prelude.map.Wall;

public class Commands {
	public static CommandAPICommand make() {
		return new CommandAPICommand("prelude")
				.withSubcommand(game())
				.withSubcommand(map())
				.withSubcommands(test());
	}
	

	public static CommandAPICommand test() {
		return new CommandAPICommand("test")
				.executesPlayer((sender,args) -> {
					sender.setWalkSpeed(0.2f);
				});
	}
	
	
	private static CommandAPICommand game() {
		return new CommandAPICommand("game")
				.withSubcommand(gameJoin())
				.withSubcommand(gameLeave()
				.withSubcommand(gameStop()));
	}
	private static CommandAPICommand gameJoin() {
		return new CommandAPICommand("join")
				.executesPlayer((player,args)->{
					Main.getInstance().addPlayerInBestTeam(player);
					player.sendMessage("game joined");
				});
	}
	private static CommandAPICommand gameLeave() {
		return new CommandAPICommand("leave")
				.executesPlayer((player,args)->{
					Main.getInstance().removePlayer(player);
					player.sendMessage("game leaved");
				});
	}
	private static CommandAPICommand gameStop() {
		return new CommandAPICommand("stop")
				.executes((sender,args)->{
					//A ajouter
					//Main.getInstance().stop();
				});
	}
	
	
	
	private static CommandAPICommand map() {
		
		Argument<PreludeMap> mapArgument = new CustomArgument<PreludeMap, String>(new StringArgument("mapList"),(info)-> {
			PreludeMap map = PreludeMap.load(info.input());
			if (map==null) {
				throw new CustomArgumentException(new MessageBuilder("No map with this name: ").appendArgInput().appendHere());
			}else {
				return map;
			}
		}).replaceSuggestions(ArgumentSuggestions.strings(info -> {return PreludeMap.getMapNameList().toArray(String[]::new);}));
		
		Argument<String> wallNameList = new StringArgument("wallList").replaceSuggestions(ArgumentSuggestions.strings((info) -> {
			return ((PreludeMap)info.previousArgs()[0]).getWalls().stream().map(Wall::getName).toArray(String[]::new);
		}));
		
		return new CommandAPICommand("map")
				.withSubcommand(mapCreate())
				.withSubcommand(mapRemove(mapArgument))
				.withSubcommand(mapSetRedSpawn(mapArgument))
				.withSubcommand(mapSetBlueSpawn(mapArgument))
				.withSubcommand(mapRename(mapArgument))
				
				.withSubcommand(mapWallAdd(mapArgument))
				.withSubcommand(mapWallRemove(mapArgument, wallNameList));
	}
	public static CommandAPICommand mapCreate() {
		return new CommandAPICommand("create")
				.withArguments(new StringArgument("mapName"))
				.executesPlayer((player,args)->{
					String name = (String) args[0];
					if (PreludeMap.getMapNameList().contains(name)) {
						player.sendMessage("map already exist");
						//ptit message
					}else {
						PreludeMap map = new PreludeMap(name,new Location(player.getWorld(),0,0,0),new Location(player.getWorld(),0,0,0));
						PreludeMap.save(map);
						map.unload();
						player.sendMessage("map created");
						//ptit message
					}
					return 1;
				});
	}
	public static CommandAPICommand mapRemove(Argument<PreludeMap> mapArgument) {
		return new CommandAPICommand("remove")
				.withArguments(mapArgument)
				.executes((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					if (PreludeMap.remove(map)) {
						sender.sendMessage("map removed");
						//ptit message
					}else {
						sender.sendMessage("map do not exist");
						//ptit message
					}
				});
	}
	public static CommandAPICommand mapSetRedSpawn(Argument<PreludeMap> mapArgument) {
		return new CommandAPICommand("setRedSpawn")
				.withArguments(mapArgument)
				.executesPlayer((player,args)->{
					PreludeMap map = (PreludeMap)args[0];
					map.setRedSpawnLocation(player.getLocation());
					player.sendMessage("red spawn added");
					PreludeMap.save(map);
					//ptit message
					map.unload();
				});
	}
	public static CommandAPICommand mapSetBlueSpawn(Argument<PreludeMap> mapArgument) {
		return new CommandAPICommand("setBlueSpawn")
				.withArguments(mapArgument)
				.executesPlayer((player,args)->{
					PreludeMap map = (PreludeMap)args[0];
					map.setBlueSpawnLocation(player.getLocation());
					player.sendMessage("blue spawn added");
					PreludeMap.save(map);
					//ptit message
					map.unload();
				});
	}
	public static CommandAPICommand mapRename(Argument<PreludeMap> mapArgument) {
		return new CommandAPICommand("rename")
				.withArguments(mapArgument)
				.withArguments(new StringArgument("name"))
				.executes((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					String newName = (String)args[1];
					map.setName(newName);
					sender.sendMessage("map renamed");
					PreludeMap.save(map);
					//ptit message
					map.unload();
				});
	}
	public static CommandAPICommand mapWallAdd(Argument<PreludeMap> mapArgument) {
		return new CommandAPICommand("addWall")
				.withArguments(mapArgument)
				.withArguments(new StringArgument("name"))
				.withArguments(new LocationArgument("from", LocationType.BLOCK_POSITION))
				.withArguments(new LocationArgument("to", LocationType.BLOCK_POSITION))
				.executes((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					String name = (String)args[1];
					Location from = (Location)args[2];
					Location to = (Location)args[3];
					if(map.getWall(name) == null) {
						map.addWall(new Wall(name, from, to));
						sender.sendMessage("wall added");
						PreludeMap.save(map);
						//ptit message
					}else {
						sender.sendMessage("wall already exist");
						//ptit message
					}
					map.unload();
				});
	}
	public static CommandAPICommand mapWallRemove(Argument<PreludeMap> mapArgument, Argument<String> wallNameList) {
		return new CommandAPICommand("removeWall")
				.withArguments(mapArgument)
				.withArguments(wallNameList)
				.executes((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					String wallName = (String)args[1];
					Wall wall = map.getWall(wallName);
					if(wall != null) {
						map.removeWall(wall);
						sender.sendMessage("wall removed");
						PreludeMap.save(map);
						//ptit message
					}else {
						sender.sendMessage("wall do not exist");
						//ptit message
					}
					
					map.unload();
		
					
				});
	}
	
}
