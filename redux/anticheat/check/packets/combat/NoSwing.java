package redux.anticheat.check.packets.combat;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class NoSwing extends PacketCheck {

	public NoSwing() {
		super("NoSwing [GCD]", 5, 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 60);
		setDescription("Checks using GCD for NoSwing.");
	}

	@Override
	public void listen(PacketEvent e) {
		if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {

			final Player p = e.getPlayer();
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

			final int diff = pd.getAttackEntityPackets() - pd.getSwingPackets();

			final Long gcd = Main.getInstance().getLocUtils().getGcd(diff, pd.getSwingPackets());

			if (gcd > 0) {
				pd.gcdFlags++;
				if (pd.gcdFlags > 1) {
					flag(pd, gcd + " > 0");
				}
			}

			if (pd.gcdFlags > 0) {
				pd.gcdFlags--;
			}

		}

	}

}
