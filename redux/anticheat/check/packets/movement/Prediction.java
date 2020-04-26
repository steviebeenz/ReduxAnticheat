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

public class Prediction extends PacketCheck {

	public Prediction() {
		super("Prediction", 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (p.isFlying() || pd.flyTicks > 0 || pd.vehicleTicks > 0 || pd.changeGamemodeTicks > 0 || pd.velocTicks > 0
				|| pd.ticksOnClimbable > 0) {
			p.sendMessage("returned");
			return;
		}

		if (locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation())
				|| locUtils.canClimb(pd.getLastLocation()) || locUtils.canClimb(pd.getNextLocation())
				|| locUtils.isInLiquid(pd.getLastLocation()) || locUtils.isInLiquid(pd.getNextLocation())) {
			return;
		}

		double y = Math.abs(pd.getDeltaY()), lastY = Math.abs(pd.getLastDeltaY()), predictedY = ((lastY * 0.0789)
				+ (y * (0.986 * (pd.isRising ? 0.996 : 1) * (pd.isFalling ? 0.991 : 1))));
		if (y <= 0 || lastY == 0) {

		} else {
			for(PotionEffect ef : p.getActivePotionEffects()) {
				if(ef.getType().equals(PotionEffectType.JUMP)) {
					predictedY *= 1 + (ef.getAmplifier() * 0.12);
				}
			}
			
			if(y > predictedY && y != 0 && predictedY != 0 && Math.abs(predictedY) > 0.005) {
				flag(pd, "d=" + y + "/" + predictedY + "(og: " + pd.onGroundTicks + ",ofg: " + pd.offGroundTicks + ")");
			}
		}

		/* XZ check */
		double x = pd.getDeltaXZ(), lastXZ = pd.getLastDeltaXZ(), predictedXZ = ((lastXZ * 0.91f)
				+ (x * 1 + ((pd.isFalling ? 0.78 : 0.5) * (pd.onGroundTicks > 0 ? 0.28 : 0.48))) * 0.56);

		if (x == 0 || lastXZ == 0) {

		} else {

			for (PotionEffect ef : p.getActivePotionEffects()) {
				if (ef.getType().equals(PotionEffectType.SPEED)) {
					predictedXZ *= 1 + (ef.getAmplifier() * 0.2);
				}
			}

			if (pd.iceTicks > 0) {
				predictedXZ *= 1.1;
			}

			if (pd.blockAboveTicks > 0) {
				predictedXZ *= 1.1;
			}

			// p.sendMessage("d=" + x + "/" + predictedXZ);
			// double prcnt = workPercentage(x, predictedXZ);

			if (x > predictedXZ && x != 0 && predictedXZ != 0 && Math.abs(predictedXZ) > 0.005) {
				flag(pd, "d=" + x + "/" + predictedXZ + "(f:" + pd.isFalling + ",r:" + pd.isRising + ")");
			}
		}

	}

}
