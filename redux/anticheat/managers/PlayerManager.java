package redux.anticheat.managers;

import java.io.File;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import redux.anticheat.Main;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.AlertSeverity;
import redux.anticheat.player.PlayerData;

public class PlayerManager {

	private final WeakHashMap<UUID, PlayerData> players;

	public PlayerManager() {
		players = new WeakHashMap<>();
		for (final Player p : Bukkit.getOnlinePlayers()) {
			addPlayer(p);
		}
	}

	public PlayerData getPlayer(UUID id) {
		for (final UUID i : players.keySet()) {
			if (i.equals(id)) {
				return players.get(i);
			}
		}
		return null;
	}

	public void addPlayer(Player p) {
		final PlayerData pd = new PlayerData(p);
		final File dir = new File(Main.getInstance().getDataFolder() + File.separator + "players");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		final File player = new File(dir, p.getUniqueId().toString() + ".yml");
		if (!player.exists()) {
			try {
				player.createNewFile();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			final YamlConfiguration load = YamlConfiguration.loadConfiguration(player);
			if (load.isSet("violations") && load.isSet("left")) {

				int violations = load.getInt("violations");
				if (System.currentTimeMillis() - load.getLong("left") >= Main.getInstance().vlDecay) {
					long diff = System.currentTimeMillis() - load.getLong("left");
					int toRemove = Math.round((diff / Main.getInstance().vlDecay));
					violations -= toRemove;
					if(violations < 0) {
						violations = 0;
					}
				}

				pd.violations = violations;
				pd.setAlerts(load.getBoolean("alerts"));
				pd.severity = AlertSeverity.valueOf((String) load.get("severity"));
				pd.setDelay(load.getLong("delay"));
			} else {
				player.delete();
				try {
					player.createNewFile();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		players.put(p.getUniqueId(), pd);
	}

	public void removePlayer(Player p) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if (pd != null) {
			pd.delete();

			final File dir = new File(Main.getInstance().getDataFolder() + File.separator + "players");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			final File player = new File(dir, p.getUniqueId().toString() + ".yml");
			if (!player.exists()) {
				try {
					player.createNewFile();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			final YamlConfiguration plda = YamlConfiguration.loadConfiguration(player);
			plda.set("violations", pd.getViolations());
			plda.set("alerts", pd.isAlerts());
			plda.set("delay", pd.getDelay());
			plda.set("left", System.currentTimeMillis());
			if (pd.severity != null) {
				plda.set("severity", pd.severity.name());
			} else {
				plda.set("severity", AlertSeverity.LOW.name());
			}

			try {
				plda.save(player);
			} catch (final Exception e) {
				e.printStackTrace();
			}

			for (final PacketCheck c : Main.getInstance().getCheckManager().getChecks()) {
				if (c.getViolations().containsKey(pd)) {
					c.getViolations().remove(pd);
				}
			}

			players.remove(p.getUniqueId());
		}
	}

	public WeakHashMap<UUID, PlayerData> getPlayers() {
		return players;
	}

}
