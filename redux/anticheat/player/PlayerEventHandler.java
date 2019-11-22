package redux.anticheat.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import redux.anticheat.Main;
import redux.anticheat.menu.MenuItem;
import redux.anticheat.menu.menus.ChecksMenu;
import redux.anticheat.menu.menus.checkmenu.CombatMenu;
import redux.anticheat.menu.menus.checkmenu.MovementMenu;
import redux.anticheat.menu.menus.checkmenu.PacketMenu;
import redux.anticheat.menu.menus.checkmenu.PlayerMenu;

public class PlayerEventHandler implements Listener {

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Main.getInstance().getPlayerManager().addPlayer(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Main.getInstance().getPlayerManager().removePlayer(e.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		Main.getInstance().getPlayerManager().removePlayer(e.getPlayer());
	}

	@EventHandler
	public void onGamemodeChange(PlayerGameModeChangeEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (pd != null) {
			pd.changeTicks = 20;
		}
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (pd != null) {
			pd.eatTicks = 20;
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (pd != null) {
			pd.lastBlockPlace = System.currentTimeMillis();
			if (pd.lastBlockPlaceData == null) {
				pd.lastBlockPlaceData = new BlockPlaceData(0L, e.getBlock(), e.getBlockAgainst());
				pd.blockPlaceData = new BlockPlaceData(System.currentTimeMillis(), e.getBlock(), e.getBlockAgainst());
				return;
			}
			pd.lastBlockPlaceData = pd.blockPlaceData;
			pd.blockPlacePacket++;
			pd.blockPlaceData = new BlockPlaceData(System.currentTimeMillis(), e.getBlock(), e.getBlockAgainst());
		}
	}

	@EventHandler
	public void onVelocity(PlayerVelocityEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (pd != null) {
			if (System.currentTimeMillis() - pd.getLastTeleported() >= 1L
					&& System.currentTimeMillis() - pd.lastDamage >= 2L) {
				pd.lastVelocity = System.currentTimeMillis();
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (pd != null) {
			pd.teleportTicks = 40;
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
		if (pd != null) {
			pd.teleportTicks = 40;
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final PlayerData data = Main.getInstance().getPlayerManager().getPlayer(event.getEntity().getUniqueId());
			if (data != null && (event.getCause() == EntityDamageEvent.DamageCause.FALL
					|| event.getCause() == EntityDamageEvent.DamageCause.MAGIC
					|| event.getCause() == EntityDamageEvent.DamageCause.DROWNING
					|| event.getCause() == EntityDamageEvent.DamageCause.FIRE
					|| event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
					|| event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK
					|| event.getCause() == EntityDamageEvent.DamageCause.POISON)) {
				data.lastDamage = System.currentTimeMillis();
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			final Player p = (Player) e.getWhoClicked();
			if (Main.getInstance().getMenuManager().containsName(e.getClickedInventory().getName())) {
				e.setCancelled(true);

				if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
					final MenuItem mi = Main.getInstance().getMenuManager().getItem(e.getCurrentItem());
					if (mi != null) {
						if (mi.getName().equals("§d§lCheck Settings")) {
							p.openInventory(
									Main.getInstance().getMenuManager().getMenu(ChecksMenu.class).getInventory());
							return;
						} else if (mi.getName().equals("§r§lMovement Checks")) {
							p.openInventory(
									Main.getInstance().getMenuManager().getMenu(MovementMenu.class).getInventory());
							return;
						} else if (mi.getName().equals("§c§lPlayer Checks")) {
							p.openInventory(
									Main.getInstance().getMenuManager().getMenu(PlayerMenu.class).getInventory());
							return;
						} else if (mi.getName().equals("§a§lPacket Checks")) {
							p.openInventory(
									Main.getInstance().getMenuManager().getMenu(PacketMenu.class).getInventory());
							return;
						} else if (mi.getName().equals("§b§lCombat Checks")) {
							p.openInventory(
									Main.getInstance().getMenuManager().getMenu(CombatMenu.class).getInventory());
							return;
						}
					}
					return;
				}

			}
		}
	}

}
