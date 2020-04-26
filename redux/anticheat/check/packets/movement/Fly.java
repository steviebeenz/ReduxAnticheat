package redux.anticheat.check.packets.movement;

import java.util.ArrayList;

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

public class Fly extends PacketCheck {

	public Fly() {
		super("Fly", 5, null, false, true, Category.MOVEMENT, new PacketType[] { PacketType.Play.Client.POSITION },
				true, 80);
	}

	public ArrayList<Double> samples = new ArrayList<Double>();

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (p.isFlying() || pd.flyTicks > 0
				|| System.currentTimeMillis() - pd.getLastOnSlime() < 1000 || pd.teleportTicks > 0 || pd.changeGamemodeTicks > 0 || pd.velocTicks > 0) {
			return;
		}

		for (final PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.JUMP) || pe.getType().getName().toLowerCase().contains("levetation")) {
				return;
			}
		}


		if (!locUtils.isOnSolidGround(pd.getNextLocation()) && !locUtils.isOnSolidGround(pd.getLastLocation())) {
			if (pd.getDeltaY() >= -0.078 && !locUtils.isCollidedWeb(pd.getLastLocation(), pd.getNextLocation())) {
				if (pd.offGroundTicks > 4) {
					if (locUtils.getBlockUnder(pd.getNextLocation()).getType().equals(Material.AIR) && !locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation())) {
						pd.fly++;
						if (pd.fly > 3) {
							flag(pd, "ofg: " + pd.offGroundTicks + ", y: " + pd.getDeltaY() + " > -0.078");
						}
						return;
					}
				} 
			} 
		} 
		
		if(pd.fly > 0) {
			pd.fly--;
		}

	}

}
