package redux.anticheat.check.packets.packet;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class BadPacketsD extends PacketCheck {
	public BadPacketsD() {
		super("BadPackets [D]", 5, 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.FLYING }, true, 90);
		setDescription("Checks if a player is sending flying packets weirdly.");
	}

	@Override
	public void listen(PacketEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (System.currentTimeMillis() - pd.resetPacketsD >= 1000) {
			double rate = ((double) pd.flyingInPackets / 20);

			if (pd.getPlayer().isFlying() || pd.flyTicks > 0) {
				rate = 0;
				pd.flyingInVl = 0;
				return;
			}

			if (System.currentTimeMillis() - pd.join < 2000) {
				rate = 0;
				pd.flyingInVl = 0;
				return;
			}

			double limit = 5;

			for (final PotionEffect pe : pd.getPlayer().getActivePotionEffects()) {
				if (pe.getType().equals(PotionEffectType.JUMP)) {
					final double amplifier = pe.getAmplifier();
					limit += amplifier * 1.2;
				}
			}

			if (pd.offGroundTicks >= 12) {
				if (pd.flyingInPackets >= limit) {
					if (locUtils.isCollided(pd.getNextLocation(), Material.AIR)
							&& locUtils.isCollided(pd.getLastLocation(), Material.AIR)
							&& !locUtils.isOnSolidGround(pd.getNextLocation())
							&& !locUtils.isOnSolidGround(pd.getLastLocation())) {
						flag(pd, pd.flyingInPackets + " >= " + limit);
					}
				}
			}

			double rateLimit = 2;
			
			rateLimit += ReflectionUtils.getPingModifier(pd.getPlayer()) * 0.12;
			rateLimit += Math.abs(20 - Main.getInstance().getTpsTask().tps) * 0.08;
			
			if (rate >= rateLimit && pd.flyingInPackets > 5) {
				flag(pd, rate + " >= " + rateLimit);
			}

			if (rate >= 1.05) {
				pd.flyingInVl++;
			} else {
				if (pd.flyingInVl > 0) {
					pd.flyingInVl--;
				}
			}

			if (rate <= 0.1) {
				pd.lowFlyingInVl++;
				if (pd.wasFalling) {
					pd.lowFlyingInVl = 0;
				}
				if (pd.lowFlyingInVl > 5) {
					pd.lowFlyingInVl = 0;
					flag(pd, rate + " <= 0.1");
				}
			} else {
				if (pd.lowFlyingInVl > 0) {
					pd.lowFlyingInVl--;
				}
			}

			if (pd.flyingInVl >= 5) {
				flag(pd, pd.flyingInVl + " >= 5");
			}

			pd.flyingInPackets = 0;
			pd.resetPacketsD = System.currentTimeMillis();
		} else {
			pd.flyingInPackets++;
		}

	}

}
