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

public class FlyB extends PacketCheck {

	public FlyB() {
		super("Fly [B]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		settings.put("ping_factor", 60);
		this.setDescription("Checks if a player is moving more than normal.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (p.isFlying() || pd.flyTicks > 0) {
			pd.flyB = 0;
			pd.offGroundTicks = 0;
			return;
		}

		if (pd.getDeltaY() == 0 && pd.getPreviousDeltaY() == 0) {
			return;
		}

		if (System.currentTimeMillis() - pd.getLastOnSlime() < 1000) {
			pd.setPreviousDeltaY(0);
			pd.setDeltaY(0);
			return;
		}
		
		if(pd.teleportTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().isUnderStairs(pd.getLastLocation()) || Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), "SKULL") || Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "SKULL")) {
			return;
		}

		if (pd.getPlayer().getVelocity().getY() > 1 || pd.getPlayer().getNoDamageTicks() > 0) {
			pd.velocTicks = 20;
		} else {
			if (pd.velocTicks > 0) {
				pd.velocTicks--;
			}
		}

		final Long gcdY = Main.getInstance().getLocUtils().getGcd((long) pd.getDeltaY(), (long) pd.getPreviousDeltaY());

		double times = 0;
		for(PotionEffect pe : p.getActivePotionEffects()) {
			if(pe.getType().equals(PotionEffectType.JUMP)) {
				times = 1 + (pe.getAmplifier() * 0.4);
			}
		}
		
		double highLimit = (0 + (pd.offGroundTicks / 150) + (pd.velocTicks / 15)) + times;
		double lowLimit = (((-pd.offGroundTicks + 1) / 4) - 5) + -(pd.velocTicks / 15) / 15;
		
		if(ReflectionUtils.getPing(p) > (int) settings.get("ping_factor")) {
			double max = (double) ((int)settings.get("ping_factor"));
			double now = (ReflectionUtils.getPing(p) / max) * 0.08;
			
			highLimit += now;
			lowLimit -= now;
		}
		
		if (gcdY > highLimit
				|| gcdY < lowLimit) {
			pd.flyB++;
		}
		
		if (pd.flyB > 0) {
			flag(pd, pd.flyB + " > 0");
			pd.flyB = 0;
		}

		if (pd.flyB > 0) {
			pd.flyB--;
		}
	}

}
