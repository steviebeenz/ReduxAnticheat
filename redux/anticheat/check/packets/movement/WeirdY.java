package redux.anticheat.check.packets.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class WeirdY extends PacketCheck {

	public WeirdY() {
		super("WeirdY", 1, 10, null, true, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 80);
		settings.put("sample_count", 10);
		setDescription("Checks if a player's motion is unusual.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

			if (pd == null) {
				return;
			}

			final Location oldLoc = pd.getLastLocation(), newLoc = pd.getNextLocation();

			if (pd.getDeltaY() == 0) {
				return;
			}

			if (System.currentTimeMillis() - pd.getLastTeleported() < 1000
					|| System.currentTimeMillis() - pd.join < 1000 || p.isFlying() || pd.flyTicks > 0
					|| System.currentTimeMillis() - pd.getLastOnSlime() < 1000 || pd.vehicleTicks > 0
					|| pd.teleportTicks > 0 || pd.velocTicks > 0 || pd.blockAboveTicks > 0) {
				if (!pd.moves.isEmpty()) {
					pd.moves.clear();
				}
				return;
			}

			if (Main.getInstance().getLocUtils().isUnderStairs(pd.getLastLocation())
					|| Main.getInstance().getLocUtils().isUnderStairs(pd.getNextLocation())
					|| Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "SLAB")
					|| Main.getInstance().getLocUtils().isCollided(newLoc, "CARPET")
					|| Main.getInstance().getLocUtils().isInLiquid(newLoc)
					|| Main.getInstance().getLocUtils().canClimb(newLoc)
					|| Main.getInstance().getLocUtils().isOnSlime(newLoc)) {
				if (!pd.moves.isEmpty()) {
					pd.moves.clear();
				}
				return;
			}

			if (Main.getInstance().getLocUtils().isCollided(oldLoc, "SKULL")
					|| Main.getInstance().getLocUtils().isCollided(newLoc, "SKULL")
					|| Main.getInstance().getLocUtils().isCollided(oldLoc, "TRAP")
					|| Main.getInstance().getLocUtils().isCollided(newLoc, "TRAP")) {
				if (!pd.moves.isEmpty()) {
					pd.moves.clear();
				}
				return;
			}

			if (oldLoc.getY() < newLoc.getY()) {
				final double d = Math.abs(pd.getDeltaY() - pd.getPreviousDeltaY());
				pd.moves.add(new MoveData(d, pd.getDeltaY()));
			}

			if (pd.moves.size() >= (int) settings.get("sample_count")) {
				getHighestValues(pd);
				pd.moves.clear();
			}

		}
	}

	private void getHighestValues(PlayerData pd) {
		double avgSpeed = 0, max = 0, min = 0;

		for (final MoveData d : pd.moves) {

			min = 1000;
			if (d.getDeltaY() < min) {
				min = d.getDeltaY();
			}

			if (max < d.getDeltaY()) {
				max = d.getDeltaY();
			}

			avgSpeed += d.getDeltaY();
		}

		avgSpeed = (avgSpeed / pd.moves.size());

		double maxSpeed = 0.43, averageSpeed = 0.3423, minSpeed = 0.083;

		if ((int) settings.get("sample_count") != 20) {
			final int multi = (int) settings.get("sample_count");
			maxSpeed = maxSpeed / 100;
			maxSpeed *= 90 + multi;
			averageSpeed = averageSpeed / 100;
			averageSpeed *= 105 + multi;
			minSpeed = minSpeed / 100;
			minSpeed *= 90 + multi;
		}

		for (final PotionEffect pe : pd.getPlayer().getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.JUMP) || pe.getType().equals(PotionEffectType.SPEED)) {
				maxSpeed *= 1 + (pe.getAmplifier() * 0.09);
				averageSpeed *= 1 + (pe.getAmplifier() * 0.09);
				minSpeed *= 1 - (pe.getAmplifier() * 0.09);
			}
		}

		if (pd.getPlayer().getWalkSpeed() > 0.2f) {
			maxSpeed *= (pd.getPlayer().getWalkSpeed() / 0.2f);
			averageSpeed *= (pd.getPlayer().getWalkSpeed() / 0.2f);
		}

		if (max > (maxSpeed + Math.abs(pd.getDeltaXZ()))) {
			pd.maxYflag++;
			if (pd.maxYflag >= 3) {
				flag(pd, pd.maxYflag + " >= " + 3);
			}
		} else {
			pd.maxYflag--;
		}

		if (avgSpeed > averageSpeed) {
			flag(pd, avgSpeed + " > " + averageSpeed);
			// pd.getPlayer().sendMessage("diff: " + (avgSpeed - averageSpeed));
		}

		if (min < minSpeed) {
			flag(pd, min + " < " + minSpeed);
			pd.getPlayer().sendMessage("diff min: " + (min - minSpeed));
		}

	}

	public class MoveData {

		private double diff;
		private double speed;

		public MoveData(double diff, double speed) {
			this.diff = diff;
			this.speed = speed;
		}

		public double getDiff() {
			return diff;
		}

		public void setDiff(double diff) {
			this.diff = diff;
		}

		public double getDeltaY() {
			return speed;
		}

		public void setSpeed(double speed) {
			this.speed = speed;
		}

	}

}
