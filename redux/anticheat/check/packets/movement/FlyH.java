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
		super("Fly [H]", 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 100);
		setDescription("Checks if a player is trying to fall slower than normal.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.teleportTicks > 0 || pd.flyTicks > 0 || p.isFlying() || pd.stairTicks > 0 || pd.jumpStairsTick > 0) {
			return;
		}

		if (pd.offGroundTicks > 0) {
			if (pd.getDeltaY() <= 0) {
				if (!locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation())) {
					if (locUtils.canClimb(pd.getNextLocation()) || locUtils.canClimb(pd.getLastLocation())) {
						return;
					}
					double limit = -0.078;
					
					limit -= ReflectionUtils.getPingModifier(p) * 0.12;
					limit -= Math.abs(pd.getVelocity()) * 0.0078;
					
					if (pd.getDeltaY() > limit && pd.risingTicks == 0 && !pd.wasFalling
							&& !ReflectionUtils.getOnGround(p)) {
						if (!pd.isFalling && !pd.wasFalling && !pd.isRising) {
							return;
						}
						pd.flyHvl++;
						if (pd.flyHvl > 2) {
							flag(pd, pd.getDeltaY() + " > " + -0.078 + " | rising: " + pd.isRising + ", falling: "
									+ pd.isFalling + " wasFalling: " + pd.wasFalling);
						}
					} else {
						if (pd.flyHvl > 0) {
							pd.flyHvl -= 0.5;
						}
					}
				}
			}
		}
		
		
		if(pd.flyHvl > 0) {
			pd.flyHvl -= 0.5;
		}
	}
	

}
