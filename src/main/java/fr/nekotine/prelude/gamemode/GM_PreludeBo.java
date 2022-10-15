package fr.nekotine.prelude.gamemode;

import java.util.List;
import java.util.Map;

import fr.nekotine.core.game.Game;
import fr.nekotine.core.game.GamePhase;
import fr.nekotine.core.game.GameTeam;
import fr.nekotine.core.lobby.GameModeIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class GM_PreludeBo extends Game{
	
	public static final GameModeIdentifier IDENTIFIER = new GameModeIdentifier("prelude_bo", Component.text("Match à mort en équipe par manche").color(TextColor.color(255, 60, 45))) {
		@Override
		public Game generateTypedGame() {
			return new GM_PreludeBo();
		}
	};

	@Override
	protected void asyncManageGameData() {
	}

	@Override
	protected void collectGameData() {
	}

	@Override
	protected void end() {
	}

	@Override
	protected void setup() {
	}

	@Override
	public void registerTeams(List<GameTeam> teamList) {
		teamList.add(new GameTeam(Component.translatable("color.minecraft.blue").color(NamedTextColor.BLUE)));
		teamList.add(new GameTeam(Component.translatable("color.minecraft.red").color(NamedTextColor.RED)));
	}

	@Override
	public void registerGamePhases(Map<String, GamePhase> _gamePhasesMap) {
	}

	@Override
	public void GotoFirstPhase() {
	}

}
