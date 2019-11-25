package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class Fly extends PacketCheck {

	public Fly() {
		super("Fly", 10, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION },
				true, 85);
		settings.put("limit", 13);
		setDescription("Checks if a player isn't falling in air.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		final double curY = pd.getDeltaY(), prevY = pd.getLastDeltaY();

		if (p.isFlying()) {
			pd.sameY = 0;
			return;
		}

		if (pd.getDeltaXZ() < 0.01) {
			pd.sameY = 0;
			return;
		}

		if (curY == prevY) {
			pd.sameY++;
		} else {
			if (pd.sameY > 0) {
				pd.sameY--;
			} else if (pd.sameY < 0) {
				pd.sameY = 0;
			}
		}

		if (locUtils.canClimb(pd.getNextLocation())
				|| locUtils.canClimb(pd.getLastLocation())
				|| locUtils.isInLiquid(pd.getNextLocation())
				|| locUtils.isInLiquid(pd.getLastLocation())
				|| locUtils.isOnSlime(pd.getNextLocation())
				|| locUtils.isUnderStairs(pd.getLastLocation())
				|| locUtils.isUnderStairs(pd.getNextLocation())
				|| locUtils.isCollidedWeb(pd.getNextLocation(), pd.getLastLocation())
				|| locUtils.isCollided(pd.getLastLocation(), "FENCE")
				|| locUtils.isCollided(pd.getNextLocation(), "FENCE")
				|| locUtils.isCollided(pd.getLastLocation(), "WALL")
				|| locUtils.isCollided(pd.getNextLocation(), "WALL")) {
			pd.sameY = 0;
			return;
		}

		for (final PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().getName().contains("levitation")) {
				pd.sameY = 0;
				return;
			}
		}

		if (!locUtils.isOnSolidGround(pd.getNextLocation())
				|| !ReflectionUtils.getOnGround(p)) {
			for (final ItemStack i : p.getInventory().getArmorContents()) {
				if (i.getType().name().contains("ELYTRA")) {
					pd.sameY = 0;
					return;
				}
			}
		} else {
			if (pd.sameY > 0) {
				pd.sameY--;
			} else if (pd.sameY < 0) {
				pd.sameY = 0;
			}
		}

		final int limit = (int) settings.get("limit");

		if (pd.sameY >= limit) {
			flag(pd, pd.sameY + " >= " + limit + "(limit)");
			pd.sameY = 0;
		}

	}

}
