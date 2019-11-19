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
		super("Fly [F]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		this.setDescription("Checks if a player is moving unexpectedly.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.flyTicks > 0 || p.isFlying() || pd.vehicleTicks > 0
				|| (System.currentTimeMillis() - pd.getLastOnSlime()) < 1500 || pd.teleportTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())
				|| Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())) {
			return;
		}

		boolean locGround = Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation()),
				locGroundLast = Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation());

		double predicted = (pd.getPreviousDeltaY() - 0.08D) * 0.9800000190734863D;

		if(pd.velocTicks > 0) {
			return;
		}
		
		if(pd.stairTicks > 0) {
			return;
		}
		
		if(ReflectionUtils.getPing(p) > 200) {
			return;
		}
		
		if (!locGround && !locGroundLast && pd.offGroundTicks >= 3 && pd.offGroundTicks < 13) {
			if (Math.abs(predicted) > 0.005D) {
				if (!near(pd.getDeltaY(), predicted)) {
					double diff = (pd.getDeltaY() - predicted);
					if ((diff > 0.37 || diff < -0.789) && pd.offGroundTicks != 11) {
						flag(pd, diff > 0.37 ? diff + " > 0.37" : diff + " < -0.789");	
					}
				}
			}
		}

	}

	private boolean near(double deltaY, double predicted) {
		return Math.abs(deltaY - predicted) < 0.001;
	}

}
