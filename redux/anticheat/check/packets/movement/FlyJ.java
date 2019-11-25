package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class FlyJ extends PacketCheck {

	public FlyJ() {
		super("Fly [J]", 10, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		
		if(System.currentTimeMillis() - pd.getLastOnSlime() < 1500 || pd.flyTicks > 0 || pd.velocTicks > 0 || pd.vehicleTicks > 0 || p.isFlying() || pd.changeGamemodeTicks > 0 || pd.teleportTicks > 0 || pd.jumpStairsTick > 0) {
			return;
		}
		
		double diff = pd.getNextLocation().getY() - pd.getLastLocation().getY();
		double expected = (diff - 0.078) * 0.98;
		
		if(locUtils.isCollidedWithWeirdBlock(pd.getNextLocation(), pd.getLastLocation())) {
			return;
		}
		
		if(!locUtils.isOnSolidGround(pd.getLastLocation()) && !locUtils.isOnSolidGround(pd.getNextLocation()) && Math.abs(diff) > 0.041 && pd.onGroundTicks < 1) {
			double limit = 0.005;
			limit += Math.abs(pd.getVelocity()) * 0.12;
			limit += ReflectionUtils.getPingModifier(p) * 0.012;
			limit += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.005;
			
			if(Math.abs(expected - diff) > limit && !pd.wasSetBack) {
				//p.sendMessage("flagged");
				pd.flyIvl++;
				if(pd.flyIvl >= (9 + Math.abs(20 - Main.getInstance().getTpsTask().tps))) {
					flag(pd, Math.abs(expected - diff) + " > " + limit);
					pd.flyIvl = 0;
				}
			} 
		}
		
		if(pd.flyIvl > 0) {
			pd.flyIvl -= 0.5;
		}
		
	}
	
	

}
