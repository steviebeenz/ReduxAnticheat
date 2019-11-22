package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class FlyD extends PacketCheck {

	public FlyD() {
		super("Fly [D]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.FLYING }, true, 85);
		setDescription("Checks if a player is sending flying packets in air.");
		settings.put("limit", 40);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (p.isFlying() || pd.flyTicks > 0) {
			pd.flyDvl = 0;
			return;
		}

		if (!ReflectionUtils.getOnGround(p) && !Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())
				&& !Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation())) {
			if (Main.getInstance().getLocUtils().canClimb(pd.getLastLocation())
					|| Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())
					|| Main.getInstance().getLocUtils().isInLiquid(pd.getLastLocation())
					|| Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation())
					|| Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), "SLIME")
					|| Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "SLIME")) {
				pd.flyDvl = 0;
				return;
			}

			final double diffY = pd.getNextLocation().getY() - pd.getLastLocation().getY();
			if (diffY >= -0.01125D) {
				pd.flyDvl++;
				if (pd.flyDvl > (int) settings.get("limit")) {
					flag(pd, pd.flyDvl + " > " + (int) settings.get("limit") + "(limit)");
					pd.flyDvl = 0;
				}
			} else {
				if (pd.flyDvl > 0) {
					pd.flyDvl--;
				}
			}
		}
	}

}
