package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class NoSlow extends PacketCheck {

	public NoSlow() {
		super("NoSlow", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 60);
		setDescription("Checks if a player is not slowing down.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.getDeltaXZ() == 0) {
			return;
		}

		if (p.isFlying()) {
			pd.flyTicks = 20;
			return;
		} else {
			if (pd.flyTicks > 0) {
				pd.flyTicks--;
			}
		}

		if (p.isBlocking()) {
			pd.blockingTicks++;
		} else {
			if (pd.blockingTicks > 0) {
				pd.blockingTicks = 0;
			}
		}

		if (pd.teleportTicks > 0) {
			return;
		}

		if (p.getOpenInventory().getType().equals(InventoryType.CHEST)
				|| p.getOpenInventory().getType().equals(InventoryType.ENDER_CHEST)
				|| p.getOpenInventory().getType().equals(InventoryType.FURNACE)
				|| p.getOpenInventory().getType().equals(InventoryType.WORKBENCH)
				|| p.getOpenInventory().getType().equals(InventoryType.PLAYER)) {
			pd.inventoryTicks++;
		} else {
			if (pd.inventoryTicks > 0) {
				pd.inventoryTicks = 0;
			}
		}

		if (p.isSneaking()) {
			if (pd.sneakingTicks < 20) {
				pd.sneakingTicks++;
			}
		} else {
			if (pd.sneakingTicks > 0) {
				pd.sneakingTicks = 0;
			}
		}

		if (pd.blockingTicks > 1) {

			if (p.isInsideVehicle()) {
				pd.blockingTicks = 0;
				return;
			}

			double limit = Math.abs(1 - ((pd.blockingTicks * 0.05)));

			if (limit < 0.3) {
				limit = 0.3;
			}

			if (p.getNoDamageTicks() > 0) {
				limit *= 1 + (p.getNoDamageTicks() / 10);
			}

			if (p.getWalkSpeed() > 0.2f) {
				limit *= (p.getWalkSpeed() / 0.2f);
			}

			if (pd.getDeltaXZ() > limit) {
				flag(pd, pd.getDeltaXZ() + " > " + limit);
			}
		}

		if (pd.sneakingTicks > 0) {
			if (pd.vehicleTicks > 0) {
				pd.sneakingTicks = 0;
				return;
			}

			final double times = Math.abs(1 - (pd.sneakingTicks / 1000));
			double limit = 0.42 * times;
			limit += (pd.offGroundTicks / 100);

			final double iceTimes = 1 + ((double) pd.iceTicks / 70) + 0.3;
			limit *= iceTimes;

			if (pd.flyTicks > 0) {
				return;
			}

			if (p.getNoDamageTicks() > 0) {
				limit *= 1 + (p.getNoDamageTicks() / 10);
			}

			if (p.getWalkSpeed() > 0.2f) {
				limit *= (p.getWalkSpeed() / 0.2f);
			}

			if (pd.getDeltaXZ() > limit) {
				flag(pd, pd.getDeltaXZ() + " > " + limit);
			}
		}

		if (pd.inventoryTicks > 0) {

			if (pd.vehicleTicks > 0) {
				pd.moreInventorySpeed = 0;
				return;
			}

			if (pd.getDeltaXZ() > 0.2 && pd.getDeltaY() >= 0) {
				pd.moreInventorySpeed++;
			} else {
				if (pd.moreInventorySpeed > 0) {
					pd.moreInventorySpeed--;
				}
			}

			if (pd.moreInventorySpeed > 14 && pd.offGroundTicks == 0) {
				flag(pd, pd.moreInventorySpeed + " > " + 14);
			}
		}
		// 6 == 3
	}

}
