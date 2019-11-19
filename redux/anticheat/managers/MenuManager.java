package redux.anticheat.managers;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import redux.anticheat.menu.Menu;
import redux.anticheat.menu.MenuItem;
import redux.anticheat.menu.menus.ChecksMenu;
import redux.anticheat.menu.menus.ReduxMenu;
import redux.anticheat.menu.menus.checkmenu.CombatMenu;
import redux.anticheat.menu.menus.checkmenu.MovementMenu;
import redux.anticheat.menu.menus.checkmenu.PacketMenu;
import redux.anticheat.menu.menus.checkmenu.PlayerMenu;

public class MenuManager {
	
	private ArrayList<Menu> menus = new ArrayList<Menu>();
	
	public MenuManager() {
		menus.add(new ReduxMenu());
		menus.add(new ChecksMenu());
		menus.add(new MovementMenu());
		menus.add(new CombatMenu());
		menus.add(new PacketMenu());
		menus.add(new PlayerMenu());
	}
	
	public boolean containsName(String s) {
		for(Menu m : menus) {
			if(m.getInventory().getName().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
	
	public Menu getMenu(Class<? extends Menu> clazz) {
		for(Menu m : menus) {
			if(m.getClass().equals(clazz)) {
				return m;
			}
		}
		return null;
	}
	
	public MenuItem getItem(ItemStack i) {
		for(Menu m : menus) {
			for(MenuItem im : m.getItems()) {
				if(im.getName().equals(i.getItemMeta().getDisplayName()) && im.getMaterial().equals(i.getType())) {
					return im;
				}
			}
		}
		return null;
	}
	
	public ArrayList<Menu> getMenus() {
		return this.menus;
	}

}
