package redux.anticheat.check.packets.movement;

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

public class Speed extends PacketCheck {

	public Speed() {
		super("Speed [A]", 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 80);
		settings.put("limit", 0.71);
		setDescription("Checks if a player's speed is more than a dynamic limit.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

			if (p.isFlying() || pd.flyTicks > 0) {
				return;
			}

			if (pd.getDeltaXZ() == 0 && pd.getLastDeltaXZ() == 0) {
				return;
			}

			double maxDist = (double) settings.get("limit");

			if (System.currentTimeMillis() - pd.getLastOnSlime() < 1500) {
				maxDist += 0.2;
			}

			if (pd.velocTicks > 0) {
				maxDist += (double) pd.velocTicks / 60;
			}

			if (pd.iceTicks > 0) {
				maxDist += (double) pd.iceTicks / 60;

			}

			if (pd.blockAboveTicks > 0) {
				maxDist += (double) pd.blockAboveTicks / 30;
			}

			maxDist += ReflectionUtils.getPingModifier(p) * 0.18;

			for (final PotionEffect pe : p.getActivePotionEffects()) {
				if (pe.getType().equals(PotionEffectType.SPEED)) {
					maxDist += (pe.getAmplifier() * 0.2);
				}
			}

			if (p.getWalkSpeed() > 0.2f) {
				maxDist *= (p.getWalkSpeed() / 0.15);
			}

			maxDist += Math.abs(pd.getVelocity()) * 0.18;
			maxDist += (Math.abs(20 - Main.getInstance().tpsTask)) * 0.16;

			if (pd.onGroundTicks > 0) {
				if (locUtils.isOnSolidGround(pd.getNextLocation())) {
					if (pd.onGroundTicks >= 3) {
						maxDist += 0.24;
					} else {
						if (pd.onGroundTicks == 2) {
							maxDist += 0.26;
						}
					}

					if (pd.getDeltaXZ() > maxDist) {
						flag(pd, pd.getDeltaXZ() + " > " + maxDist + " (limit)");
					}
				}
			}

			return;
		}
	}

}
