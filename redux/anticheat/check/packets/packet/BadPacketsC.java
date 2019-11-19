package redux.anticheat.check.packets.packet;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class BadPacketsC extends PacketCheck {

	public BadPacketsC() {
		super("BadPackets [C]", 5, 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
		this.setDescription("Checks if a player is spoofing their ground meaning they won't take any damage.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

			if (Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), Material.AIR)
					&& Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), Material.AIR)) {
				if (!Main.getInstance().getLocUtils().isOnGround(p) && !Main.getInstance().getLocUtils().isOnSolidGround(p.getLocation())
						&& !Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation())) {
					if (ReflectionUtils.getOnGround(p)) {
						if (!Main.getInstance().getLocUtils().canClimb(p) && !Main.getInstance().getLocUtils().isOnSmallBlock(pd.getNextLocation())
								&& !Main.getInstance().getLocUtils().isOnSmallBlock(pd.getLastLocation())
								&& !Main.getInstance().getLocUtils().isOnSlime(pd.getNextLocation())
								&& !Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation())
								&& !Main.getInstance().getLocUtils().isUnderStairs(pd.getNextLocation())
								&& !Main.getInstance().getLocUtils().isUnderStairs(pd.getLastLocation())) {

							if (Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), "FENCE")
									|| Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "FENCE")
									|| Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), "WALL")
									|| Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "WALL")) {
								pd.badPacketsC = 0;
								return;
							}

							pd.badPacketsC++;
							if (pd.badPacketsC >= 2) {
								flag(pd, pd.badPacketsC + " >= 2");
								pd.badPacketsC = 0;
							}
						} else {
							if (pd.badPacketsC > 0) {
								pd.badPacketsC--;
							}
						}
					} else {
						if (pd.badPacketsC > 0) {
							pd.badPacketsC--;
						}
					}
				}
			}
		}
	}

}
