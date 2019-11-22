package redux.anticheat.check.packets.movement;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class SpeedD extends PacketCheck {

	public SpeedD() {
		super("Speed [D]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.teleportTicks > 0 || pd.flyTicks > 0 || p.isFlying() || pd.vehicleTicks > 0 || pd.stairTicks > 0
				|| pd.jumpStairsTick > 0) {
			p.sendMessage("returned (tp/ft/fl/ve/st/ju): " + pd.teleportTicks + ", " + pd.flyTicks + ", "
					+ pd.vehicleTicks + ", " + pd.stairTicks + ", " + pd.jumpStairsTick);
			return;
		}

		if (locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation())) {
			return;
		}

		if (locUtils.canClimb(pd.getLastLocation()) || locUtils.canClimb(pd.getNextLocation())) {
			return;
		}

		final float friction = getFriction(pd);
		double maxSpeed = (pd.getDeltaXZ() - pd.getLastDeltaXZ()) * friction;

		maxSpeed -= ReflectionUtils.getPingModifier(p) * 0.08;
		maxSpeed -= pd.getVelocity() * 0.00078;

		if (maxSpeed >= 0.38) {
			flag(pd, friction + " >= " + maxSpeed);
		}

		double speed = pd.getDeltaXZ() * friction, speedLimit = 0.61;

		for (final PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.SPEED)) {
				speedLimit *= 1 + (pe.getAmplifier() * 0.2);
			}
		}

		if (p.getWalkSpeed() > 0.2f) {
			speedLimit *= (p.getWalkSpeed() / 0.2f);
		}

		speed -= ReflectionUtils.getPingModifier(p) * 0.08;
		speed -= pd.getVelocity() * 0.00078;

		speedLimit += Math.abs((20 - Main.getInstance().getTpsTask().tps)) / 100;

		if (speed > speedLimit && !pd.wasFalling) {

			if (pd.isRising) {
				if (pd.risingTicks > 1) {
					flag(pd, speed + " >= " + speedLimit);
				}
				return;
			}
			flag(pd, speed + " >= " + speedLimit);
			p.sendMessage("falling: " + pd.isFalling + ", rising: " + pd.isRising + " wasFalling: " + pd.wasFalling
					+ " wasRising: " + pd.risingTicks);
		}

		// p.sendMessage("speed: " + speed + "/" + speedLimit);
	}

	private float getFriction(PlayerData pd) {
		float friction = 0.91F;

		final boolean onGround = locUtils.isOnSolidGround(pd.getNextLocation());

		if (onGround) {
			final Vector pos = pd.getNextLocation().toVector();
			final Block b = pd.getPlayer().getWorld()
					.getBlockAt(new Location(pd.getPlayer().getWorld(), pos.getX(), pos.getY() - 1, pos.getZ()));
			if (b != null) {
				double sliperiness = 0;

				sliperiness = ReflectionUtils.getSliperness(b);

				return friction *= sliperiness;
			} else {
				pd.getPlayer().sendMessage("null block");
				return 0.91F;
			}
		}

		return friction;
	}

}
