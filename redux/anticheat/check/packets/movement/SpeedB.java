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

public class SpeedB extends PacketCheck {

	public SpeedB() {
		super("Speed [B]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		this.setDescription("Checks if a player speed is more than predicted.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.teleportTicks > 0 || pd.vehicleTicks > 0 || pd.flyTicks > 0 || p.isFlying()) {
			return;
		}

		double speed = pd.getDeltaXZ();

		double predicted = (pd.getLastDeltaXZ()) * 0.91;

		boolean locGround = Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation()),
				locGroundLast = Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation());

		if(p.isDead()) {
			return;
		}
		
		if (!locGround && !locGroundLast && pd.offGroundTicks >= 2) {
			if (Math.abs(predicted) > 0.005D) {
				if (!near(speed, predicted)) {
					double diff = (speed - predicted);
					double limit = 0.3714328744445773;
					
					for(PotionEffect pe : p.getActivePotionEffects()) {
						if(pe.getType().equals(PotionEffectType.SPEED)) {
							limit *= 1 + (pe.getAmplifier() * 0.2f);
						}
					}
					
					if(pd.velocTicks > 10) {
						return;
					}
					
					limit += (Math.abs(pd.getVelocity()) * 0.12);
					limit += (ReflectionUtils.getPingModifier(p) * 0.05);
					limit += ((Math.abs(20 - Main.getInstance().getTpsTask().tps)) * 0.08);
					
					if (diff > limit) {
						flag(pd, diff + " > " + limit);
					}
				}
			}
		}

	}

	private boolean near(double deltaY, double predicted) {
		return Math.abs(deltaY - predicted) < 0.001;
	}

}
