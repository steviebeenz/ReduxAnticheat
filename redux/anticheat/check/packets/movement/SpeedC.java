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

		final boolean serverside = locUtils.isOnSolidGround(pd.getLastLocation())
				&& locUtils.isOnSolidGround(pd.getNextLocation());
		final boolean client = ReflectionUtils.getOnGround(p);

		double limit = 0.21 + 0.075f + (pd.onGroundTicks < 8 ? 0.4f * Math.pow(0.75f, pd.onGroundTicks) : ((double)pd.onGroundTicks * 0.08));

		final boolean isOnSmallBlock = locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(),
				pd.getNextLocation());

		if (serverside && client && pd.iceTicks == 0 && pd.blockAboveTicks == 0 && pd.getDeltaXZ() > limit
				&& !isOnSmallBlock && pd.stairTicks == 0 && pd.jumpStairsTick == 0 && pd.velocTicks < 5) {
			limit += pd.getVelocity() * 0.08;
			limit += ReflectionUtils.getPingModifier(p) * 0.12;
			limit += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.05;
			pd.speedCvl++;
			if (pd.speedCvl >= 5 && pd.getDeltaXZ() > limit) {
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
