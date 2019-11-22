package redux.anticheat.menu.menus.checkmenu;

import java.util.ArrayList;

import org.bukkit.Material;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.menu.Menu;
import redux.anticheat.menu.MenuItem;

public class MovementMenu extends Menu {

	public MovementMenu() {
		super("§d§lMovement §8(§f§l" + Main.getInstance().getCheckManager().getChecks(Category.MOVEMENT) + " checks§8)",
				45);

		int slot = 0;
		final ArrayList<String> lore = new ArrayList<String>();
		for (final PacketCheck c : Main.getInstance().getCheckManager().get(Category.MOVEMENT)) {
			lore.add(c.getDescription());
			MenuItem add;
			if (c.isEnabled()) {
				add = new MenuItem(Material.EMERALD_BLOCK, 1, "§d" + c.getName(), lore);
			} else {
				add = new MenuItem(Material.BARRIER, 1, "§d" + c.getName(), lore);
			}
			getInventory().setItem(slot, add.getItemStack());
			getItems().add(add);

			lore.clear();
			slot++;
			continue;
		}
	}

}
