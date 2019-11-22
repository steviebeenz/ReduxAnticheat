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
import redux.anticheat.utils.ReflectionUtils;

public class KillauraC extends PacketCheck {

	public KillauraC() {
		super("Killaura [C]", 5, 20, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.POSITION }, false, 30);
		setDescription("Checks if a player is hitting a dead entity.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (e.getPacketType().equals(getType()[0])) {
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
			if (e.getPacket().getEntityUseActions().read(0).equals(EntityUseAction.ATTACK)) {
				final Entity ent = getEntityFromPacket(e.getPacket(), pd.getPlayer());
				if (ent == null || !(ent instanceof LivingEntity)) {
					return;
				}

				if (ent.isDead() || ((LivingEntity) ent).getHealth() == 0) {
					pd.deathHits++;
					if (pd.deathHits >= (5 + ReflectionUtils.getPingModifier(e.getPlayer()))) {
						flag(pd, pd.deathHits + " >= " + (5 + ReflectionUtils.getPingModifier(e.getPlayer())));
						pd.deathHits = 0;
					}
				} else {
					if (pd.getPlayer().isDead()) {
						pd.deathHits++;
						if (pd.deathHits >= (5 + ReflectionUtils.getPingModifier(e.getPlayer()))) {
							flag(pd, pd.deathHits + " >= " + (5 + ReflectionUtils.getPingModifier(e.getPlayer())));
							pd.deathHits = 0;
						}
					} else {
						pd.deathHits = 0;
					}
				}
			}
		}
	}

}
