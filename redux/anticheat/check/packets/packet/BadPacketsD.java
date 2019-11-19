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

public class BadPacketsD extends PacketCheck {
	public BadPacketsD() {
		super("BadPackets [D]", 5, 10, null, false, true, Category.PACKETS,
				new PacketType[] { PacketType.Play.Client.FLYING }, true, 90);
		this.setDescription("Checks if a player is sending flying packets weirdly.");
	}

	@Override
	public void listen(PacketEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (System.currentTimeMillis() - pd.resetPacketsD >= 1000) {
			double rate = ((double)pd.flyingInPackets / 20);

			if (pd.getPlayer().isFlying() || pd.flyTicks > 0) {
				rate = 0;
				pd.flyingInVl = 0;
				return;
			}
			
			if(System.currentTimeMillis() - pd.join < 2000) {
				rate = 0;
				pd.flyingInVl = 0;
				return;
			}
			

			double limit = 5;
			
			for(PotionEffect pe : pd.getPlayer().getActivePotionEffects()) {
				if(pe.getType().equals(PotionEffectType.JUMP)) {
					double amplifier = (double) pe.getAmplifier();
					limit += amplifier * 1.2;
				}
			}
			
			if (pd.offGroundTicks >= 12) {
				if (pd.flyingInPackets >= limit) {
					if (Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), Material.AIR)
							&& Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), Material.AIR)
							&& !Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation()) && !Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())) {
						flag(pd, pd.flyingInPackets + " >= " + limit);
					}
				}
			}

			if (rate >= 2 && pd.flyingInPackets > 5) {
				flag(pd, rate + " >= " + 2);
			//	pd.getPlayer().sendMessage("rate > 2 and flyingpackets > 5");
			}

			if (rate >= 1.05) {
				pd.flyingInVl++;
			} else {
				if (pd.flyingInVl > 0) {
					pd.flyingInVl--;
				}
			}
			
			if(rate <= 0.1) {
				pd.lowFlyingInVl++;
				if(pd.wasFalling) {
					pd.lowFlyingInVl = 0;
				}
				if(pd.lowFlyingInVl > 5) {
					pd.lowFlyingInVl = 0;
					flag(pd, rate + " <= 0.1");
				}
			} else {
				if(pd.lowFlyingInVl > 0) {
					pd.lowFlyingInVl--;
				}
			}

			if (pd.flyingInVl >= 5) {
				flag(pd, pd.flyingInVl + " >= 5");
				//pd.getPlayer().sendMessage("flyingInVl 5 total.");
			}

			pd.flyingInPackets = 0;
			pd.resetPacketsD = System.currentTimeMillis();
		} else {
			pd.flyingInPackets++;
		}

	}

}
