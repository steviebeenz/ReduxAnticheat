package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class FlyI extends PacketCheck {

	public FlyI() {
		super("Fly [I]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, false, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.flyTicks > 0 || p.isFlying() || pd.teleportTicks > 0 || pd.velocTicks > 0 || pd.vehicleTicks > 0
				|| pd.stairTicks > 0 || pd.jumpStairsTick > 0 || pd.blockAboveTicks > 0) {
			return;
		}

		if (locUtils.canClimb(pd.getNextLocation()) || locUtils.canClimb(pd.getLastLocation()) || locUtils.isOnSolidGround(pd.getNextLocation())) {
			return;
		}

		double diff = Math.abs(pd.getDeltaY() - pd.getLastDeltaY());

		if (diff == 0) {
			return;
		}
		
		if(locUtils.isCollidedWithWeirdBlock(pd.getNextLocation(), pd.getLastLocation())) {
			return;
		}

		double speed = (diff * 0.91) + (pd.getVelocity() * 0.12) % 1.0;

		if (pd.isRising) {
			//p.sendMessage("speed: " + speed);
			if(speed < 0.001) {
				flag(pd, "less y");
			}
			
			if(pd.ticksOnLadder > 0) {
				return;
			}
			
			double starting = 0.92, predicted = 0;
			
			predicted = (starting) - (0.093 * pd.risingTicks) - (pd.getDeltaY() * 0.082);
			
			speed -= ReflectionUtils.getPingModifier(p) * 0.12;
			speed -= Math.abs(pd.getVelocity()) * 0.0078;
			
			if(speed > predicted) {
				flag(pd, speed + " > " + predicted);
			}
			
			//p.sendMessage("speed: " + speed + "/" + predicted);
		}
	}

}
