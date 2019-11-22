package redux.anticheat.menu.menus;

import java.util.ArrayList;

import org.bukkit.Material;

import redux.anticheat.Main;
import redux.anticheat.menu.Menu;
import redux.anticheat.menu.MenuItem;

public class ChecksMenu extends Menu {

	public ChecksMenu() {
		super("§d§lChecks §8(§f" + Main.getInstance().getCheckManager().getChecks().size() + "§8)", 45);

		final ArrayList<String> lore = new ArrayList<String>();
		lore.add("Modify the combat checks.");
		final MenuItem a = new MenuItem(Material.DIAMOND_SWORD, 1, "§b§lCombat Checks", lore);
		lore.clear();
		lore.add("Modify the movement checks.");
		final MenuItem b = new MenuItem(Material.IRON_BOOTS, 1, "§r§lMovement Checks", lore);
		lore.clear();
		lore.add("Modify the miscellaneous checks.");
		final MenuItem c = new MenuItem(Material.BOOK, 1, "§7§lOther Checks", lore);
		lore.clear();
		lore.add("Modify the packets checks.");
		final MenuItem d = new MenuItem(Material.BEDROCK, 1, "§a§lPacket Checks", lore);
		lore.clear();
		lore.add("Modify the player checks.");
		final MenuItem e = new MenuItem(Material.REDSTONE, 1, "§c§lPlayer Checks", lore);
		lore.clear();

		getInventory().setItem(20, a.getItemStack());
		getInventory().setItem(21, b.getItemStack());
		getInventory().setItem(22, c.getItemStack());
		getInventory().setItem(23, d.getItemStack());
		getInventory().setItem(24, e.getItemStack());

		getItems().add(a);
		getItems().add(b);
		getItems().add(c);
		getItems().add(d);
		getItems().add(e);
	}

}
