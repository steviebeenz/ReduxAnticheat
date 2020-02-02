package redux.anticheat.check.packets.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class MultiAura extends PacketCheck {

	public MultiAura() {
		super("Killaura [Multi]", 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 85);
	}

	@Override
	public void listen(PacketEvent e) {
		if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {
			Player p = e.getPlayer();
			PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
			Entity ent = getEntityFromPacket(e.getPacket(), p);
			
			if(pd.lastEntity != null) {
				double time = 27;
				double dist = locUtils.getHorizontalDistance(pd.getNextLocation(), ent.getLocation());
				
				time *= 1 + (dist * 0.78);
				
				
				if(ent != pd.lastEntity && ent.getUniqueId() != pd.lastEntity.getUniqueId() && System.currentTimeMillis() - pd.lastEntityTime <= time) {
					flag(pd, "Hit two entities faster than actually humanly possible.");
				}
			}
			
			pd.lastEntity = ent;
			pd.lastEntityTime = System.currentTimeMillis();
		}

	}

}
