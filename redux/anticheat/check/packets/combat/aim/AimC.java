package redux.anticheat.check.packets.combat.aim;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class AimC extends PacketCheck {

	public AimC() {
		super("Aim [C]", 5, 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 90);
		this.setDescription("Checks if a player is not hitting a hitbox.");
	}

	@Override
	public void listen(PacketEvent e) {

		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {
			Entity entity = getEntityFromPacket(e.getPacket(), p);

			if (entity != null && pd.getNextLocation() != null) {

				if(pd.getLastLocation().distanceSquared(entity.getLocation()) < 1) {
					p.sendMessage("more than 1");
					return;
				}
				
				double diff = Main.getInstance().getLocUtils().getDistanceFromMouse(pd, entity);

				if(diff > 180) {
					diff -= 180;
				}
				
			//	Bukkit.broadcastMessage("diff: " + diff);
				
				if(diff > 100) {
					flag(pd, diff + " > " + 100);
				}
			}

		}

	}

}
