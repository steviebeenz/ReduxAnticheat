package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class MovePattern extends PacketCheck {

	public MovePattern() {
		super("MovePattern", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		this.setDescription("Checks if a player is moving in a pattern.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.getDeltaY() > 0 || pd.getDeltaY() < 0 || pd.getPreviousDeltaY() > 0 || pd.getPreviousDeltaY() < 0) {

			if (Main.getInstance().getLocUtils().canClimb(pd.getNextLocation()) || Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())
					|| Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation()) || Main.getInstance().getLocUtils().isInLiquid(pd.getLastLocation())) {
				pd.movePattern = 0;
				return;
			}

			if (Main.getInstance().getLocUtils().isCollidedStairs(pd.getLastLocation(), pd.getNextLocation())
					|| Main.getInstance().getLocUtils().isCollidedSlab(pd.getLastLocation(), pd.getNextLocation()) ||
					Main.getInstance().getLocUtils().isCollidedWeb(pd.getLastLocation(), pd.getNextLocation())) {
				pd.movePattern = 0;
				return;
			}

			if (pd.vehicleTicks > 0 || pd.teleportTicks > 0 || pd.flyTicks > 0 || p.isFlying()) {
				pd.movePattern = 0;
				return;
			}

			double curDY = (pd.getDeltaY() - pd.getPreviousDeltaY()), curDelta = pd.getDeltaY();
			if (curDY == pd.curDY) {
				pd.movePattern++;
			} else {
				if (pd.movePattern > 0) {
					pd.movePattern--;
				}
			}

			if (curDelta == pd.curDelta) {
				pd.movePattern++;
			} else {
				if (pd.movePattern > 0) {
					pd.movePattern--;
				}
			}
			
			if(pd.getDeltaXZ() == pd.getLastDeltaXZ() && pd.getDeltaXZ() != 0) {
				pd.movePattern++;
			} else {
				if(pd.movePattern > 0) {
					pd.movePattern--;
				}
			}

			if (pd.movePattern >= 3) {
				flag(pd, pd.movePattern + " >= " + 3);
			}

			pd.curDelta = curDelta;
			pd.curDY = curDY;
		}
	}

}
