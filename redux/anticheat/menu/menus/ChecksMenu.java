package redux.anticheat.menu.menus;

import java.util.ArrayList;

import org.bukkit.Material;

import redux.anticheat.Main;
import redux.anticheat.menu.Menu;
import redux.anticheat.menu.MenuItem;

public class ChecksMenu extends Menu {

	public ChecksMenu() {
		super("§d§lChecks §8(§f" + Main.getInstance().getCheckManager().getChecks().size() + "§8)", 45);
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Modify the combat checks.");
		MenuItem a = new MenuItem(Material.DIAMOND_SWORD, 1, "§b§lCombat Checks", lore);
		lore.clear();
		lore.add("Modify the movement checks.");
		MenuItem b = new MenuItem(Material.IRON_BOOTS, 1, "§r§lMovement Checks", lore);
		lore.clear();
		lore.add("Modify the miscellaneous checks.");
		MenuItem c = new MenuItem(Material.BOOK, 1, "§7§lOther Checks", lore);
		lore.clear();
		lore.add("Modify the packets checks.");
		MenuItem d = new MenuItem(Material.BEDROCK, 1, "§a§lPacket Checks", lore);
		lore.clear();
		lore.add("Modify the player checks.");
		MenuItem e = new MenuItem(Material.REDSTONE, 1, "§c§lPlayer Checks", lore);
		lore.clear();
		
		this.getInventory().setItem(20, a.getItemStack());
		this.getInventory().setItem(21, b.getItemStack());
		this.getInventory().setItem(22, c.getItemStack());
		this.getInventory().setItem(23, d.getItemStack());
		this.getInventory().setItem(24, e.getItemStack());
		
		this.getItems().add(a);
		this.getItems().add(b);
		this.getItems().add(c);
		this.getItems().add(d);
		this.getItems().add(e);
	}
	
	

}
