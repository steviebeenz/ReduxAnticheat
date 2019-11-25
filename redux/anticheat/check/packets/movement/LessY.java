package redux.anticheat.check.packets.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class LessY extends PacketCheck {

	public LessY() {
		super("LessY", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 80);
		setDescription("Checks if a player is falling slower than normal.");
	}

	@Override
	public void listen(PacketEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

		if (Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())
				|| Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())) {
			return;
		}

		if (e.getPlayer().isFlying() || pd.flyTicks > 0) {
			return;
		}

		if (System.currentTimeMillis() - pd.getLastOnSlime() < 1500) {
			return;
		}

		if (pd.velocTicks > 0 || pd.teleportTicks > 0 || pd.vehicleTicks > 0 || pd.stairTicks > 0 || pd.jumpStairsTick > 0 || pd.changeTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().isCollidedWeb(pd.getLastLocation(), pd.getNextLocation())
				|| Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation())
				|| Main.getInstance().getLocUtils().isInLiquid(pd.getLastLocation()) || locUtils.isCollidedWithWeirdBlock(pd.getNextLocation(), pd.getLastLocation())) {
			return;
		}

		if (pd.getDeltaY() < 0 && pd.isFalling) {
			double limit = -0.01555;
			
			limit += Math.abs(pd.getVelocity() * 0.00078);
			limit += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.05;
			if (pd.getDeltaY() > limit) {
				flag(pd, pd.getDeltaY() + " > " + -0.01555);
			}
		}

	}

}
