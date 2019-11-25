package redux.anticheat.check.packets.packet;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class BadPacketsH extends PacketCheck {

	public BadPacketsH() {
		super("BadPackets [H]", 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.ARM_ANIMATION }, false, 80);
		setDescription("Checks for killaura like packets.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.flyTicks > 0 || p.isFlying()) {
			return;
		}

		if ((System.currentTimeMillis() - pd.flyingA) + ReflectionUtils.getPingModifier(p)
				+ Math.abs(20 - Main.getInstance().getTpsTask().tps) < 8) {
			flag(pd, "flying < 8 (experimental)");
		}

	}

}
