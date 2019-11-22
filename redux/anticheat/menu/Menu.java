package redux.anticheat.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Menu {

	private final ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	private final String name;
	private final int slots;
	private final Inventory inventory;

	public Menu(String name, int slots) {
		this.name = name;
		this.slots = slots;

		inventory = Bukkit.createInventory(null, slots, name);
	}

	public String getName() {
		return name;
	}

	public int getSlots() {
		return slots;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void open(Player p) {
		p.openInventory(inventory);
	}

	public ArrayList<MenuItem> getItems() {
		return items;
	}

}
