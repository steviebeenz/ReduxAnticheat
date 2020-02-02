package redux.anticheat.check;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.learning.datasets.DataSet;
import redux.anticheat.player.AlertSeverity;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.HoverText;
import redux.anticheat.utils.LocUtils;
import redux.anticheat.utils.ReflectionUtils;

public abstract class PacketCheck {

	private String name;
	private final List<String> punishments;
	private final HashMap<PlayerData, Integer> violations;
	private List<DataSet> dataStore = null;
	private int maxViolations;
	private Category category;
	private final boolean canLearn;
	private boolean enabled, setback;
	private double severity;
	private PacketType[] types;
	private String description;
	public LocUtils locUtils;

	public HashMap<String, Object> settings = new HashMap<String, Object>();

	public PacketCheck(String name, int maxViolations, List<String> punishments, boolean canLearn,
			boolean enabled, Category category, PacketType[] type, boolean setback, double severity) {
		this.name = name;
		this.punishments = punishments;
		this.maxViolations = maxViolations;
		this.canLearn = canLearn;
		this.enabled = enabled;
		this.category = category;
		types = type;
		this.setback = setback;
		this.severity = severity;
		violations = new HashMap<PlayerData, Integer>();
		dataStore = new ArrayList<DataSet>();
		locUtils = Main.getInstance().getLocUtils();
	}

	public abstract void listen(PacketEvent e);

	public String getName() {
		return name;
	}

	public List<String> getPunishments() {
		return punishments;
	}

	public int getMaxViolations() {
		return maxViolations;
	}

	public void setCategory(Category c) {
		category = c;
	}

	public Category getCategory() {
		return category;
	}

	public boolean canLearn() {
		return canLearn;
	}

	public void setName(String s) {
		name = s;
	}

	public void setEnabled(boolean b) {
		enabled = b;
		if (!enabled && canLearn) {
			saveData();
		}
		return;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void saveData() {
		return;
	}
	
	public Entity getEntityFromPacket(PacketContainer pc, Player p) {
		try {
			return pc.getEntityModifier(p.getWorld()).readSafely(0);
		} catch(Exception e) {
			return entityBackup(pc, p);
		}
	}

	public Entity entityBackup(PacketContainer pc, Player p) {
		if (pc.getType().equals(PacketType.Play.Client.USE_ENTITY)) {
			final int id = pc.getIntegers().readSafely(0);
			for (final Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 20, 20, 20)) {
				if (e.getEntityId() == id) {
					return e;
				}
			}
		}
		return null;
	}

	public void flag(PlayerData pd, String info) {

		if (pd.wasSetBack && this.shouldSetback()) {
			pd.wasSetBack(false);
		}

		pd.setViolations((int) (pd.getViolations() + (1 * ((severity / 5) / 5))));
		violations.put(pd, violations.getOrDefault(pd, 0) + 1);

		if(pd.getViolations() >= 50) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				pd.setViolations(0);
				violations.remove(pd);
				pd.isbeingpunished = false;
				pd.getPlayer().kickPlayer(Main.getInstance().removalMessage);
			});
		}
		
		if (shouldSetback()) {
			pd.setDown();
		}
		if (!pd.isbeingpunished) {
			for (final PlayerData st : Main.getInstance().getPlayerManager().getPlayers().values()) {
				if (st.isAlerts() && (System.currentTimeMillis() - st.getLastAlert()) > st.getDelay()) {
					final String sev = workoutSeverity(severity, pd);
					final HoverText ht = new HoverText();
					ht.addText(Main.getInstance().msgPrefix);
					ht.addText("§7" + pd.getPlayer().getName())
							.addHoverText("§7TPS: §d" + Main.getInstance().getTpsTask().roundTPS() + " §7Ping: §d"
									+ ReflectionUtils.getPing(pd.getPlayer()) + "ms §7Total Violations: §d"
									+ pd.violations);
					ht.addText(" §7failed ").addHoverText("§7" + info);
					ht.addText("§7" + getName()).addHoverText("§7" + getDescription());
					ht.addText("§7 | Sev: " + sev + " §7| Vio: " + violations.get(pd) + ".");

					if (st.severity == AlertSeverity.LOW) {
						ht.sendToPlayer(st.getPlayer());
					} else if (st.severity == AlertSeverity.MEDIUM) {
						if (sev.contains("§6") || sev.contains("§c") || sev.contains("§d")) {
							ht.sendToPlayer(st.getPlayer());
						}
					} else if (st.severity == AlertSeverity.HIGH) {
						if (sev.contains("§c") || sev.contains("§d")) {
							ht.sendToPlayer(st.getPlayer());
						}
					}

					st.setLastAlert(System.currentTimeMillis());
				}
			}

			if (Main.getInstance().logConsole) {
				Bukkit.getConsoleSender()
						.sendMessage(Main.getInstance().msgPrefix + "§7" + pd.getPlayer().getName() + " failed "
								+ getName() + " | Sev: " + workoutSeverity(severity, pd) + " §7| Vio: "
								+ pd.getViolations() + ".");
			}
		} else {
			return;
		}

		if (workoutSeverity(severity, pd).contains("§d")) {
			final Player p = pd.getPlayer();
			pd.isbeingpunished = true;
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				pd.setViolations(0);
				violations.remove(pd);
				pd.isbeingpunished = false;
				p.kickPlayer(Main.getInstance().removalMessage);
			});
		}
	}

	public String workoutSeverity(double i, PlayerData pd) {
		i = (i / maxViolations);
		i = ((i) * (1.1 + (violations.getOrDefault(pd, 0))));

		i = (round(i, 2));

		if (i < 50) {
			return "§7" + i;
		} else if (i >= 50 && i < 75) {
			return "§6" + i;
		} else if (i >= 75 && i < 100) {
			return "§c" + i;
		}
		return "§d" + i;
	}

	public PacketType[] getType() {
		return types;
	}

	public void setType(PacketType[] type) {
		types = type;
	}

	public List<DataSet> getDataStore() {
		return dataStore;
	}

	private static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_EVEN);
		return bd.doubleValue();
	}

	public boolean shouldSetback() {
		return this.setback;
	}

	public void setSetback(boolean setback) {
		this.setback = setback;
	}

	public double getSeverity() {
		return severity;
	}

	public void setSeverity(double severity) {
		this.severity = severity;
	}

	public void setMaxViolations(int vl) {
		maxViolations = vl;
	}

	public void loadCustomSettings() {
		try {
			final File dir = new File(Main.getInstance().getDataFolder() + File.separator + "checks" + File.separator
					+ getCategory().name().toLowerCase(), getName().toLowerCase() + ".yml");
			final YamlConfiguration yml = YamlConfiguration.loadConfiguration(dir);

			for (final String s : settings.keySet()) {
				if (!yml.isSet(s)) {
					yml.set(s, settings.get(s));
				} else {
					settings.put(s, yml.get(s));
				}
			}

			yml.save(dir);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Object> getSettings() {
		return settings;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<PlayerData, Integer> getViolations() {
		return violations;
	}

}
