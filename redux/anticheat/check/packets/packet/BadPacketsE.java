package redux.anticheat.check.packets.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class BadPacketsE extends PacketCheck {

	public BadPacketsE() {
		super("BadPackets [E]", 5, 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION }, true, 86);
		setDescription("Checks if a player is moving while sending flying packets.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (e.getPacketType().equals(getType()[1])) {
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
			final double rate = (pd.flyingInPackets / 20);

			if (rate >= 0.1) {
				if (pd.getDeltaXZ() > 0.2 && pd.getLastDeltaXZ() > 0.2 || pd.getDeltaY() > 0.15 && pd.getLastDeltaY() > 0.5) {
					pd.badPacketsE++;
					if (pd.badPacketsE > 2) {
						flag(pd, rate + " >= 0.2 while moving");
						pd.badPacketsE = 0;
					}
				} else {
					pd.badPacketsE = 0;
				}
			} else {
				pd.badPacketsE = 0;
			}
		}
	}

}
