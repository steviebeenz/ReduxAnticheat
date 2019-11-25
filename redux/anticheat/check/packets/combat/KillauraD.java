package redux.anticheat.check.packets.combat;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class KillauraD extends PacketCheck {

	public KillauraD() {
		super("Killaura [D]", 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 80);
		setDescription("Checks if a player reaches too far.");
	}

	@Override
	public void listen(PacketEvent e) {
		final Player p = e.getPlayer();
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {

			final Entity ent = getEntityFromPacket(e.getPacket(), p);

			if (ent == null || !(ent instanceof LivingEntity) || pd.getLastLocation() == null) {
				return;
			}
			PlayerData player2 = null;

			if (ent instanceof Player) {
				player2 = Main.getInstance().getPlayerManager().getPlayer(ent.getUniqueId());
			}

			final double distance = pd.getNextLocation().distanceSquared(ent.getLocation());

			final long gcd = Main.getInstance().getLocUtils().getGcd((long) distance, (long) distance);

			if (distance < 1) {
				return;
			}

			if ((gcd == 0 ? true : false)) {
				pd.nonDivisible++;
			} else {
				if (pd.nonDivisible > 0) {
					pd.nonDivisible--;
				}
			}

			if (pd.nonDivisible > 1) {
				flag(pd, "not divisible > 1");
			}

			double p2delta = 0;

			if (player2 != null) {
				p2delta = player2.getDeltaXZ();
			}

			double limit = 18;

			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				limit += 10;
			}

			if (gcd > (limit + (pd.getDeltaXZ() * 0.6) + p2delta)) {
				pd.moreGCD++;
				if (pd.moreGCD > 2) {
					flag(pd, gcd + " > " + (limit + (pd.getDeltaXZ() * 0.6) + p2delta));
				}
			} else {
				pd.moreGCD--;
			}
		}

	}

}
