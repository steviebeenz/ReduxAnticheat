package redux.anticheat.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Menu {
	
	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	private String name;
	private int slots;
	private Inventory inventory;
	
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
		return this.inventory;
	}
	
	public void open(Player p) {
		p.openInventory(inventory);
	}

	public ArrayList<MenuItem> getItems() {
		return items;
	}

}
