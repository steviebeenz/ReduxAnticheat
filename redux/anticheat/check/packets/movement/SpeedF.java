package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class SpeedF extends PacketCheck {

	public SpeedF() {
		super("Speed [F]", 5, 10, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		
		if(System.currentTimeMillis() - pd.getLastOnSlime() < 1500 || System.currentTimeMillis() - pd.join < 1000 || pd.teleportTicks > 0 || pd.changeTicks > 0 || pd.jumpStairsTick > 0 || pd.stairTicks > 0 || pd.flyTicks > 0 || p.isFlying() || pd.blockAboveTicks > 0) {
			return;
		}
		
		double x = pd.getNextLocation().getX() - pd.getLastLocation().getX(), z = pd.getNextLocation().getZ() - pd.getLastLocation().getZ();
		
		x = (x * x);
		
		double speed = (x + z * z) * 0.3f, limit = 0.1;
		
		limit += ReflectionUtils.getPingModifier(p) * 0.028;
		limit += Math.abs(pd.getVelocity()) * 0.018;
		limit += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.044;
		
		if(!locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation()) && !locUtils.isOnSolidGround(pd.getNextLocation().clone().add(0, 0.3, 0)) && pd.iceTicks <= 1 && speed > limit) {
			if(System.currentTimeMillis() - pd.speedFflags < 1000) {
				if(pd.speedFflags == 0L) {
					pd.speedFflags = System.currentTimeMillis();
					return;
				}
				pd.speedFvl++;
			} else {
				if(pd.speedFvl > 6) {
					flag(pd, speed + " > " + limit + " (" + Math.round((speed/limit) * 100) + "%)");
					pd.speedFvl = 0;
					pd.speedFflags = System.currentTimeMillis();
					return;
				}
				
				pd.speedFvl = 0;
				pd.speedFflags = System.currentTimeMillis();
			}
		}
		
		//p.sendMessage("speed: " + (speed > 0.039) + " ground: " + locUtils.isOnSolidGround(pd.getNextLocation().clone().add(0, 0.3, 0)) + ", ice: " + (pd.iceTicks <= 1) + ", withWeird: " + locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation()));
		
	}
	
	

}
