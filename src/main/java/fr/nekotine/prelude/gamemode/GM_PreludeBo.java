package fr.nekotine.prelude.gamemode;

import fr.nekotine.core.lobby.GameMode;
import fr.nekotine.core.minigame.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GM_PreludeBo extends Game{
	
	public static final GameMode IDENTIFIER = new GameMode("prelude_bo", Component.text("Match à mort en équipe par manche").color(TextColor.color(255, 60, 45))) {//TODO
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

}
