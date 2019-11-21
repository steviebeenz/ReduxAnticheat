package redux.anticheat.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import redux.anticheat.Main;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.AlertSeverity;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class ReduxCommand extends BukkitCommand {

	public ArrayList<UUID> debugEnabled;

	public ReduxCommand() {
		super("redux");
		setDescription("Main command for rdx ac.");
		setPermission("redux.commands");
		final List<String> aliases = new ArrayList<String>();
		aliases.add("rdx");
		setAliases(aliases);
		debugEnabled = new ArrayList<UUID>();
	}

	@Override
	public boolean execute(CommandSender arg0, String command, String[] args) {
		if (arg0 instanceof Player) {
			final Player p = ((Player) arg0);
			final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer((p).getUniqueId());
			if (command.equalsIgnoreCase("redux") || command.equalsIgnoreCase("rdx")) {
				if (args.length == 0) {
					arg0.sendMessage(Main.getInstance().msgPrefix + "Redux Anticheat v" + Main.getInstance().getVersion() + " §8[1/2]");
					arg0.sendMessage("§d/redux alerts" + "\n" + "§7Manage alert settings.");
					arg0.sendMessage("");
					arg0.sendMessage("§d/redux debug" + "\n" +  "§7Toggles debug messages.");
					arg0.sendMessage("");
					arg0.sendMessage("§d/redux menu" + "\n" + "§7Opens a menu.");
					arg0.sendMessage("");
					arg0.sendMessage("§d/redux ping <player>"  + "\n" + "§7Shows a players ping.");
					arg0.sendMessage("");
					arg0.sendMessage("§d/redux vl§7/§dvio§7/§dviolations <player> (clear)" + "\n" + "§7View or clear a player's violation");
					return true;
				}
				if (args.length == 1) {
					if(args[0].equalsIgnoreCase("menu")) {
						Main.getInstance().getReduxMenu().open(p);
						return true;
					}
					
					if(args[0].equalsIgnoreCase("vio") || args[0].equalsIgnoreCase("violations") || args[0].equalsIgnoreCase("vl")) {
						arg0.sendMessage(Main.getInstance().msgPrefix + "You need to enter a player.");
						return true;
					}
					if (args[0].equalsIgnoreCase("alerts")) {
						arg0.sendMessage(Main.getInstance().msgPrefix + "Redux Anticheat §8[1/1]");
						arg0.sendMessage("§d/redux alerts console" + "\n" + "§7Toggles console logging of flags or changes.");
						arg0.sendMessage("");
						arg0.sendMessage("§d/redux alerts delay <delay in ms> "  + "\n" +  "§7Sets your delay of alerts.");
						arg0.sendMessage("");
						arg0.sendMessage("§d/redux alerts severity <Low, Medium, High>"  + "\n" + "§7Filters alerts by severity.");
						arg0.sendMessage("");
						arg0.sendMessage("§d/redux alerts toggle"  + "\n" +  "§7Toggle alerts.");
						return true;
					}
					if (args[0].equalsIgnoreCase("debug")) {
						if (debugEnabled.contains((p).getUniqueId())) {
							debugEnabled.remove((p).getUniqueId());
						} else {
							debugEnabled.add((p).getUniqueId());
						}
						arg0.sendMessage(Main.getInstance().msgPrefix + "You "
								+ (debugEnabled.contains((p).getUniqueId()) ? "enabled debug." : "disabled debug."));
						return true;
					}
					if (args[0].equalsIgnoreCase("ping")) {
						arg0.sendMessage(Main.getInstance().msgPrefix + "You need to enter a player.");
						return true;
					}
				}
				if (args.length == 2) {
					if(args[0].equalsIgnoreCase("vio") || args[0].equalsIgnoreCase("violations") || args[0].equalsIgnoreCase("vl")) {
						String player = args[1];
						Player pl = Bukkit.getPlayer(player);
						if(pl != null) {
							PlayerData plPd = Main.getInstance().getPlayerManager().getPlayer(pl.getUniqueId());
							arg0.sendMessage(Main.getInstance().msgPrefix + pl.getName() + " total violations: §d" + plPd.getViolations());
							return true;
						} else {
							arg0.sendMessage(Main.getInstance().msgPrefix + "Could not find player.");
							return true;
						}
					}
					
					if (args[0].equalsIgnoreCase("alerts")) {
						if (args[1].equalsIgnoreCase("console")) {
							if (Main.getInstance().logConsole) {
								Main.getInstance().logConsole = false;
							} else {
								Main.getInstance().logConsole = true;
							}
							arg0.sendMessage(Main.getInstance().msgPrefix + "Console logging was "
									+ (Main.getInstance().logConsole ? "enabled." : "disabled."));
							return true;
						}
						if (args[1].equalsIgnoreCase("toggle")) {
							if (pd.isAlerts()) {
								pd.setAlerts(false);
								;
							} else {
								pd.setAlerts(true);
							}
							arg0.sendMessage(Main.getInstance().msgPrefix + "You "
									+ (pd.isAlerts() ? "enabled alerts." : "disabled alerts."));
							return true;
						}
						if (args[1].equalsIgnoreCase("severity")) {
							arg0.sendMessage(Main.getInstance().msgPrefix
									+ "You need to enter a severity level, your current severity alert level is: "
									+ pd.severity.name());
							return true;
						} if(args[1].equalsIgnoreCase("delay")) {
							arg0.sendMessage(Main.getInstance().msgPrefix + "You are currently getting alerts every " + pd.getDelay() + "ms (" + pd.getDelay() / 1000 + "s)");
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("ping")) {
						final Player target = Bukkit.getPlayer(args[1]);
						if (target == null) {
							arg0.sendMessage(Main.getInstance().msgPrefix + "Invalid player entered.");
							return true;
						}
						
						arg0.sendMessage(
								"§d" + target.getName() + "'s ping §7(packet): " + ReflectionUtils.getPing(target));
						return true;
					}
				}
				if (args.length == 3) {
					if (args[0].equalsIgnoreCase("alerts")) {
						if (args[1].equalsIgnoreCase("severity")) {
							final AlertSeverity as = AlertSeverity.valueOf(args[2].toUpperCase());
							if (as == null) {
								arg0.sendMessage(Main.getInstance().msgPrefix + "You entered an invalid level.");
								return true;
							}

							if (pd.severity.equals(as)) {
								arg0.sendMessage(Main.getInstance().msgPrefix + "Your severity level is already set at "
										+ as.name() + ".");
								return true;
							}

							pd.severity = as;
							arg0.sendMessage(
									Main.getInstance().msgPrefix + "Your severity level was set as " + as.name() + ".");
							return true;
						} else if(args[1].equalsIgnoreCase("delay")) {
							long delay;
							try {
								delay = Long.valueOf(args[2]);
							} catch(Exception e) {
								arg0.sendMessage(Main.getInstance().msgPrefix + "You need to enter a valid number.");
								return true;
							}
							
							pd.setDelay(delay);
							arg0.sendMessage(Main.getInstance().msgPrefix + "You set your delay so you recieve alerts every " + delay + "ms (" + ((double)delay / 1000) + "s).");
							
							return true;
						}
					} else if(args[0].equalsIgnoreCase("vio") || args[0].equalsIgnoreCase("violations") || args[0].equalsIgnoreCase("vl")) {
						String player = args[1];
						Player pl = Bukkit.getPlayer(player);
						if(pl != null) {
							PlayerData plPd = Main.getInstance().getPlayerManager().getPlayer(pl.getUniqueId());
							if(args[2].equalsIgnoreCase("clear")) {
								plPd.setViolations(0);
								for(PacketCheck pc : Main.getInstance().getCheckManager().getChecks()) {
									pc.getViolations().remove(plPd);
								}
								
								arg0.sendMessage(Main.getInstance().msgPrefix + "You cleared " + pl.getName() + "'s violations.");
								return true;
							} else {
								arg0.sendMessage(Main.getInstance().msgPrefix + "Invalid command.");
								return true;
							}
						} else {
							arg0.sendMessage(Main.getInstance().msgPrefix + "Could not find player.");
							return true;
						}
						
						
						
					}
				}
			}
		}
		arg0.sendMessage(Main.getInstance().msgPrefix + "Invalid command.");
		return true;
	}

}
