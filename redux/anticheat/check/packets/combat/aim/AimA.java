package redux.anticheat.check.packets.combat.aim;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class AimA extends PacketCheck {

	public AimA() {
		super("Aim [A]", 5, 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.POSITION,
						PacketType.Play.Client.POSITION_LOOK },
				true, 75);
		setDescription("Checks the average speed when hitting.");
		settings.put("average_speed", 0.4);
		settings.put("average_diff", 0.2);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if (e.getPacketType().equals(getType()[0])) {
			if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {
				final Entity ent = getEntityFromPacket(e.getPacket(), e.getPlayer());

				if (ent == null) {
					return;
				}

				pd.hitTicks = 20;
			}
		} else if (e.getPacketType().equals(getType()[1]) || e.getPacketType().equals(getType()[2])) {
			pd.lowFlyingInVl = 0;
			if (pd.getDeltaXZ() > 0.05 && --pd.hitTicks >= 0) {
				final double diff = Math.abs(pd.getDeltaXZ() - pd.getLastDeltaXZ());

				pd.hits.add(new HitData(diff, pd.getDeltaXZ(), pd.hitTicks));

				if (pd.flyTicks > 0 || p.isFlying()) {
					pd.hits.clear();
					return;
				}

				if (pd.hits.size() >= 20) {

					final double[] avgs = average(pd);

					double limitMultiply = 1;

					for (final PotionEffect pe : p.getActivePotionEffects()) {
						if (pe.getType().equals(PotionEffectType.SPEED)) {
							limitMultiply += (pe.getAmplifier() * 0.06);
						}
					}

					limitMultiply += Math.abs(ReflectionUtils.getPingModifier(p) * 0.12);
					limitMultiply += Math.abs(pd.getVelocity()) * 0.12;

					if (pd.teleportTicks > 0) {
						pd.hits.clear();
						return;
					}

					if (avgs[0] >= ((double) settings.get("average_speed") * limitMultiply)) {
						flag(pd, avgs[0] + " >= " + ((double) settings.get("average_speed") * limitMultiply)
								+ "(speed)");
					}

					if (avgs[1] >= ((double) settings.get("average_diff") * limitMultiply)) {
						flag(pd, avgs[1] + " >= " + ((double) settings.get("average_diff") * limitMultiply) + "(diff)");
					}

					pd.hits.clear();
				}
			}

		}

	}

	/*
	 * max diff hitdata: diff: 0.001176 speed: 0.24193 ticks: 9
	 *
	 * max speed data: diff: 0.012 hitticks: 8 speed: 0.25
	 */

	private double[] average(PlayerData pd) {
		double avgSpeed = 0, avgDiff = 0;

		if (pd.hits.size() < 20) {
			return new double[] { 0, 0 };
		}

		for (final HitData d : pd.hits) {
			avgSpeed += d.speed;
			avgDiff += d.diff;
		}

		avgSpeed = (avgSpeed / pd.hits.size());
		avgDiff = (avgDiff / pd.hits.size());

		return new double[] { avgSpeed, avgDiff };
	}

	public class HitData {

		private final double diff;
		private final double speed;
		private final int hitticks;

		private HitData(double diff, double speed, int hitticks) {
			this.diff = diff;
			this.speed = speed;
			this.hitticks = hitticks;
		}

		public double getDiff() {
			return diff;
		}

		public double getSpeed() {
			return speed;
		}

		public double getHitticks() {
			return hitticks;
		}

	}

}
