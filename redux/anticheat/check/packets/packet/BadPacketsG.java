package redux.anticheat.check.packets.packet;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class BadPacketsG extends PacketCheck {

	public BadPacketsG() {
		super("BadPackets [G]", 5, 10, null, false, true, Category.PACKETS, new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 80);
		this.setDescription("Checks for Killaura like packets.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		
		if(pd.flyTicks > 0 || p.isFlying()) {
			return;
		}
		
		if((System.currentTimeMillis() - pd.flyingA) + ReflectionUtils.getPingModifier(p) + Math.abs(20 -Main.getInstance().getTpsTask().tps) < 8) {
			flag(pd, "flying > 8 (experimental)");
		}
	}
	

}