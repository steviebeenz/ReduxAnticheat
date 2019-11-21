package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class Glide extends PacketCheck {
	
	public Glide() {
		super("Glide", 5, 10, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION }, true, 65);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		
		if(pd.teleportTicks > 0 || pd.flyTicks > 0 || p.isFlying() || pd.velocTicks > 0) {
			return;
		}
		
		if(Main.getInstance().getLocUtils().canClimb(pd.getLastLocation()) || Main.getInstance().getLocUtils().canClimb(pd.getNextLocation()) || Main.getInstance().getLocUtils().isCollidedWeb(pd.getNextLocation(), pd.getLastLocation()) || Main.getInstance().getLocUtils().isCollidedWeb(pd.getLastLocation(), "WEB") || Main.getInstance().getLocUtils().isCollidedWeb(pd.getNextLocation(), "WEB")) {
			return;
		}
		
		if(pd.fallingTicks > 0 && pd.isFalling && !pd.wasFalling && !pd.isRising && !ReflectionUtils.getOnGround(p)) {
			
		}
	}
	
	

}
