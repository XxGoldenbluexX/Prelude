package fr.nekotine.prelude.effigies;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.PlayerWrapper;

public class Spider extends Effigy{

	public Spider(PlayerWrapper w) {
		super(w);
	}

	@Override
	protected void castSpell1() {
		getWrapper().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20,1,false,false,true));
	}

	@Override
	protected void castSpell2() {
		Player p = getWrapper().getPlayer();
		p.setVelocity(p.getEyeLocation().getDirection().multiply(1));
	}
	
	
	
}
