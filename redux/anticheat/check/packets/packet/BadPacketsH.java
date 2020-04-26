package redux.anticheat.check.packets.packet;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class BadPacketsH extends PacketCheck {

	public BadPacketsH() {
		super("BadPackets [H]", 5, null, false, true, Category.PACKETS, new PacketType[] { PacketType.Play.Client.ABILITIES }, true, 60);
		this.setDescription("Checks if a player is sending abilities packets wrongly.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if(p.getGameMode().equals(GameMode.SURVIVAL) && !p.getAllowFlight()) {
			if(pd.changeGamemodeTicks < 1 && pd.flyTicks < 1) {
				pd.abilitiesFlag++;
				if(pd.abilitiesFlag > 1) {
					flag(pd, "ap - g: s, f: f");
					pd.abilitiesFlag = 0;
				}
			}
		}
	}
	
	

}
