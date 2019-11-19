package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class NormalMovements extends PacketCheck {

	public NormalMovements() {
		super("NormalMovements", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (p.isFlying() || pd.flyTicks > 0 || pd.teleportTicks > 0 || pd.vehicleTicks > 0) {
			return;
		}

		/*
		 * Max 0: 0.78 Max 1: 0.6 Max 2: 0.6 Max 3: 0.24785109963254115
		 */

		if(Main.getInstance().getLocUtils().canClimb(pd.getNextLocation()) | Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())) {
			return;
		}
		
		if(System.currentTimeMillis() - pd.getLastOnSlime() <= 1500) {
			return;
		}
		
		if(Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation()) || Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())) {
			return;
		}
		
		if(pd.jumpStairsTick > 0 || pd.stairTicks > 0) {
			return;
		}
		
		double z = 0.78, t = 0.6, tree = 0.43;
		
		for(PotionEffect pe : p.getActivePotionEffects()) {
			if(pe.getType().equals(PotionEffectType.JUMP)) {
				z *= 1 + (pe.getAmplifier() * 0.2);
				t *= 1 + (pe.getAmplifier() * 0.2);
				tree *= 1 + (pe.getAmplifier() * 0.2);
			}
		}
		
		z += ReflectionUtils.getPingModifier(p) * 0.08;
		z += pd.getVelocity() * 0.08;
		
		t += ReflectionUtils.getPingModifier(p) * 0.08;
		t += pd.getVelocity() * 0.08;
		
		tree += ReflectionUtils.getPingModifier(p) * 0.08;
		tree += pd.getVelocity() * 0.08;
		
		
		if (pd.isRising) {
			if (pd.risingTicks >= 0 && pd.risingTicks < 11) {
				if (pd.risingTicks == 0) {
					if (pd.getDeltaY() >= z) {
						flag(pd, pd.getDeltaY() + " >= " + z);
					}
				} else if (pd.risingTicks == 1 || pd.risingTicks == 2) {
					if (pd.getDeltaY() >= t) {
						flag(pd, pd.getDeltaY() + " >= " + t);
					}
				} else if (pd.risingTicks == 3) {
					if (pd.getDeltaY() >= tree) {
						flag(pd, pd.getDeltaY() + " >= " + tree);
					}
				}
			}
		}
	}

}
