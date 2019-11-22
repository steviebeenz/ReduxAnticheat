package redux.anticheat.check.packets.movement;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class Jesus extends PacketCheck {

	public Jesus() {
		super("Jesus", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 70);
		setDescription("Checks if a player is walking on top of a liquid.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		final boolean oldCollided = Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), Material.WATER)
				|| Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), Material.LAVA);
		final boolean newCollided = Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), Material.WATER)
				|| Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), Material.LAVA);

		if (oldCollided && newCollided) {
			if (pd.flyTicks > 10) {
				pd.jesus = 0;
				return;
			}

			final Block b = (Main.getInstance().getLocUtils().getSecondBlockDown(pd.getNextLocation()) != null
					? Main.getInstance().getLocUtils().getSecondBlockDown(pd.getNextLocation())
					: null);

			for (final Entity ent : p.getWorld().getNearbyEntities(p.getLocation(), 2, 2, 2)) {
				if (ent instanceof Boat) {
					pd.jesus = 0;
				}
			}

			if (b != null) {
				if (b.isLiquid()) {
					if (ReflectionUtils.getOnGround(p)) {
						if (!Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())
								&& !Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation())) {
							if (!Main.getInstance().getLocUtils().isOnSmallBlock(pd.getNextLocation())) {
								pd.jesus++;
								if (pd.jesus >= 3) {
									flag(pd, pd.jesus + " >= 2");
								}
							}
						}
					} else {
						if (pd.jesus > 0) {
							pd.jesus--;
						}
					}
				}
			}

			if (pd.jesus > 0) {
				pd.jesus--;
			}
		}
	}

}
