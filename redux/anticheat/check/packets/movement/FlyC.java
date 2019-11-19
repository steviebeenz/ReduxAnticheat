package redux.anticheat.check.packets.movement;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class FlyC extends PacketCheck {

	public FlyC() {
		super("Fly [C]", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
		this.setDescription("Checks if a player's motion is in a pattern.");
		settings.put("limit", 8);
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if (!Main.getInstance().getLocUtils().isOnGround(p) && Main.getInstance().getLocUtils().isCollided(p.getLocation(), Material.AIR)) {

			if (p.isFlying()) {
				pd.flyCvl = 0;
				return;
			}
			
			Block b = Main.getInstance().getLocUtils().getBlockUnder(pd.getLastLocation()), b2 = Main.getInstance().getLocUtils().getBlockUnder(pd.getNextLocation());
			
			if(b != null) {
				if(Main.getInstance().getLocUtils().isStair(b.getType())) {
					pd.flyCvl = 0;
					return;
				}
			}
			
			if(b2 != null) {
				if(Main.getInstance().getLocUtils().isStair(b2.getType())) {
					pd.flyCvl = 0;
					return;
				}
			}
			
			int limit = (int) settings.get("limit");

			final double diff = (pd.getDeltaY() - pd.getPreviousDeltaY());
			if (diff != 0) {
				if (pd.lastFlyC == -diff && pd.offGroundTicks == 0) {
					if (++pd.flyCvl >= limit) {
						flag(pd, pd.flyCvl + " >= " + limit + "(limit)");
						pd.flyCvl = 0;
					}
				} else {
					pd.flyCvl = 0;
				}
			}

			pd.lastFlyC = diff;
		}
	}

}
