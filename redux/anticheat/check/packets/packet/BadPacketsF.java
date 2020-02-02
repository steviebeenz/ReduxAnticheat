package redux.anticheat.check.packets.packet;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class BadPacketsF extends PacketCheck {

	public BadPacketsF() {
		super("BadPackets [F]", 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.ARM_ANIMATION }, true, 65);
		setDescription("Checks if a player is placing blocks without swinging as much as needed.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.blockPlaceData != null) {
			if (pd.blockPlacePacket >= 3) {
				final double diffInPackets = ((double) pd.blockPlacePacket / 3);
				if (diffInPackets > (pd.swingPackets + ReflectionUtils.getPingModifier(p))) {
					flag(pd, diffInPackets + " > " + (pd.swingPackets + ReflectionUtils.getPingModifier(p)));
				}
			}

		}
	}

}
