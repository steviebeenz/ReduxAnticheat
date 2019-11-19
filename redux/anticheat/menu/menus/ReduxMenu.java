package redux.anticheat.menu.menus;

import java.util.ArrayList;

import org.bukkit.Material;

import redux.anticheat.menu.Menu;
import redux.anticheat.menu.MenuItem;

public class ReduxMenu extends Menu {
	
	public ReduxMenu() {
		super("§d§lRedux Menu", 45);
		
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Modify or edit checks.");
		MenuItem a = new MenuItem(Material.COMPASS, 1, "§d§lCheck Settings", lore);
		lore.clear();
		lore.add("Shows information about redux");
		MenuItem b = new MenuItem(Material.PAPER, 1, "§d§lVersion", lore);
		lore.clear();
		lore.add("View player information.");
		MenuItem c = new MenuItem(Material.IRON_SWORD, 1, "§d§lPlayers", lore);
		lore.clear();
		
		this.getInventory().setItem(20, a.getItemStack());
		this.getInventory().setItem(22, b.getItemStack());
		this.getInventory().setItem(24, c.getItemStack());
		
		this.getItems().add(a);
		this.getItems().add(b);
		this.getItems().add(c);
	}
	
	
	

}
