package redux.anticheat.check.packets.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
public class KillauraB extends PacketCheck {

	public KillauraB() {
		super("Killaura [B]", 5, 15, null, true, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 80);
		this.setDescription("Checks if a player doesn't swing when hitting.");
	}

	@Override
	public void listen(PacketEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

		final Entity ent = getEntityFromPacket(e.getPacket(), e.getPlayer());
		if (ent == null || !(ent instanceof LivingEntity)) {
			return;
		}

		if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {
			if (pd.getAttackEntityPackets() > pd.getSwingPackets()) {
				if (pd.getAttackEntityPackets() - pd.getSwingPackets() > 2) {
					flag(pd, pd.getAttackEntityPackets() - pd.getSwingPackets() + " > " + 2);
				}
			} else {
				if (pd.getSwingPackets() == 0 && pd.getAttackEntityPackets() >= 3) {
					flag(pd, pd.getSwingPackets() + " = " +  pd.getAttackEntityPackets() + "> 3");
				}
			}
		}
	}

}
