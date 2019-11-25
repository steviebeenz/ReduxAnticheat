package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class SpeedE extends PacketCheck {
	
	public SpeedE() {
		super("Speed [E]", 10, null, false, false, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.teleportTicks > 0 || pd.vehicleTicks > 0 || pd.flyTicks > 0 || pd.jumpStairsTick > 0
				|| pd.stairTicks > 0) {
			return;
		}

		if (locUtils.isCollidedWithWeirdBlock(pd.getNextLocation(), pd.getLastLocation())) {
			return;
		}

		float friction = SpeedD.getFriction(pd);

		double speed = pd.getDeltaXZ() * pd.getDeltaXZ() + pd.getLastDeltaXZ() * pd.getLastDeltaXZ(),
				speedSqrt = Math.abs(Math.sqrt(speed));

		if (speed == 0) {
			return;
		}

		//p.sendMessage("speed: " + speed + " collided ground: " + locUtils.isOnSolidGround(pd.getNextLocation())
			//	+ ", speedSqrt: " + speedSqrt);
		
		double expected = 0.4 + Math.abs(Math.abs(pd.getDeltaXZ() - pd.getLastDeltaXZ()) * friction);
		expected *= 1 + Math.abs((pd.onGroundTicks < 1 ? Math.pow(pd.offGroundTicks, 0.65) * 0.88 : Math.pow(pd.onGroundTicks, 0.13) * 0.52));
		
		expected += ReflectionUtils.getPingModifier(p) * 0.12;
		expected += Math.abs(pd.getVelocity()) * 0.18;
		expected += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.08;
		
		//p.sendMessage("sqrt: " + speedSqrt + "/" + expected);
		
		if(pd.iceTicks > 0) {
			expected += (0.08 * pd.iceTicks);
		}
		
		if(pd.blockAboveTicks > 0) {
			expected += (0.06 * pd.blockAboveTicks);
		}
		
		if(speedSqrt > expected) {
			pd.speedEvl++;
			if(pd.speedEvl > 2) {
				flag(pd, speedSqrt + " > " + expected + " onGround or offGround: " + pd.onGroundTicks + " or " + pd.offGroundTicks);
			}
		} else {
			if(pd.speedEvl > 0) {
				pd.speedEvl -= 0.5;
			}
		}
		
		
	}

}
