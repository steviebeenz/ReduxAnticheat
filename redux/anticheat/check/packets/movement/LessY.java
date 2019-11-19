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
		this.setDescription("Checks if a player is falling slower than normal.");
	}

	@Override
	public void listen(PacketEvent e) {
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

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

		if (pd.velocTicks > 0 || pd.teleportTicks > 0 || pd.vehicleTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().isCollidedWeb(pd.getLastLocation(), pd.getNextLocation())
				|| Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation())
				|| Main.getInstance().getLocUtils().isInLiquid(pd.getLastLocation())) {
			return;
		}

		if (pd.getDeltaY() < 0) {
			if (pd.getDeltaY() > -0.01555) {
				flag(pd, pd.getDeltaY() + " > " + -0.01555);
			}
		}

	}

}
