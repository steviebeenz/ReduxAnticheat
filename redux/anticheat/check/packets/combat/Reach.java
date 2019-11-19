package redux.anticheat.check.packets.combat;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.check.events.UpdatePackets;
import redux.anticheat.player.PlayerData;

public class Reach extends PacketCheck {

	public Reach() {
		super("Reach", 5, 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 80);
		settings.put("max_reach", 3.0);
		this.setDescription("Checks if a player hits further than normal.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (isEnabled()) {
			final PacketContainer packet = e.getPacket();
			if (packet.getEntityUseActions().read(0).equals(EntityUseAction.ATTACK)) {
				final Player p = e.getPlayer();
				if (!UpdatePackets.shouldCheck(p.getUniqueId())) {
					return;
				}
				final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

				final Entity entity = getEntityFromPacket(packet, p);

				if (entity == null || !(entity instanceof LivingEntity) || pd.getLastLocation() == null) {
					return;
				}

				final double dist = Main.getInstance().getLocUtils().getHorizontalDistance(pd.getNextLocation(), entity.getLocation());
				double maxReach = (double) settings.get("max_reach");

				if(p.getGameMode().equals(GameMode.CREATIVE)) {
					maxReach += 2.5 + 0.4;
				}
				
				maxReach += getVeloc(entity);
				maxReach += getVeloc(p);
				maxReach += pd.getDeltaXZ();

				if (pd.offGroundTicks > 0) {
					maxReach += Math.abs(pd.getNextLocation().getY() - entity.getLocation().getY());
					if (pd.offGroundTicks < 10) {
						maxReach += pd.offGroundTicks * 0.01;
					}
				}

				if (pd.offGroundTicks > 0 && Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation())) {
					maxReach += 0.2;
				}

				if (!Main.getInstance().getLocUtils().isOnSolidGround(entity.getLocation())) {
					maxReach += 0.3;
					maxReach += Math.abs(pd.getNextLocation().getY() - entity.getLocation().getY());
				}

				if (entity instanceof Player) {
					PlayerData pd2 = Main.getInstance().getPlayerManager().getPlayer(entity.getUniqueId());
					if (pd2 != null) {
						maxReach += pd2.getDeltaXZ() * 0.5;
					}
				}

				maxReach += Math.abs(pd.getDeltaXZ() - pd.getLastDeltaXZ());
				maxReach += (Math.abs(pd.getNextLocation().getX() - entity.getLocation().getX())
						+ Math.abs(pd.getNextLocation().getZ() - entity.getLocation().getZ())) / 10;
				if (dist > maxReach) {
					flag(pd, dist + " > " + maxReach + " (reach)");
				}

				return;
			}
		}
	}

	private double getVeloc(Entity entity) {
		double d = 0;
		d += Math.abs(entity.getVelocity().getX());
		d += Math.abs(entity.getVelocity().getZ());
		d += Math.abs(entity.getVelocity().getY());
		return d;
	}

}
