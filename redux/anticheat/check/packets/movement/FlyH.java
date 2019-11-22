package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class FlyH extends PacketCheck {

	public FlyH() {
		super("Fly [H]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 100);
		this.setDescription("Checks if a player is trying to fall slower than normal.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.teleportTicks > 0 || pd.flyTicks > 0 || p.isFlying()) {
			return;
		}

		if (pd.offGroundTicks > 0) {
			if (pd.getDeltaY() <= 0) {
				if (!Main.getInstance().getLocUtils().isCollidedWithWeirdBlock(pd.getLastLocation(),
						pd.getNextLocation())) {
					if (Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())
							|| Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())) {
						return;
					}
					if (pd.getDeltaY() > -0.078 && pd.risingTicks == 0 && !pd.wasFalling
							&& !ReflectionUtils.getOnGround(p)) {
						if (!pd.isFalling && !pd.wasFalling && !pd.isRising) {
							return;
						}
						vl++;
						if (vl > 1) {
							flag(pd, pd.getDeltaY() + " > " + -0.078 + " | rising: " + pd.isRising + ", falling: "
									+ pd.isFalling + " wasFalling: " + pd.wasFalling);
						}
					} else {
						if (vl > 0) {
							vl -= 0.5;
						}
					}
				}
			}
		}
	}

}
