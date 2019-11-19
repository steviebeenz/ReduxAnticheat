package redux.anticheat.menu;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItem {
	
	public ItemStack itemStack;
	public int amount;
	private String name;
	private ArrayList<String> lore;
	private Material material;
	
	public MenuItem(Material material, int amount, String name, ArrayList<String> lore) {
		this.amount = amount;
		this.name = name;
		this.lore = lore;
		this.material = material;
		
		itemStack = new ItemStack(material, amount);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public ItemMeta getItemMeta() {
		return itemStack.getItemMeta();
	}

	public int getAmount() {
		return amount;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getLore() {
		return lore;
	}

	public Material getMaterial() {
		return material;
	}

}
