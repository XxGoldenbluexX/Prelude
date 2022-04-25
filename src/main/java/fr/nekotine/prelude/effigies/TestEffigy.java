package fr.nekotine.prelude.effigies;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.PlayerWrapper;

public class TestEffigy extends Effigy{

	public TestEffigy(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
	}

	@Override
	protected void castPrimarySpell() {
	}

	@Override
	protected void castSecondarySpell() {
	}

}
