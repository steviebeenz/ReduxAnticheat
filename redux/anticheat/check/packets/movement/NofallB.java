package redux.anticheat.check.packets.movement;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class NofallB extends PacketCheck {

	public NofallB() {
		super("Nofall [B]", 5, 10, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION, PacketType.Play.Client.FLYING }, true, 75);
		settings.put("limit", 3);
		this.setDescription("Checks if a player is sending flying packets in an unatural order.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if(p.isFlying() || pd.flyTicks > 0) {
			pd.patternFall = 0;
			return;
		}
		
		if(pd.teleportTicks > 0) {
			pd.patternFall = 0;
			return;
		}
		
		if(pd.vehicleTicks > 0) {
			pd.patternFall = 0;
			return;
		}
		
		if(e.getPacketType().equals(getType()[0])) {
			if(pd.lastMovePacket == null) {
				pd.lastMovePacket = PacketType.Play.Client.POSITION;
				return;
			}
			
			
			Block b = Main.getInstance().getLocUtils().getBlockUnder(pd.getNextLocation());
			if(b != null) {

			} else {
				pd.patternFall = 0;
				return;
			}
			
			if(!Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation()) && !Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation()) && !Main.getInstance().getLocUtils().isCollidedStairs(pd.getLastLocation(), pd.getNextLocation())
					&& !Main.getInstance().getLocUtils().isCollidedSlab(pd.getLastLocation(), pd.getNextLocation())) {
				if(pd.lastMovePacket.equals(PacketType.Play.Client.FLYING) && pd.getDeltaY() < 0) {
					pd.patternFall++;
					if(pd.patternFall > (int)settings.get("limit")) {
						flag(pd, pd.patternFall + " > " + settings.get("limit") + " (limit)");
						pd.patternFall = 0;
					}
					p.sendMessage("pattern " + pd.patternFall);
				}
			} else {
				pd.patternFall = 0;
			}
			pd.lastMovePacket = PacketType.Play.Client.POSITION;
		} else if(e.getPacketType().equals(getType()[1])) {
			pd.lastMovePacket = PacketType.Play.Client.FLYING;
		}
	}
	
	

}
