package redux.anticheat.learning;

import java.io.File;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;

public class Learning {

	public Learning() {
		final boolean enable = Main.getInstance().getConfig().getBoolean("learning.enabled")
				&& !Main.getInstance().getConfig().getBoolean("settings.slowServer");
		if (!enable) {
			Main.sendConsole("§7Tried to enable learning, but config says otherwise.");
			return;
		}

		final File dataDir = new File(Main.getInstance().getDataFolder() + File.separator + "learning");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}

		final File moveDataDir = new File(
				Main.getInstance().getDataFolder() + File.separator + "learning" + File.separator + "move");
		final File combatDataDir = new File(
				Main.getInstance().getDataFolder() + File.separator + "learning" + File.separator + "combat");

		if (!moveDataDir.exists()) {
			moveDataDir.mkdir();
		}

		if (!combatDataDir.exists()) {
			combatDataDir.mkdir();
		}

		final boolean combat = Main.getInstance().getConfig().getBoolean("learning.combat"),
				movement = Main.getInstance().getConfig().getBoolean("learning.movement");

		if (combat) {
			for (final PacketCheck c : Main.getInstance().getCheckManager().getChecks()) {
				if (c.getCategory().equals(Category.COMBAT)) {
					if (c.canLearn()) {
						final File checkDir = new File(combatDataDir + File.separator + c.getName());
						if (checkDir.exists()) {
							checkDir.mkdirs();
						}
					}
				}
			}

		}
		if (movement) {
			for (final PacketCheck c : Main.getInstance().getCheckManager().getChecks()) {
				if (c.getCategory().equals(Category.MOVEMENT)) {
					if (c.canLearn()) {
						final File checkDir = new File(moveDataDir + File.separator + c.getName());
						if (!checkDir.exists()) {
							checkDir.mkdirs();
						}
					}
				}
			}
		}

	}

}
