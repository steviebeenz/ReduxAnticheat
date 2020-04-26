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

public class FlyC extends PacketCheck {

	public FlyC() {
		super("Fly [C]", 5, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION },
				true, 85);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if (pd.flyTicks > 0 || pd.vehicleTicks > 0 || pd.teleportTicks > 0 || p.isFlying() || pd.changeGamemodeTicks > 0
				|| System.currentTimeMillis() - pd.getLastOnSlime() < 2000 || pd.ticksOnClimbable > 0) {
			return;
		}

		// kind of jumping
		double diff = pd.getDeltaXZ() - pd.getLastDeltaXZ();
		double max = 0.49;

		max *= 1 + (ReflectionUtils.getPingModifier(p) * 0.1);
		max += Math.abs(pd.getVelocity() * 0.16);

		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.JUMP)
					|| pe.getType().getName().toLowerCase().contains("LEVITATION")) {
				max *= 1 + (pe.getAmplifier() * 0.2);
			}
		}

		if (diff > max) {
			if (pd.flyCvl++ > 2) {
				flag(pd, diff + " > " + max);
				pd.flyCvl = 0;
			}
		} else {
			pd.flyCvl = Math.max(0, pd.flyCvl--);
		}
	}

}
