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

public class AimB extends PacketCheck {

	public AimB() {
		super("Aim [B]", 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.POSITION_LOOK }, false,
				75);
		setDescription("Checks if a player sends more rotations than normal.");

	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if (e.getPacketType().equals(getType()[0])) {
			if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {

				final Entity en = entityBackup(e.getPacket(), p);
				if (en == null) {
					return;
				}

				if (pd.lastSentHit == 0L) {
					pd.lastSentHit = System.currentTimeMillis();
					pd.lastSentCount = 0;
					return;
				}

				final double playerCenterHitbox = p.getLocation().getY() + (1.8 / 2),
						entityHitbox = en.getLocation().getY();

				final double playerLoc = Math.abs(p.getLocation().getX() + playerCenterHitbox + p.getLocation().getZ()),
						entityLoc = Math.abs(p.getLocation().getX() + entityHitbox + p.getLocation().getZ());

				final int limitPer50 = 9;

				final long diff = Math.abs(pd.lastSentLook - pd.lastSentHit);

				double limit = (diff / 50) * limitPer50;
				limit += Math.abs(playerLoc - entityLoc);
				limit += Math.abs(pd.getDeltaXZ()) + Math.abs(pd.getDeltaY());

				if (pd.lastSentCount == 10) {
					return;
				}

				pd.lastSentCount *= 0.56;

				if (diff >= 50) {
					if (pd.lastSentCount > 0) {
						if (pd.lastSentCount > limit) {
							flag(pd, diff + " >= 50");
						}
					}
				}

				pd.lastSentCount = 0;
			}
		} else if (e.getPacketType().equals(getType()[1])) {
			pd.lastSentLook = System.currentTimeMillis();
			pd.lastSentCount++;
		}
	}

}
