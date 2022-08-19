package fr.nekotine.prelude.effigies;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.MobGoals;

import fr.nekotine.core.charge.ICharge;
import fr.nekotine.core.usable.Usable;
import fr.nekotine.core.util.UtilMobAi;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.ai.WitchBatFollowGoal;
import fr.nekotine.prelude.utils.Ability;
import net.kyori.adventure.text.Component;

public class Witch extends Effigy implements ICharge{
	private static final int PRIMARY_COOLDOWN = 3 * 20;
	private static final int SECONDARY_COOLDOWN = 0 * 20;
	
	private static final long PASSIVE_BAT_GENERATION = 5 * 1000;
	private static final Color DAMAGE_POTION_COLOR = Color.fromRGB(68, 10, 9);
	private static final Color SLOW_POTION_COLOR = Color.GRAY;
	private static final PotionEffect SLOW_POTION_EFFECT = new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1, false, false, true);
	private static final PotionEffect SPEED_POTION_EFFECT = new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0, false, false, true);
	private static final long BAT_CAP = 5;
	private static final double PRIMARY_DAMAGE = 1 * 2;
	private static final double PRIMARY_HEAL = 0.5 * 2;
	private static final Component SECONDARY_POTION_NAME = Component.text("WitchSecondary");
	private static final String CHARGE_NAME = "WitchPassive";
	private static final Material BAT_INDICATOR_MATERIAL = Material.DRIED_KELP;
	
	private final ArrayList<Bat> bats = new ArrayList<>();
	
	private final Usable batIndicator;
	
	//
	
	public Witch(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		batIndicator = new Usable(Main.getInstance().getUsableModule(), new ItemStack(BAT_INDICATOR_MATERIAL)) {
			@Override
			protected void OnDrop(PlayerDropItemEvent e) {
				e.setCancelled(true);
			}
		};
		batIndicator.SetName("Chauve-souris");
		batIndicator.Give(getWrapper().getPlayer().getInventory());
		batIndicator.register();
	}

	//
	
	@Override
	protected boolean castPrimarySpell() {
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		Player player = getWrapper().getPlayer();
		player.getWorld().playSound(player, Sound.ENTITY_WITCH_THROW, 1, 0);
		ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
		PotionMeta meta = potion.getPotionMeta();
		meta.setColor(DAMAGE_POTION_COLOR);
		potion.setPotionMeta(meta);
		return true;
	}
	@Override
	protected boolean castSecondarySpell() {
		if(bats.size() < 1) return false;
		
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		Player player = getWrapper().getPlayer();
		for(int i=0 ; i < bats.size() ; i++) {
			Bat bat = bats.get(i);
			
			ThrownPotion potion = bat.launchProjectile(ThrownPotion.class);
			potion.setShooter(player);
			potion.setVelocity(new Vector());
			potion.customName(SECONDARY_POTION_NAME);
			
			PotionMeta meta = potion.getPotionMeta();
			if(i % 2 == 0) {
				meta.setColor(DAMAGE_POTION_COLOR);
			}else {
				meta.setColor(SLOW_POTION_COLOR);
			}
			potion.setPotionMeta(meta);
			
			bat.remove();
		}
		bats.clear();
		batIndicator.SetAmount(1);
		batIndicator.SetEnchantedGlow(false);
		
		player.getWorld().playSound(player, Sound.ENTITY_WITCH_CELEBRATE, 1, 0);
		
		if(!Main.getInstance().getChargeModule().Exist(player.getName(), CHARGE_NAME)) AddCharge();
		
		return true;
		
	}
	@Override
	protected void roundEnd() {
		CancelCharge();
		RemoveBats();
	}
	@Override
	protected void death() {
		CancelCharge();
		RemoveBats();
	}
	@Override
	protected void destroy() {
		CancelCharge();
		RemoveBats();
		batIndicator.Remove();
		batIndicator.unregister();
		super.destroy();
	}
	@Override
	protected void roundStart() {
		AddCharge();
	}
	
	//
	
	@EventHandler
	public void OnPotion(PotionSplashEvent e) {
		Player player = getWrapper().getPlayer();
		if(player.equals( e.getPotion().getShooter()) ) {
			for(LivingEntity hit : e.getAffectedEntities()) {
				if(hit instanceof Bat) continue;
				
				if(DAMAGE_POTION_COLOR.equals(e.getPotion().getPotionMeta().getColor())) {
					
					double damage = PRIMARY_DAMAGE;
					if(hit instanceof Player && Main.getInstance().inSameTeam(player, (Player)hit)) damage = -PRIMARY_HEAL;
					Main.getInstance().getDamageModule().Damage(
							hit,
							player, 
							e.getPotion(),
							DamageCause.CUSTOM, 
							damage, 
							true, 
							false,
							null);
					if(!SECONDARY_POTION_NAME.equals(e.getPotion().customName()) && !player.equals(hit)) SummonBat();

				}else if(SLOW_POTION_COLOR.equals(e.getPotion().getPotionMeta().getColor())) {
					if(hit instanceof Player && Main.getInstance().inSameTeam(player, (Player)hit)) {
						hit.addPotionEffect(SPEED_POTION_EFFECT);
					}else {
						hit.addPotionEffect(SLOW_POTION_EFFECT);
					}
					
				}
			}
		}
	}
	@EventHandler
	public void OnDeath(EntityDeathEvent e) {
		if(bats.contains(e.getEntity())) {
			bats.remove(e.getEntity());
		}
	}
	
	//

	@Override
	public void Cancelled(String arg0, String arg1, long arg2) {	
	}
	@Override
	public void Ended(String arg0, String arg1) {
		SummonBat();
		AddCharge();
	}
	
	//
	
	private void SummonBat() {
		Player player = getWrapper().getPlayer();
		if(bats.size() < BAT_CAP) {
			Bat bat = (Bat)player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
			bat.setSilent(true);
			bat.setInvulnerable(true);
			bat.setCollidable(false);
			bat.setLeashHolder(getWrapper().getPlayer());
			// --- AI part
			UtilMobAi.clearBrain(bat);
			Goal<Bat> followGoal = new WitchBatFollowGoal(Main.getInstance(), bat, player, (Math.PI*2/BAT_CAP)*bats.size());
			MobGoals goals = Bukkit.getMobGoals();
			if (!goals.hasGoal(bat, followGoal.getKey())) {
				goals.addGoal(bat, 0, followGoal);
			}
			// ---
			bats.add(bat);
			batIndicator.SetAmount(bats.size());
			batIndicator.SetEnchantedGlow(true);
		}
		if(bats.size() >= BAT_CAP){
			CancelCharge();
		}
	}
	private void CancelCharge() {
		Main.getInstance().getChargeModule().SetCancelled(getWrapper().getPlayer().getName(), CHARGE_NAME, true);
	}
	private void AddCharge() {
		if(bats.size() >= BAT_CAP) return;
		Main.getInstance().getChargeModule().AddCharge(
				getWrapper().getPlayer().getName(), 
				CHARGE_NAME, 
				PASSIVE_BAT_GENERATION, 
				true, 
				false, 
				0,
				this);
	}
	private void RemoveBats() {
		for(Bat bat : bats){
			bat.remove();
		}
		bats.clear();
		batIndicator.SetAmount(1);
		batIndicator.SetEnchantedGlow(false);
	}
}
