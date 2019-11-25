package redux.anticheat.check.packets.movement;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class Nofall extends PacketCheck {

	public Nofall() {
		super("Nofall", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 75);
		setDescription("Checks if a player is changing their ground packet.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if (!ReflectionUtils.getOnGround(p)
				&& Main.getInstance().getLocUtils().isCollided(p.getLocation(), Material.AIR)) {

			if (System.currentTimeMillis() - pd.getLastTeleported() < 1500) {
				return;
			}

			if (System.currentTimeMillis() - pd.join < 1500) {
				return;
			}

			if (p.isFlying()) {
				return;
			}

			if (pd.teleportTicks > 0) {
				return;
			}

			final Block b = Main.getInstance().getLocUtils().getBlockUnder(pd.getNextLocation());
			if (b != null) {

			} else {
				pd.nfVl = 0;
				pd.nf2vl = 0;
				return;
			}

			final double diff = (pd.getDeltaY() - pd.getLastDeltaY());
			if (diff != 0) {
				if (diff < 0) {
					if (Main.getInstance().getLocUtils().isCollided(p.getLocation(), Material.AIR)
							&& ReflectionUtils.getOnGround(p)
							&& !Main.getInstance().getLocUtils().isInLiquid(p.getLocation())
							&& !Main.getInstance().getLocUtils().isClimbable(p.getLocation().getBlock().getType())
							&& !Main.getInstance().getLocUtils().isUnderStairs(p.getLocation())
							&& !Main.getInstance().getLocUtils().isUnderStairs(pd.getNextLocation())
							&& !Main.getInstance().getLocUtils().isUnderStairs(pd.getLastLocation())) {
						if (++pd.nfVl >= 2) {
							flag(pd, pd.nfVl + " >= " + 2 + ", deltaY: " + pd.getDeltaY());
							pd.nfVl = 0;
						}
					} else {
						pd.nfVl = 0;
					}
				} else {
					pd.nfVl = 0;
				}
			}
		} else {
			if (!Main.getInstance().getLocUtils().isOnSlime(pd.getNextLocation())
					&& !Main.getInstance().getLocUtils().isOnSlime(pd.getLastLocation())
					&& !ReflectionUtils.getOnGround(p)) {
				if (pd.onGroundTicks == 0 && pd.offGroundTicks > 20
						&& !Main.getInstance().getLocUtils().isClimbable(p.getLocation().getBlock().getType())
						&& !Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())
						&& !Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())
						&& !Main.getInstance().getLocUtils().isInLiquid(p.getLocation())
						&& !Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation()) && !Main.getInstance()
								.getLocUtils().isCollidedWeb(pd.getLastLocation(), pd.getNextLocation())) {
					if (pd.getDeltaY() <= 0 && pd.flyTicks == 0
							&& (System.currentTimeMillis() - pd.getLastOnSlime()) > 1000 && pd.vehicleTicks < 10) {
						if (++pd.nf2vl >= 8) {
							flag(pd, pd.nf2vl + " >= " + 8 + " delta y: " + pd.getDeltaY());

							pd.nf2vl = 0;
						}
					} else {
						pd.nf2vl = 0;
					}
				} else {
					if (pd.nf2vl > 0) {
						pd.nf2vl--;
					}
				}
			} else {
				pd.nf2vl = 0;
			}
		}

	}

}
