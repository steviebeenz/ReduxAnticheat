package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class GlideB extends PacketCheck {

	public GlideB() {
		super("Glide [B]", 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 100);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (locUtils.isCollidedWithWeirdBlock(pd.getLastLocation(), pd.getNextLocation()) || pd.flyTicks > 0
				|| pd.teleportTicks > 0 || pd.changeGamemodeTicks > 0 || pd.stairTicks > 0 || pd.jumpStairsTick > 0) {
			return;
		}

	}

}
