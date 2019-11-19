package redux.anticheat;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import redux.anticheat.commands.ReduxCommand;
import redux.anticheat.learning.Learning;
import redux.anticheat.managers.CheckManager;
import redux.anticheat.managers.MenuManager;
import redux.anticheat.managers.PlayerManager;
import redux.anticheat.menu.menus.ReduxMenu;
import redux.anticheat.player.PlayerEventHandler;
import redux.anticheat.tasks.PacketsClear;
import redux.anticheat.tasks.TpsTask;
import redux.anticheat.utils.LocUtils;

public class Main extends JavaPlugin {

	private ProtocolManager procManager;
	private CheckManager checkManager;
	private PlayerManager playerManager;
	private Learning learning;
	private TpsTask tps = null;
	public final String consolePrefix = "§7[§dRedux§7] ", msgPrefix = "§d§lRedux §7| ";
	public String removalMessage = "";
	public int tpsTask, clearTask;
	public ReduxCommand reduxCommand;
	private LocUtils locUtils;
	public boolean logConsole;
	public String version;
	private MenuManager menuManager;

	private static boolean slowServer;

	@Override
	public void onLoad() {
		final File f = new File(getDataFolder(), "config.yml");
		if (!f.exists()) {
			saveResource("config.yml", true);
		}
	}

	private static Main instance;

	public static Main getInstance() {
		return instance;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;

		version = getDescription().getVersion();

		reduxCommand = new ReduxCommand();
		registerCommand(reduxCommand);

		sendConsole("BukkitVersion: " + Bukkit.getBukkitVersion() + " vs Version: " + Bukkit.getVersion());
		sendConsole("§dRedux Anticheat v" + version + " §7is loading.");
		Bukkit.getPluginManager().registerEvents(new PlayerEventHandler(), instance);
		slowServer = getConfig().getBoolean("settings.slowServer");
		logConsole = getConfig().getBoolean("settings.sendToConsole");
		int i = 0;
		for(String s : getConfig().getStringList("messages.removalMessage")) {
			i++;
			if(i >= 1) {
				removalMessage += s + "\n";
			} else {
				removalMessage += s;
			}
		}
		
		removalMessage = ChatColor.translateAlternateColorCodes('&', removalMessage);
		tps = new TpsTask();
		tpsTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(instance, tps, 0, 1L);
		playerManager = new PlayerManager();
		clearTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(instance, new PacketsClear(), 0L, 20L);
		for (final Player p : Bukkit.getOnlinePlayers()) {
			getPlayerManager().addPlayer(p);
		}

		if (procManager == null) {
			procManager = ProtocolLibrary.getProtocolManager();
		}
		
		locUtils = new LocUtils();
		
		checkManager = new CheckManager(slowServer);
		if (!slowServer) {
			learning = new Learning();
		}
		menuManager = new MenuManager();

		sendConsole("§dRedux §7has been enabled.");
	}

	@Override
	public void onDisable() {
		checkManager.disable();
		for (final Player p : Bukkit.getOnlinePlayers()) {
			playerManager.removePlayer(p);
		}
		menuManager.getMenus().clear();
		HandlerList.unregisterAll(instance);
		procManager.removePacketListeners(instance);
		Bukkit.getScheduler().cancelTasks(instance);
		this.unregisterCommand("redux");
		sendConsole("§dRedux Anticheat §7has been disabled.");
	}

	public ProtocolManager getProtocolManager() {
		return procManager;
	}

	public CheckManager getCheckManager() {
		return checkManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static void sendConsole(String s) {
		Bukkit.getConsoleSender().sendMessage(getInstance().consolePrefix + s);
	}

	public Learning getLearning() {
		return learning;
	}

	public TpsTask getTpsTask() {
		return tps;
	}

	public void registerCommand(Command... commands) {
		try {
			final Field bukkitCommandMap = getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			final CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());

			Arrays.stream(commands).forEach(command -> commandMap.register(getName(), command));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void unregisterCommand(String s) {
		try {
			final Field bukkitCommandMap = getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			final CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());

			final Field f = commandMap.getClass().getDeclaredField("knownCommands");
			f.setAccessible(true);

			Map<String, Command> knownCommands = (Map<String, Command>) f.get(commandMap);
			if (knownCommands.get(s) != null) {
				knownCommands.remove(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getVersion() {
		return version;
	}
	
	public LocUtils getLocUtils() {
		return this.locUtils;
	}

	public ReduxMenu getReduxMenu() {
		return (ReduxMenu) menuManager.getMenu(ReduxMenu.class);
	}
	
	public MenuManager getMenuManager() {
		return this.menuManager;
	}

}
