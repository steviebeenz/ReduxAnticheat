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
		super("BadPackets [C]", 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 90);
		setDescription("Checks if a player is spoofing their ground meaning they won't take any damage.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

			if(pd.teleportTicks > 0) {
				return;
			}
			
			if (locUtils.isCollided(pd.getNextLocation(), Material.AIR)
					&& locUtils.isCollided(pd.getLastLocation(), Material.AIR)) {
				if (!locUtils.isOnSolidGround(p.getLocation())
						&& !locUtils.isOnSolidGround(pd.getNextLocation())
						&& !locUtils.isOnSolidGround(pd.getLastLocation())) {
					if (ReflectionUtils.getOnGround(p)) {
						if (!locUtils.canClimb(p)
								&& !locUtils.isOnSmallBlock(pd.getNextLocation())
								&& !locUtils.isOnSmallBlock(pd.getLastLocation())
								&& !locUtils.isOnSlime(pd.getNextLocation())
								&& !locUtils.isInLiquid(pd.getNextLocation())) {

							if (locUtils.isCollided(pd.getLastLocation(), "FENCE")
									|| locUtils.isCollided(pd.getNextLocation(), "FENCE")
									|| locUtils.isCollided(pd.getLastLocation(), "WALL")
									|| locUtils.isCollided(pd.getNextLocation(), "WALL")
									|| locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation())) {
								pd.badPacketsC = 0;
								return;
							}

							pd.badPacketsC++;
							if (pd.badPacketsC >= 3) {
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
