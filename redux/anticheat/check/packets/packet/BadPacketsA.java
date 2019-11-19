package redux.anticheat.check.packets.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class BadPacketsA extends PacketCheck {

	private final int limit = 26;

	public BadPacketsA() {
		super("BadPackets [A]", 5, 40, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 100);
		this.setDescription("Checks if a player is sending more packets in a second.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
			
			if(System.currentTimeMillis() - pd.join < 1500) {
				return;
			}
			
			double trueLimit = (limit + ReflectionUtils.getPingModifier(e.getPlayer())
					+ (Math.abs(20 - Main.getInstance().getTpsTask().tps) * 3));

			if (System.currentTimeMillis() - pd.getLastTeleported() < 1500) {
				trueLimit += Math.abs(1500 - (System.currentTimeMillis() - pd.getLastTeleported())) / 1000;
			}

			if (System.currentTimeMillis() - pd.join < 1000) {
				trueLimit += Math.abs(1000 - (System.currentTimeMillis() - pd.join)) / 20;
			}
			
			if(pd.teleportTicks > 0) {
				trueLimit += Math.abs(0 - ((double)pd.teleportTicks / 2));
				return;
			}
			
			trueLimit += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.193;

			trueLimit += (pd.vehicleTicks * 0.75);

			if (pd.getPackets() > trueLimit) {
				flag(pd, pd.getPackets() + " > " + trueLimit);
			}

			final long delay = System.currentTimeMillis() - pd.lastFlyingA;

			if (delay < 20L) {
				return;
			}

			if (ReflectionUtils.getPing(e.getPlayer()) > (delay * 50L) * 2.75) {
				flag(pd, "pingspoof");
			}

		}
	}

}
