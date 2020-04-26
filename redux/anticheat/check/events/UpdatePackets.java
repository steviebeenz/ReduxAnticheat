package redux.anticheat.check.events;

import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import redux.anticheat.Main;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class UpdatePackets {

	public static WeakHashMap<UUID, Long> updates = new WeakHashMap<>();

	public boolean listen() {
		Main.getInstance().getProtocolManager()
				.addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR,
						new PacketType[] { PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION }) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						final PacketContainer packet = e.getPacket();
						final Player p = e.getPlayer();

						final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
						if (pd == null) {
						}

						if (pd != null && System.currentTimeMillis() - pd.join < 1000) {
							return;
						}

						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}
						
						
						pd.setLastMovement(System.currentTimeMillis());
						pd.setLastLocation(p.getLocation());
						pd.setNextLocation(new Location(p.getWorld(), packet.getDoubles().readSafely(0),
								packet.getDoubles().readSafely(1), packet.getDoubles().readSafely(2)));

						pd.setLastDeltaXZ(pd.getDeltaXZ());
						pd.setDeltaXZ(Main.getInstance().getLocUtils().getHorizontalDistance(pd.getLastLocation(),
								pd.getNextLocation()));
						pd.setPreviousDeltaY(pd.getDeltaY());
						pd.setDeltaY(pd.getNextLocation().getY() - pd.getLastLocation().getY());

						if(pd.getDeltaY() > 0) {
							pd.isRising = true;
							pd.risingTicks++;
							pd.fallingTicks = 0;
							pd.isFalling = false;
						} else if(pd.getDeltaY() < 0) {
							pd.isRising = false;
							pd.fallingTicks++;
							pd.risingTicks = 0;
							pd.fallingTicks = 0;
							pd.isFalling = true;
						} else {
							pd.risingTicks = 0;
							pd.isRising = false;
							pd.isFalling = false;
						}
						
						if (p.isInsideVehicle() || p.getVehicle() != null) {
							pd.vehicleTicks = 20;
						} else {
							if (pd.vehicleTicks > 0) {
								pd.vehicleTicks--;
							}
						}
						if (pd.teleportTicks > 0) {
							pd.teleportTicks--;
						}

						if (pd.changeGamemodeTicks > 0) {
							pd.changeGamemodeTicks--;
						}
						
						if(pd.flyTicks > 0 && !p.isFlying()) {
							pd.flyTicks--;
						}

						if (pd.eatTicks > 0) {
							pd.eatTicks--;
						}

						updates.put(p.getUniqueId(), System.currentTimeMillis());
						pd.addPacket();

						if (System.currentTimeMillis() - pd.getLastTeleported() > 500) {
							if ((!pd.getNextLocation().equals(null) && !pd.getLastLocation().equals(null))) {
								if (Main.getInstance().getLocUtils().isOnSlime(pd.getNextLocation())
										|| Main.getInstance().getLocUtils().isOnSlime(pd.getLastLocation())
										|| Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "SLIME")
										|| Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), "SLIME")) {
									pd.setLastOnSlime(System.currentTimeMillis());
								}
							}
						}

						if (Main.getInstance().getLocUtils().isBlockAbove(pd.getNextLocation())
								|| Main.getInstance().getLocUtils().isBlockAbove(pd.getLastLocation())) {
							pd.blockAboveTicks = 20;
						} else {
							if (pd.blockAboveTicks > 0) {
								pd.blockAboveTicks--;
							}
						}

						if (Main.getInstance().getLocUtils().isCollidedVertically(pd.getNextLocation(), "ICE")) {
							pd.iceTicks = 40;
						} else {
							if (pd.iceTicks > 0) {
								pd.iceTicks--;
							}
						}

						if (!ReflectionUtils.getOnGround(p)) {
							pd.offGroundTicks++;
							pd.onGroundTicks = 0;
						} else {
							pd.offGroundTicks = 0;
							pd.onGroundTicks++;
						}

						if (pd.getNextLocation() != null && pd.getLastLocation() != null) {
							Main.getInstance().getCheckManager().runCheck(e);
						}
					}
				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.KEEP_ALIVE) {

					@Override
					public void onPacketReceiving(PacketEvent event) {
						Main.getInstance().getCheckManager().runCheck(event);
					}

				});
		
		Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Server.SET_SLOT) {

			@Override
			public void onPacketSending(PacketEvent event) {
				Main.getInstance().getCheckManager().runCheck(event);
			}
			
		});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.USE_ITEM) {

					@Override
					public void onPacketSending(PacketEvent event) {
						Main.getInstance().getCheckManager().runCheck(event);
					}

				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.ABILITIES) {

					@Override
					public void onPacketReceiving(PacketEvent e) {
						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}
						Main.getInstance().getCheckManager().runCheck(e);
					}

				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.FLYING) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						final Player p = e.getPlayer();
						final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

						if (pd != null) {

						} else {
							return;
						}

						if (System.currentTimeMillis() - pd.join < 1000) {
							return;
						}

						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}

						if (pd.flyingA != 0L) {
							pd.lastFlyingA = pd.flyingA;
						}
						pd.flyingA = System.currentTimeMillis();

						Main.getInstance().getCheckManager().runCheck(e);

					}
				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.BLOCK_PLACE) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						final Player p = e.getPlayer();
						final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}
						pd.addPlacePacket();
						Main.getInstance().getCheckManager().runCheck(e);

					}
				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.ARM_ANIMATION) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						final PlayerData pd = Main.getInstance().getPlayerManager()
								.getPlayer(e.getPlayer().getUniqueId());
						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}
						pd.setLastSwing(System.currentTimeMillis());
						pd.addSwingPackets();
						Main.getInstance().getCheckManager().runCheck(e);
						updates.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
					}
				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Server.RESPAWN) {

					@Override
					public void onPacketSending(PacketEvent e) {
						final PlayerData pd = Main.getInstance().getPlayerManager()
								.getPlayer(e.getPlayer().getUniqueId());
						pd.teleportTicks = 40;
					}

				});

		Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(),
				ListenerPriority.MONITOR, PacketType.Play.Client.TELEPORT_ACCEPT) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				final PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
				if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
					e.getPlayer().sendMessage(e.getPacketType().name());
				}
				pd.teleportTicks = 40;
				//pd.getPlayer().sendMessage("teleport");

				pd.setLastTeleported(System.currentTimeMillis());
				updates.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
				pd.addPacket();
			}
		});

		Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(),
				ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_VELOCITY) {
			@Override
			public void onPacketSending(PacketEvent e) {

			}
		});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.CUSTOM_PAYLOAD) {

					@Override
					public void onPacketReceiving(PacketEvent e) {
						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}
					}

				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						final PlayerData pd = Main.getInstance().getPlayerManager()
								.getPlayer(e.getPlayer().getUniqueId());

						if (Main.getInstance().reduxCommand.debugEnabled.contains(e.getPlayer().getUniqueId())) {
							e.getPlayer().sendMessage(e.getPacketType().name());
						}

						if (e.getPacket().getEntityUseActions().readSafely(0).equals(EntityUseAction.ATTACK)) {
							pd.addAttackEntityPacket();
						} else {
							pd.addInteractEntityPacket();
						}
						Main.getInstance().getCheckManager().runCheck(e);
					}
				});

		Main.getInstance().getProtocolManager().addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Client.WINDOW_CLICK) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						Main.getInstance().getCheckManager().runCheck(e);
					}
				});

		Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(),
				ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent e) {
				// if(e.getPlayer().hasPermission("redux.bypass")) {
				// return;
				// } else {
				//e.setReadOnly(false);
				//final PacketContainer packet = e.getPacket().deepClone();
				//final Entity entity = packet.getEntityModifier(e).readSafely(0);
				//final StructureModifier<List<WrappedWatchableObject>> modifier = packet
				//		.getWatchableCollectionModifier();
				//final List<WrappedWatchableObject> read = modifier.read(0);

				//e.setPacket(packet);

				//if (!(entity instanceof LivingEntity) || entity == e.getPlayer()) {
				//	return;
				//}

				//for (final WrappedWatchableObject obj : read) {
				//	if (obj.getIndex() == 6) {
				//		final float value = (float) obj.getValue();
				//		if (value > 0) {
				//			obj.setValue(1f, false);
				//		}
				//	}
				//}
				// }
			}
		});

		return true;
	}

	public static boolean shouldCheck(UUID id) {
		if (updates.containsKey(id)) {
			return true;
		}
		return false;
	}

}
