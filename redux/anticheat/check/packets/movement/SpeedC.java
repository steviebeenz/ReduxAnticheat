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
		setDescription("Checks a player's ground speed.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		final boolean serverside = Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())
				&& Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation());
		final boolean client = ReflectionUtils.getOnGround(p);

		final double limit = 0.21 + 0.075f + (pd.onGroundTicks < 8 ? 0.4f * Math.pow(0.75f, pd.onGroundTicks) : 0);

		final boolean isOnSmallBlock = Main.getInstance().getLocUtils().isCollidedWithWeirdBlock(pd.getLastLocation(),
				pd.getNextLocation());

		if (serverside && client && pd.iceTicks == 0 && pd.blockAboveTicks == 0 && pd.getDeltaXZ() > limit
				&& !isOnSmallBlock && pd.stairTicks == 0 && pd.jumpStairsTick == 0 && pd.velocTicks < 5) {
			pd.speedCvl++;
			p.sendMessage("flagged vl: " + pd.speedCvl);
			if (pd.speedCvl >= 3.5) {
				flag(pd, pd.getDeltaXZ() + " > " + limit);
				pd.speedCvl = 0;
			}
		} else {
			if (pd.speedCvl > 0) {
				pd.speedCvl -= 0.5;
			}
		}

	}

}
