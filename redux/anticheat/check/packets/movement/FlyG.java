package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class FlyG extends PacketCheck {

	public FlyG() {
		super("Fly [G]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if(pd.teleportTicks > 0 || System.currentTimeMillis() - pd.join < 1000 || pd.vehicleTicks > 0 || pd.flyTicks > 0 || pd.stairTicks > 0 || pd.jumpStairsTick > 0) {
			pd.flyGvl = 0;
			return;
		}
		
		if (pd.offGroundTicks > 0 && pd.getDeltaY() > 0) {
			if (pd.lastYDiff == 0) {
				pd.lastYDiff = pd.getDeltaY() - pd.getLastDeltaY();
				return;
			}

			if (locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation()) || locUtils.isOnSolidGround(pd.getNextLocation().add(0, 0.3, 0)) || locUtils.isOnSolidGround(pd.getLastLocation().add(0, 0.3, 0))) {
				pd.flyGvl = 0;
				return;
			}

			if(locUtils.isOnSolidGround(pd.getNextLocation()) || locUtils.isOnSolidGround(pd.getLastLocation()) || locUtils.canClimb(pd.getNextLocation()) || locUtils.canClimb(pd.getLastLocation())) {
				pd.flyGvl = 0;
				return;
			}
			
			if ((pd.getDeltaY() - pd.getLastDeltaY()) == pd.lastYDiff
					|| near((pd.getDeltaY() - pd.getLastDeltaY()), pd.lastYDiff)) {
				pd.flyGvl++;
				if (pd.flyGvl >= 2) {
					flag(pd, "same y difference");
				}
			} else {
				pd.flyGvl -= 0.5;
			}

			pd.lastYDiff = (pd.getDeltaY() - pd.getLastDeltaY());

		} else {
			return;
		}
	}

	private boolean near(double deltaY, double predicted) {
		return Math.abs(deltaY - predicted) < 0.001;
	}

}
