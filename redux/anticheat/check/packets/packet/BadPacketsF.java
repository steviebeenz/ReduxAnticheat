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
		super("BadPackets [F]", 5, 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.ARM_ANIMATION }, true, 65);
		this.setDescription("Checks if a player is placing blocks without swinging as much as needed.");
	}

	/*
	 * 
	 * NO ARM ANIMATION BEFORE PLACE
	 * 
	 * (non-Javadoc)
	 * 
	 * @see redux.anticheat.check.PacketCheck#listen(com.comphenix.protocol.events.
	 * PacketEvent)
	 */

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.blockPlaceData != null) {
			if (pd.blockPlacePacket >= 3) {
				double diffInPackets = ((double)pd.blockPlacePacket / 3);
				if (diffInPackets > (pd.swingPackets + ReflectionUtils.getPingModifier(p))) {
					flag(pd, diffInPackets + " > " + (pd.swingPackets + ReflectionUtils.getPingModifier(p)));
				}
			}

		}
	}

}
