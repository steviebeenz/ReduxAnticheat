package redux.anticheat.check.packets.packet;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class BadPacketsG extends PacketCheck {

	public BadPacketsG() {
		super("BadPackets [G]", 5, null, false, true, Category.PACKETS, new PacketType[] { PacketType.Play.Client.KEEP_ALIVE }, true, 80);
		this.setDescription("Checks if a player is sending more Keep Alive packets than usual.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		
		if(System.currentTimeMillis() - pd.lastKeepAlive < 100) {
			pd.keepAliveFlag++;
			if(pd.keepAliveFlag > 1) {
				flag(pd, "ka < 500");
				pd.keepAliveFlag = 0;
			}
		}
		
		pd.lastKeepAlive = System.currentTimeMillis();
	}

	
}
