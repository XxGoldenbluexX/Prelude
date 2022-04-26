package fr.nekotine.prelude;

import org.bukkit.Location;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import dev.jorel.commandapi.arguments.StringArgument;
import fr.nekotine.prelude.map.PreludeMap;

public class Commands {
	public static CommandAPICommand make() {
		return new CommandAPICommand("prelude")
				.withSubcommand(game())
				.withSubcommand(map());
	}
	

	
	private static CommandAPICommand game() {
		return new CommandAPICommand("game")
				.withSubcommand(gameJoin())
				.withSubcommand(gameLeave()
				.withSubcommand(gameStop()));
	}
	private static CommandAPICommand gameJoin() {
		return new CommandAPICommand("join")
				.executesPlayer((sender,args)->{
					Main.getInstance().addPlayerInBestTeam(sender);
				});
	}
	private static CommandAPICommand gameLeave() {
		return new CommandAPICommand("leave")
				.executesPlayer((sender,args)->{
					Main.getInstance().removePlayer(sender);
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
		
		Argument mapArgument = new CustomArgument<PreludeMap>("mapList",(info)-> {
			PreludeMap map = PreludeMap.load(info.input());
			if (map==null) {
				throw new CustomArgumentException(new MessageBuilder("No map with this name: ").appendArgInput().appendHere());
			}else {
				return map;
			}
		}).replaceSuggestions(ArgumentSuggestions.strings(info -> {return PreludeMap.getMapNameList().toArray(String[]::new);}));
		
		return new CommandAPICommand("map")
				.withSubcommand(mapCreate())
				.withSubcommand(mapRemove(mapArgument))
				.withSubcommand(mapSetRedSpawn(mapArgument))
				.withSubcommand(mapSetBlueSpawn(mapArgument))
				.withSubcommand(mapRename(mapArgument));
	}
	public static CommandAPICommand mapCreate() {
		return new CommandAPICommand("create")
				.withArguments(new StringArgument("mapName"))
				.executesPlayer((player,args)->{
					String name = (String) args[0];
					if (PreludeMap.getMapNameList().contains(name)) {
						//ptit message
					}else {
						PreludeMap map = new PreludeMap(name,new Location(player.getWorld(),0,0,0),new Location(player.getWorld(),0,0,0));
						PreludeMap.save(map);
						map.unload();
						//ptit message
					}
				});
	}
	public static CommandAPICommand mapRemove(Argument mapArgument) {
		return new CommandAPICommand("remove")
				.withArguments(mapArgument)
				.executes((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					if (PreludeMap.remove(map)) {
						//ptit message
					}else {
						//ptit message
					}
				});
	}
	public static CommandAPICommand mapSetRedSpawn(Argument mapArgument) {
		return new CommandAPICommand("setRedSpawn")
				.withArguments(mapArgument)
				.executesPlayer((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					if(map!=null) {
						map.setRedSpawnLocation(sender.getLocation());
						//ptit message
					}else {
						//ptit message
					}
				});
	}
	public static CommandAPICommand mapSetBlueSpawn(Argument mapArgument) {
		return new CommandAPICommand("setBlueSpawn")
				.withArguments(mapArgument)
				.executesPlayer((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					if(map != null) {
						map.setBlueSpawnLocation(sender.getLocation());
						//ptit message
					}else {
						//ptit message
					}
					
				});
	}
	public static CommandAPICommand mapRename(Argument mapArgument) {
		return new CommandAPICommand("rename")
				.withArguments(mapArgument)
				.withArguments(new StringArgument("name"))
				.executesPlayer((sender,args)->{
					PreludeMap map = (PreludeMap)args[0];
					String newName = (String)args[1];
					if(map!=null) {
						map.setName(newName);
						//ptit message
					}else {
						//ptit message
					}
				});
	}
	
	
}
