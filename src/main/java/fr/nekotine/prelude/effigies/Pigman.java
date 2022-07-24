package fr.nekotine.prelude.effigies;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.core.damage.DamageManager;
import fr.nekotine.core.damage.LivingEntityDamageEvent;
import fr.nekotine.core.projectile.CustomProjectile;
import fr.nekotine.core.projectile.IProjectile;
import fr.nekotine.core.projectile.ProjectileManager;
import fr.nekotine.core.usable.Usable;
import fr.nekotine.core.usable.UsableManager;
import fr.nekotine.core.util.UtilMath;
import fr.nekotine.prelude.Effigy;
import fr.nekotine.prelude.EffigyList;
import fr.nekotine.prelude.Main;
import fr.nekotine.prelude.PlayerWrapper;
import fr.nekotine.prelude.utils.Ability;

public class Pigman extends Effigy implements IProjectile{
	private static final int PRIMARY_COOLDOWN = 5 * 20;
	private static final int SECONDARY_COOLDOWN = 0 * 20;
	
	private static final Material MEAT_MATERIAL = Material.COOKED_PORKCHOP;
	private static final Material NO_MEAT_MATERIAL = Material.PORKCHOP;
	private static final double DAMAGE_BOOST_PER_MEAT = 0.25 * 2;
	
	private static final int PRIMARY_COOLDOWN_REDUCTION = 1 * 20;
	private static final double PRIMARY_DAMAGE = 0.5 * 2;
	private static final int PRIMARY_MEAT_COST = 1;
	private static final float PRIMARY_MEAT_VELOCITY = 1.5f;
	private static final Material[] PRIMARY_BARRIER = {Material.BARRIER};
	
	private static final double SECONDARY_HEAL_PER_MEAT = 0.5 * 2;
	
	private Usable meat;
	private boolean hit = false;
	private static final Consumer<PlayerDropItemEvent> CANCEL_DROP_EVENT = new Consumer<PlayerDropItemEvent>() {
		@Override
		public void accept(PlayerDropItemEvent e) {
			e.setCancelled(true);
		}
	};
	
	//
	
	public Pigman(PlayerWrapper wrapper, EffigyList effigyType) {
		super(wrapper, effigyType);
		meat = Main.getInstance().getModuleManager().Get(UsableManager.class).AddUsable(
				new ItemStack(NO_MEAT_MATERIAL),
				getWrapper().getPlayer().getInventory());
		meat.SetName("Viande");
		meat.OnDrop(CANCEL_DROP_EVENT);
		meat.Give();
	}

	//
	
	@Override
	public void Faded(CustomProjectile arg0) {
	}
	@Override
	public void Hit(LivingEntity hitE, Block hitB, CustomProjectile proj) {
		if(hitB != null) {
			proj.GetProjectile().remove();
			return;
		}
		
		Main.getInstance().getModuleManager().Get(DamageManager.class).Damage(
				hitE, 
				getWrapper().getPlayer(), 
				null, 
				DamageCause.CUSTOM, 
				PRIMARY_DAMAGE, 
				true, 
				true, 
				proj.GetProjectile().getLocation());
		
		getWrapper().getPlayer().setVelocity(UtilMath.GetTrajectory3d(getWrapper().getPlayer().getLocation(), hitE.getLocation()));
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 0);
		
		if(isOnCooldown(Ability.PRIMARY)) setCooldown(Ability.PRIMARY, getCooldown(Ability.PRIMARY) - PRIMARY_COOLDOWN_REDUCTION);
		
		proj.GetProjectile().remove();
	}
	@Override
	public void Triggered(CustomProjectile arg0) {
	}
	
	//
	
	@Override
	protected void castPrimarySpell() {
		if(!CanUseSpell(PRIMARY_MEAT_COST)) return;
		
		setCooldown(Ability.PRIMARY, PRIMARY_COOLDOWN);
		
		RemoveMeat(PRIMARY_MEAT_COST);
		
		Item meatItem = getWrapper().getPlayer().getWorld().dropItem(getWrapper().getPlayer().getLocation().add(0, 1.5, 0), new ItemStack(MEAT_MATERIAL));
		meatItem.setInvulnerable(true);
		ArrayList<Player> inTeam = Main.getInstance().getPlayersInTeam(getWrapper().getTeam());
		Main.getInstance().getModuleManager().Get(ProjectileManager.class).AddProjectile(
				meatItem, 
				getWrapper().getPlayer(),
				this, 
				getWrapper().getPlayer().getEyeLocation().getDirection().multiply(PRIMARY_MEAT_VELOCITY), 
				10 * 1000, 
				true, 
				true,
				inTeam.toArray(new LivingEntity[inTeam.size()]),
				PRIMARY_BARRIER);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT, 1, 0);
	}
	@Override
	protected void castSecondarySpell() {
		if(!CanUseSpell(1)) return;
		
		setCooldown(Ability.SECONDARY, SECONDARY_COOLDOWN);
		
		Main.getInstance().getModuleManager().Get(DamageManager.class).Damage(
				getWrapper().getPlayer(),
				null, 
				null, 
				DamageCause.CUSTOM, 
				-SECONDARY_HEAL_PER_MEAT * meat.GetAmount(), 
				true, 
				false, 
				null);
		
		getWrapper().getPlayer().getWorld().playSound(getWrapper().getPlayer(), Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 0);
		
		RemoveMeat(meat.GetAmount());
	}
	@Override
	protected void roundEnd() {
	}
	@Override
	protected void death() {
	}
	@Override
	protected void roundStart() {
	}
	@Override
	protected void destroy() {
		meat.Remove();
		super.destroy();
	}
	
	//
	
	private boolean CanUseSpell(int meatCost) {
		return meat.GetMaterial() == MEAT_MATERIAL && meat.GetAmount() >= meatCost;
	}
	private void RemoveMeat(int toRemove) {
		if(meat.GetAmount() - toRemove <= 0) {
			meat.SetMaterial(NO_MEAT_MATERIAL);
			meat.SetAmount(1);
			meat.SetEnchantedGlow(false);
			return;
		}
		meat.AddAmount(-toRemove);
	}
	private void AddMeat(int toAdd) {
		if(meat.GetMaterial() == NO_MEAT_MATERIAL) {
			meat.SetMaterial(MEAT_MATERIAL);
			meat.SetAmount(toAdd);
			meat.SetEnchantedGlow(true);
			return;
		}
		meat.AddAmount(toAdd);
	}
	
	//
	
	@EventHandler
	public void OnDamage(LivingEntityDamageEvent e) {
		if(getWrapper().getPlayer().equals(e.GetDamager()) && e.GetCause() == DamageCause.ENTITY_ATTACK) {
			e.AddBaseMod(DAMAGE_BOOST_PER_MEAT * meat.GetAmount());
			if(hit) {
				hit = false;
				AddMeat(1);
			}else {
				hit = true;
			}
		}
	}
}
