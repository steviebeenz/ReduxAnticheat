package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class FlyF extends PacketCheck {

	public FlyF() {
		super("Fly [F]", 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		setDescription("Checks if a player is moving unexpectedly.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.flyTicks > 0 || p.isFlying() || pd.vehicleTicks > 0
				|| (System.currentTimeMillis() - pd.getLastOnSlime()) < 1500 || pd.teleportTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())
				|| Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())) {
			return;
		}

		final boolean locGround = Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation()),
				locGroundLast = Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation());

		final double predicted = (pd.getLastDeltaY() - 0.08D) * 0.9800000190734863D;

		if (pd.velocTicks > 0) {
			return;
		}

		if (pd.stairTicks > 0) {
			return;
		}

		
		double highLimit = 0.36, lowLimit = -0.78;
		
		highLimit += Math.abs(pd.getVelocity()) * 0.12;
		lowLimit -= Math.abs(pd.getVelocity()) * 0.12;
		highLimit += ReflectionUtils.getPingModifier(p) * 0.08;
		lowLimit -= ReflectionUtils.getPingModifier(p) * 0.08;
		

		if (!locGround && !locGroundLast && pd.offGroundTicks >= 3 && pd.offGroundTicks < 13) {
			if (Math.abs(predicted) > 0.005D) {
				if (!near(pd.getDeltaY(), predicted)) {
					final double diff = (pd.getDeltaY() - predicted);
					if ((diff > highLimit || diff < lowLimit) && pd.offGroundTicks != 11) {
						flag(pd, diff > 0.37 ? diff + " > " + highLimit : diff + " < " + lowLimit);
					}
				}
			}
		}

	}

	private boolean near(double deltaY, double predicted) {
		return Math.abs(deltaY - predicted) < 0.001;
	}

}
