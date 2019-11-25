package redux.anticheat.check.packets.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;

public class AutoUse extends PacketCheck {

	public AutoUse() {
		super("AutoUse", 5, 10, null, false, true, Category.PLAYER, new PacketType[] { PacketType.Play.Server.SET_SLOT, PacketType.Play.Client.POSITION_LOOK }, false, 60);
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
		if(e.getPacketType().equals(getType()[0])) {
			
			
			ItemStack hand2 = e.getPlayer().getItemInHand();
			ItemStack i = e.getPacket().getItemModifier().readSafely(0);
			
			if(i != null && hand2 != null) {
				if(hand2.getType().name().contains("POTION") && i.getType().equals(Material.AIR) && p.getOpenInventory().getType().equals(InventoryType.CRAFTING)) {
					double diff = System.currentTimeMillis() - pd.lastSentLook;
					if(pd.hasPotion && diff < 5 && diff != 0) {
						flag(pd, System.currentTimeMillis() - pd.lastSentLook + " < " +  8);
						pd.hasPotion = false;
					}
					pd.hasPotion = false;
				}
			}
			
		//	if(hand3 != null) {
				
			//} else {
				//p.sendMessage("hand 3 == null");
			//}
			
			pd.changeItemSlot = System.currentTimeMillis();
			//p.sendMessage("dropped item");
		} else {
			if(p.getItemInHand() != null && p.getItemInHand().getType().name().contains("POTION")) {
				pd.hasPotion = true;
			} else {
				pd.hasPotion = false;
			}
		}
	}

}
