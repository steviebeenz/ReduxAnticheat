package redux.anticheat.tasks;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import redux.anticheat.Main;

public class PacketsClear extends BukkitRunnable {

	@Override
	public void run() {
		if (!Main.getInstance().getPlayerManager().getPlayers().isEmpty()) {
			for (final UUID id : Main.getInstance().getPlayerManager().getPlayers().keySet()) {
				Main.getInstance().getPlayerManager().getPlayer(id).resetAllCounters();
			}
		}
	}

}
