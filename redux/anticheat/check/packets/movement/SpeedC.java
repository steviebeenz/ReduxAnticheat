package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class SpeedC extends PacketCheck {

	public SpeedC() {
		super("Speed [C]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 95);
		this.setDescription("Checks a player's ground speed.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		boolean serverside = Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())
				&& Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation());
		boolean client = ReflectionUtils.getOnGround(p);

		double limit = 0.21 + 0.075f + (pd.onGroundTicks < 8 ? 0.4f * Math.pow(0.75f, pd.onGroundTicks) : 0);

		boolean isOnSmallBlock = Main.getInstance().getLocUtils().isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation());

		if (serverside && client && pd.iceTicks == 0 && pd.blockAboveTicks == 0 && pd.getDeltaXZ() > limit
				&& !isOnSmallBlock && pd.stairTicks == 0 && pd.jumpStairsTick == 0 && pd.velocTicks < 5) {
			this.vl++;
			p.sendMessage("flagged vl: " + vl);
			if (vl >= 5) {
				flag(pd, pd.getDeltaXZ() + " > " + limit);
				vl = 0;
			}
		} else {
			if(vl > 0) {
				vl -= 0.5;
			}
		}

	}

}
