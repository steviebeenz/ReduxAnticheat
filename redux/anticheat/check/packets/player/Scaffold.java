package redux.anticheat.check.packets.player;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class Scaffold extends PacketCheck {

	public Scaffold() {
		super("Scaffold", 10, null, false, true, Category.PLAYER,
				new PacketType[] { PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.POSITION,
						PacketType.Play.Client.POSITION_LOOK },
				true, 70);
		setDescription("Checks if a player is sending scaffold like packets.");
		settings.put("diff", 9);
		settings.put("total", 26);
		settings.put("blocks", 3);
		settings.put("without", 14);
		settings.put("rate", 0.14);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.teleportTicks > 0) {
			return;
		}

		if (e.getPacketType().equals(getType()[0])) {

		} else if (e.getPacketType().equals(getType()[1])) {
			pd.position++;

			if (pd.blockPlaceData != null && System.currentTimeMillis() - pd.blockPlaceData.getTime() <= 10) {
				pd.blockPlace++;
			}

			if (pd.blockPlaceData != null && pd.lastBlockPlaceData != null) {
				if (pd.lastBlockPlaceData.getTime() != 0L && pd.blockPlaceData.getTime() != 0L) {
					final int diff = (int) settings.get("diff");
					if (pd.blockPlaceData.getPlaced().getType().isSolid()
							&& pd.lastBlockPlaceData.getPlaced().getType().isSolid()) {
						if ((pd.blockPlaceData.getTime() - pd.lastBlockPlaceData.getTime())
								+ ReflectionUtils.getPingModifier(p) < diff) {
							flag(pd, (pd.blockPlaceData.getTime() - pd.lastBlockPlaceData.getTime())
									+ ReflectionUtils.getPingModifier(p) + " < " + diff);
							pd.lastBlockPlaceData = null;
						}
					}
				}
			}

		} else {
			pd.positionLook++;
		}

		if (System.currentTimeMillis() - pd.lastSent >= 1000) {
			final int total = (pd.blockPlace + pd.position + pd.positionLook);
			final int totalWithout = (pd.blockPlace + pd.positionLook);
			double rate = 0;

			if (pd.blockPlace > 0) {
				rate = (double) pd.blockPlace / total;
			}

			final int totalLimit = (int) settings.get("total");
			final int blocks = (int) settings.get("blocks");
			final int without = (int) settings.get("without");
			final double rateLimit = (double) settings.get("rate");

			if (total >= totalLimit && pd.blockPlace > 0 || pd.blockPlace >= blocks
					|| totalWithout >= without && pd.blockPlace > 0 || rate >= rateLimit && total > 14) {
				flag(pd, total + " >= " + totalLimit + " | " + pd.blockPlace + " >= " + blocks + " | " + totalWithout
						+ " >= " + without + " | " + Math.round(rate) + " >= " + rateLimit);
			}
			pd.lastSent = System.currentTimeMillis();
			pd.blockPlace = 0;
			pd.position = 0;
			pd.positionLook = 0;
		}
	}

}
