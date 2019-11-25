package redux.anticheat.check.packets.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class Killaura extends PacketCheck {

	private PacketListener listener;

	public Killaura() {
		super("Killaura [Packets]", 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 80);
		setDescription("Checks if a player sends too many packets.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final PacketContainer packet = e.getPacket();
			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
			if (packet.getType().equals(PacketType.Play.Client.USE_ENTITY)) {
				if (packet.getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {
					final Entity ent = getEntityFromPacket(packet, p);
					if (ent != null) {
						if (pd.nullEntity > 0) {
							pd.nullEntity--;
						}

						final double diff = Math.abs(System.currentTimeMillis() - pd.getLastMovement())
								+ ReflectionUtils.getPingModifier(p)
								+ Math.abs(20 - Main.getInstance().getTpsTask().tps);

						if (diff < 8) {
							pd.killauraPackets++;
							if (pd.killauraPackets > 1) {
								flag(pd, diff + " < 8");
								pd.killauraPackets = 0;
							}
						} else {
							if (pd.killauraPackets > 0) {
								pd.killauraPackets = 0;
							}
						}
					} else {
						pd.nullEntity++;
						if (pd.nullEntity > 2) {
							setName("Killaura [Invalid]");
							flag(pd, "invalid >= 2");
							return;
						}
					}
				} else {
					return;
				}
			} else {
				return;
			}
			return;
		}
	}

	public PacketListener getListener() {
		return listener;
	}

}
