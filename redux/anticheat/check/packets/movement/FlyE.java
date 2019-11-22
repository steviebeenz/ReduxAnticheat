package redux.anticheat.check.packets.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class FlyE extends PacketCheck {

	private final boolean shouldFlag = true;

	public FlyE() {
		super("Fly [E]", 1, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		setDescription("Checks if a player is moving faster than normal.");
		settings.put("max_ground", 2.8076494873881725);
		settings.put("min_ground", 0.00007115656465271);
		settings.put("max_off_ground", 2.1959840585782076);
		settings.put("min_off_ground", 0.00003077058958932);
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

			if (p.isFlying()) {
				return;
			}

			if (pd.getLastLocation() == null) {
				pd.setLastLocation(p.getLocation());
				return;
			}

			if (System.currentTimeMillis() - pd.getLastTeleported() < 1500) {
				return;
			}

			if (pd.flyTicks > 0 || pd.changeTicks > 0) {
				return;
			}

			final Location newLoc = pd.getNextLocation(), oldLoc = pd.getLastLocation();

			final double newLocation = Math.abs(newLoc.getX() + newLoc.getY() + newLoc.getZ()),
					oldLocation = Math.abs(oldLoc.getX() + oldLoc.getY() + oldLoc.getZ());

			final double dist = Math.abs(newLocation - oldLocation);

			double packetLimit = 0.42;

			for (final PotionEffect pe : p.getActivePotionEffects()) {
				if (pe.getType().equals(PotionEffectType.SPEED) || pe.getType().equals(PotionEffectType.JUMP)) {
					final int amplifier = pe.getAmplifier();
					packetLimit *= 1 + (amplifier * 0.2);
				}
			}

			if (pd.getDeltaXZ() > packetLimit && pd.blockPlacePacket > 19) {
				flag(pd, pd.getDeltaXZ() + " > " + packetLimit);
			}

			if (Main.getInstance().getLocUtils().isInLiquid(newLoc)
					|| Main.getInstance().getLocUtils().isInLiquid(oldLoc)) {
				return;
			}

			if (pd.blockAboveTicks > 0) {
				return;
			}

			double maxGround = (double) settings.get("max_ground"),
					maxOffGround = (double) settings.get("max_off_ground"),
					minGround = (double) settings.get("min_ground"),
					minOffGround = (double) settings.get("min_off_ground");

			if (pd.eatTicks > 0) {
				minGround -= 0.000015;
			}

			if (pd.offGroundTicks > 10) {
				final double times = pd.offGroundTicks - 10;
				maxOffGround += (0.15 * times);
			}

			if (pd.blockPlacePacket > 8) {
				minOffGround -= 0.0004;
			}

			if (pd.velocTicks > 0) {
				return;
			}

			if (System.currentTimeMillis() - pd.getLastOnSlime() < 1500) {
				return;
			}

			for (final PotionEffect pe : p.getActivePotionEffects()) {
				if (pe.getType().equals(PotionEffectType.SPEED) || pe.getType().equals(PotionEffectType.JUMP)) {
					final int amplifier = pe.getAmplifier();
					maxGround *= 1 + (amplifier * 0.2);
					maxOffGround *= 1 + (amplifier * 0.2);
				}
			}

			if (p.getWalkSpeed() > 0.2F) {
				maxGround *= (p.getWalkSpeed() / 0.2);
				maxOffGround *= (p.getWalkSpeed() / 0.2);
			} else if (p.getWalkSpeed() < 0.2F) {
				minGround *= (p.getWalkSpeed() / 0.2);
				minOffGround *= (p.getWalkSpeed() / 0.2);
			}

			if (dist == 0) {
				return;
			}

			if (pd.blockAboveTicks > 0) {
				return;
			}

			if (pd.iceTicks > 0) {
				return;
			}

			if (pd.vehicleTicks > 0) {
				return;
			}

			if (pd.teleportTicks > 0) {
				return;
			}

			if (System.currentTimeMillis() - pd.join < 1500) {
				return;
			}

			if (Main.getInstance().getLocUtils().isCollided(newLoc, Material.SOUL_SAND)
					|| Main.getInstance().getLocUtils().isCollided(oldLoc, Material.SOUL_SAND)
					|| Main.getInstance().getLocUtils().isCollidedWeb(oldLoc, newLoc)) {
				minGround -= 0.0004;
				minOffGround -= 0.0004;
			}

			if (Main.getInstance().getLocUtils().canClimb(oldLoc)
					|| Main.getInstance().getLocUtils().canClimb(newLoc)) {
				return;
			}

			if (ReflectionUtils.getPing(p) > 0) {
				maxGround *= 1 + (ReflectionUtils.getPing(p) / 100);
				maxOffGround *= 1 + (ReflectionUtils.getPing(p) / 100);
			}

			if (shouldFlag) {
				if (pd.offGroundTicks > 0) {
					if (dist > maxOffGround && maxOffGround != 0) {
						flag(pd, dist + " > " + maxOffGround + " (maxOffGround)");
						// p.sendMessage("diff maxoff: " + (dist - maxOffGround));
					}
					if (dist < minOffGround && minOffGround != 0) {
						flag(pd, dist + " < " + minOffGround + " (minOffGround)");
						// p.sendMessage("diff minoffground: " + (dist - minOffGround));
					}
				} else {
					if (dist > maxGround && maxGround != 0) {
						flag(pd, dist + " > " + maxGround + " (maxGround)");
						// p.sendMessage("diff maxGround: " + (dist - maxGround));
					}
					if (dist < minGround && minGround != 0) {
						flag(pd, dist + " < " + minGround + " (minGround)");
						// p.sendMessage("diff minGround: " + (dist - minGround));
					}
				}
			}

			return;
		}
	}

}
