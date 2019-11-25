package redux.anticheat.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import redux.anticheat.player.PlayerData;

public class LocUtils {

	private static ArrayList<String> weirdBlocks = new ArrayList<String>();

	static {
		weirdBlocks.add("WEB");
		weirdBlocks.add("SLAB");
		weirdBlocks.add("SNOW");
		weirdBlocks.add("STAIR");
		weirdBlocks.add("DOOR");
		weirdBlocks.add("TRAPDOOR");
		weirdBlocks.add("FENCE");
		weirdBlocks.add("SENSOR");
		weirdBlocks.add("REPEATER");
		weirdBlocks.add("COMPARATOR");
		weirdBlocks.add("GATE");
		weirdBlocks.add("CARPET");
		weirdBlocks.add("BED");
		weirdBlocks.add("LAVA");
		weirdBlocks.add("WATER");
	}

	public boolean isCollidedWithWeirdBlock(Location from, Location to) {
		if (from == null || to == null) {
			return false;
		}

		for (final String s : weirdBlocks) {
			if (this.isCollided(to, s)) {
				return true;
			} else {
				if (this.isCollided(from, s)) {
					return true;
				} else {
					continue;
				}
			}
		}

		return false;
	}

	public int getDistanceFromMouse(PlayerData pd, final Entity entity) {
		final float[] neededRotations = getRotationsNeeded(pd, entity);
		if (neededRotations != null) {
			final float neededYaw = pd.getLastLocation().getYaw() - neededRotations[0];
			final float neededPitch = pd.getLastLocation().getPitch() - neededRotations[1];
			final float distanceFromMouse = (float) Math.sqrt(neededYaw * neededYaw + neededPitch * neededPitch * 2.0f);
			return (int) distanceFromMouse;
		}
		return -1;
	}

	public float[] getRotationsNeeded(PlayerData pd, Entity entity) {
		if (entity == null) {
			return null;
		}

		final double diffX = entity.getLocation().getX() - pd.getLastLocation().getX();
		final double diffZ = entity.getLocation().getZ() - pd.getLastLocation().getZ();
		double diffY;

		if ((entity instanceof LivingEntity)) {
			final LivingEntity entityLivingBase = (LivingEntity) entity;
			diffY = entityLivingBase.getLocation().getY() + entityLivingBase.getEyeHeight()
					- (pd.getLastLocation().getY() + pd.getPlayer().getEyeHeight());
		} else {
			return new float[] { 0, 0 };
		}

		final double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
		final float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D);

		return new float[] { pd.getLastLocation().getYaw() + wrap(yaw - pd.getLastLocation().getYaw()),
				pd.getLastLocation().getPitch() + wrap(pitch - pd.getLastLocation().getPitch()) };
	}

	public static float wrap(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	public double getHorizontalDistance(final Location from, final Location to) {
		if (from == null || to == null) {
			return 1;
		}
		final double deltaX = to.getX() - from.getX();
		final double deltaZ = to.getZ() - from.getZ();
		return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
	}

	public boolean isOnSolidGround(Location loc) {
		if (loc != null) {
			for (double x = -0.5; x < 0.5; x += 0.2) {
				for (double y = -0.5; y < 1; y += 0.5) {
					for (double z = -0.5; z < 0.5; z += 0.2) {
						if (!isAir(loc.clone().add(x, y, z).getBlock().getType())
								&& !isLiquid(loc.clone().add(x, y, z).getBlock().getType())) {
							return true;
						}
					}
				}
			}
		}

		return false;

	}

	public boolean isCollidedWeb(Location oldLoc, Location newLoc) {
		if (this.isCollided(oldLoc, "WEB") || this.isCollided(newLoc, "WEB") || this.isCollidedWeb(oldLoc, "WEB")
				|| this.isCollidedWeb(newLoc, "WEB")) {
			return true;
		}
		return false;
	}

	public boolean isCollidedStairs(Location oldLoc, Location newLoc) {
		if (this.isCollided(oldLoc, "STAIRS") || this.isCollided(newLoc, "STAIRS")) {
			return true;
		}
		return false;
	}

	public boolean isCollidedSlab(Location oldLoc, Location newLoc) {
		if (this.isCollided(oldLoc, "SLAB") || this.isCollided(newLoc, "SLAB")) {
			return true;
		}
		return false;
	}

	public boolean isUnderStairs(Location l) {
		if (!l.equals(null)) {
			for (double x = -1; x < 1; x += 0.2) {
				for (double y = -0.5; y < 1; y += 0.5) {
					for (double z = -1; z < 1; z += 0.2) {
						if (isStair(l.clone().add(x, y, z).getBlock().getType())) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean isOnSmallBlock(Location l) {
		if (!l.equals(null)) {
			for (double x = -0.9; x < 0.9; x += 0.1) {
				for (double y = -0.9; y < 0; y += 0.1) {
					for (double z = -0.9; z < 0.9; z += 0.1) {
						final Block b = l.clone().add(x, y, z).getBlock();
						if (b.getType().name().contains("LILY")) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean isStair(Material type) {
		if (type.name().contains("STAIRS")) {
			return true;
		}
		return false;
	}

	public boolean isAir(Material type) {
		if (type.name().contains("AIR")) {
			return true;
		}
		return false;
	}

	public Block getSecondBlockDown(Location l) {
		if (l.clone().add(0, -1.5, 0).getBlock().getType() != Material.AIR) {
			return l.clone().add(0, -1.5, 0).getBlock();
		}

		return null;
	}

	public Block getBlockUnder(Location l) {
		if (l.getBlock().getType().isSolid()) {
			return l.getBlock();
		} else {
			final int y = (int) l.getY();
			for (int i = y; i > 0; i--) {
				final Block b = l.getWorld().getBlockAt((int) l.getX(), i, (int) l.getZ());
				if (b.getType() != Material.AIR) {
					return b;
				}
			}
		}
		return null;
	}

	public boolean isInLiquid(Location loc) {
		if (loc != null) {
			for (double x = -1; x < 1; x += 0.5) {
				for (double y = -1; y < 1; y += 0.5) {
					for (double z = -1; z < 1; z += 0.5) {
						if (isLiquid(loc.clone().add(x, y, z).getBlock().getType())) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Boolean isLiquid(Material mat) {
		return (mat.name().contains("WATER") || mat.name().contains("LAVA"));
	}

	public Boolean isClimbable(Material mat) {
		return (mat.name().contains("LADDER") || mat.name().contains("VINE")) || mat.name().contains("SCAFFOLD");
	}

	public Boolean isSlimeBlock(Material mat) {
		return (mat.name().contains("SLIME"));
	}

	public boolean isOnSlime(Location l) {
		try {
			if (!l.equals(null)) {
				for (double x = -1; x < 1; x += 0.5) {
					for (double y = -1; y < 1; y += 0.5) {
						for (double z = -1; z < 1; z += 0.5) {
							if (isSlimeBlock(l.clone().add(x, y, z).getBlock().getType())) {
								return true;
							}
						}
					}
				}

				return false;
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean canClimb(Player p) {
		for (double x = -1; x < 1; x += .5) {
			for (double y = -1; y < 1; y += .5) {
				for (double z = -1; z < 1; z += .5) {
					if (isClimbable(p.getLocation().clone().add(x, y, z).getBlock().getType())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean canClimb(Location l) {
		if (l != null) {
			for (double x = -1; x < 1; x += .2) {
				for (double y = -1; y < 1; y += .2) {
					for (double z = -1; z < 1; z += .2) {
						final Block b = l.clone().add(x, y, z).getBlock();
						if (b != null) {
							if (isClimbable(b.getType())) {
								return true;
							}
						} else {
							return false;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean isBlockAbove(Location l) {
		for (double x = -0.5; x < 0.5; x += .2) {
			for (double y = 1.8; y < 2.8; y += 0.2) {
				for (double z = -0.5; z < 0.5; z += .2) {
					if (l.clone().add(x, y, z).getBlock().getType().isSolid()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public final double MIN_EUCILDEAN_VALUE = 0b100000000000000;

	public long getGcd(long current, long previous) {
		return previous <= MIN_EUCILDEAN_VALUE ? current : getGcd(previous, current % previous);
	}

	public boolean isCollided(Location l, Material m) {
		if (l != null) {
			for (double x = -0.5; x < 0.5; x += .2) {
				for (double y = -0.5; y < 0.5; y += .2) {
					for (double z = -0.5; z < 0.5; z += .2) {
						if (l.clone().add(x, y, z).getBlock().getType().equals(m)
								|| l.clone().add(x, y, z).getBlock().getType().name().contains(m.name())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isCollided(Location l, String string) {
		for (double x = -0.5; x < 0.5; x += .2) {
			for (double y = -0.9; y < 0.5; y += .2) {
				for (double z = -0.5; z < 0.5; z += .2) {
					if (l.clone().add(x, y, z).getBlock().getType().name().contains(string)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isCollidedWeb(Location l, String string) {
		for (double x = -0.5; x < 0.5; x += .2) {
			for (double y = -0.9; y < 0.9; y += .2) {
				for (double z = -0.5; z < 0.5; z += .2) {
					if (l.clone().add(x, y, z).getBlock().getType().name().contains(string)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isCollidedVertically(Location l, String string) {
		for (double y = -0.9; y < 0.5; y += .1) {
			if (l.clone().add(0, y, 0).getBlock().getType().name().contains(string)) {
				return true;
			}

		}
		return false;
	}

	public boolean isCollided(Location location, String string, double posOffset, double negOffset) {
		for (double x = -0.5 + negOffset; x < 0.5 - posOffset; x += .2) {
			for (double y = -0.9; y < 0.5; y += .2) {
				for (double z = -0.5 + negOffset; z < 0.5 - posOffset; z += .2) {
					if (location.clone().add(x, y, z).getBlock().getType().name().contains(string)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
