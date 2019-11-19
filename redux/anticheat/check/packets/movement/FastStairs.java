package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class FastStairs extends PacketCheck {

	public FastStairs() {
		super("FastStairs", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 80);
		this.setDescription("Checks if a player is moving faster than normal on stairs.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.flyTicks > 0 || p.isFlying() || pd.teleportTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().isCollidedStairs(pd.getNextLocation(), pd.getLastLocation())) {
			pd.stairTicks++;
			if (pd.stairTicks > 3 && pd.getDeltaY() > -0.000001) {
				double limit = 0.63;
				if (pd.getDeltaXZ() == 0 && pd.getLastDeltaXZ() == 0) {
					return;
				}

				if (pd.jumpStairsTick > 0) {
					pd.jumpStairsTick--;
				}

				if (pd.getDeltaY() > 0.5 || pd.getDeltaY() != 0 && pd.getDeltaY() != 0.5
						|| pd.getDeltaY() != pd.getPreviousDeltaY()) {
					pd.jumpStairsTick = 5;
				} else {
					if (pd.jumpStairsTick > 0) {
						pd.jumpStairsTick--;
					}
				}

				if (pd.jumpStairsTick > 0) {
					limit += 0.38;
				}

				if (pd.getDeltaXZ() > limit) {
					flag(pd, pd.getDeltaXZ() + " > " + limit);
				}
			}
		} else {
			if (pd.stairTicks > 0) {
				pd.stairTicks--;
			}
		}

	}

}
