package redux.anticheat.check.packets.combat;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class Criticals extends PacketCheck {

	public Criticals() {
		super("Criticals", 5, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 65);
		setDescription("Checks if a player is getting criticals hits impossibly.");
	}

	@Override
	public void listen(PacketEvent pe) {
		Player p = pe.getPlayer();
		// Entity e = this.getEntityFromPacket(pe.getPacket(), p);
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (pd.getNextLocation() != null && pd.getLastLocation() != null) {
			if (pd.getNextLocation().getY() - pd.getLastLocation().getY() % 2 == 0) {
				if (!locUtils.isInLiquid(pd.getNextLocation())
						&& !locUtils.isCollidedWeb(pd.getLastLocation(), pd.getNextLocation())) {
					flag(pd, "Bruh");
				}
			}
		}
	}

}
