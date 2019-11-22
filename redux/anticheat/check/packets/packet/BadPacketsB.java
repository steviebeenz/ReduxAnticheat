package redux.anticheat.check.packets.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class BadPacketsB extends PacketCheck {

	public BadPacketsB() {
		super("BadPackets [B]", 5, 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
		setDescription("Checks if they are using LB's anticactus.");
	}

	@Override
	public void listen(PacketEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

		if (Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "CACTUS", 0.2, 0.2)
				|| Main.getInstance().getLocUtils().isCollided(pd.getPlayer().getLocation(), "CACTUS", 0.2, 0.2)) {
			pd.getPlayer().sendMessage("collided");
			final long diff = pd.flyingA - pd.lastFlyingA;
			if (diff <= 50 && (pd.getDeltaXZ() > 0 || Math.abs(pd.getDeltaY()) > 0)
					&& pd.getPlayer().getNoDamageTicks() == 0) {
				pd.antiCactus++;
				if (pd.antiCactus >= 5) {
					flag(pd, diff + " < " + 30);
					pd.getPlayer().sendMessage("diff in ms: " + diff + " | collided: " + true);
					pd.antiCactus = 0;
				}
			} else {
				if (pd.antiCactus > 0) {
					pd.antiCactus--;
				}
			}
		}
	}

}
