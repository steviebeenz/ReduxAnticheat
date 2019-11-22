package redux.anticheat.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ReflectionUtils {

	private static String nmsPrefix, cbPrefix;
	private static Class<?> iBlockData, blockPosition, CraftWorld, vanillaBlock, worldServer;

	static {
		nmsPrefix = "net.minecraft.server."
				+ Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		cbPrefix = "org.bukkit.craftbukkit."
				+ Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		try {
			iBlockData = Class.forName(nmsPrefix + "IBlockData");
			blockPosition = Class.forName(nmsPrefix + "BlockPosition");
			CraftWorld = Class.forName(cbPrefix + "CraftWorld");
			vanillaBlock = Class.forName(nmsPrefix + "Block");
			worldServer = Class.forName(nmsPrefix + "WorldServer");
		} catch (final ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double getPing(Player p) {
		try {
			final Class<?> cp = Class.forName(cbPrefix + "entity.CraftPlayer");
			final Method getHandle = cp.getMethod("getHandle");
			final Object entityPlayer = getHandle.invoke(p);
			final double ping = entityPlayer.getClass().getField("ping").getInt(entityPlayer);
			return ping;
		} catch (final Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean getOnGround(Player p) {
		try {
			final Class<?> cp = Class.forName(cbPrefix + "entity.CraftPlayer");
			final Method getHandle = cp.getMethod("getHandle");
			final Object entityPlayer = getHandle.invoke(p);
			final Field groundField = entityPlayer.getClass().getField("onGround");
			final boolean onGround = groundField.getBoolean(entityPlayer);
			return onGround;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
//:184
	}

	public static float getSliperness(Block b) {
		try {
			final Object blockNms = getVanillaBlock(b);

			return (float) getFieldValue(getFieldByName(vanillaBlock, "frictionFactor"), blockNms);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static Object getFieldValue(Field field, Object object) {
		try {
			field.setAccessible(true);
			return field.get(object);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field getFieldByName(Class<?> clazz, String fieldName) {
		try {
			final Field field = clazz.getDeclaredField(fieldName) != null ? clazz.getDeclaredField(fieldName)
					: clazz.getSuperclass().getDeclaredField(fieldName);
			field.setAccessible(true);

			return field;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Object getVanillaBlock(Block block) {
		try {
			final Object getType = getBlockData(block);
			return getMethodValue(getMethod1(iBlockData, "getBlock"), getType);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Object getBlockData(Block block) {
		try {
			final Object bPos = blockPosition.getConstructor(int.class, int.class, int.class).newInstance(
					block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			final Object world = getWorldHandle(block.getWorld());
			return getMethodValue(getMethod1(worldServer, "getType", blockPosition), world, bPos);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getWorldHandle(org.bukkit.World world) {
		return getMethodValue(getMethod1(CraftWorld, "getHandle"), world);
	}

	public static Object getPlayer(Player p) {
		try {
			final Class<?> cp = Class.forName(cbPrefix + "entity.CraftPlayer");
			final Method getHandle = cp.getMethod("getHandle");
			final Object entityPlayer = getHandle.invoke(p);
			return entityPlayer;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getClass(final String string) {
		try {
			return Class.forName(string);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getNMSClass(final String string) {
		return getClass(nmsPrefix + string);
	}

	public static Class<?> getCBClass(final String string) {
		return getClass(cbPrefix + string);
	}

	public static double getPingModifier(Player player) {
		double ping = getPing(player);
		ping = Math.abs(0 - ping);

		ping = ping / 800;
		return ping;
	}

	public static Object getMethodValue(Method method, Object object, Object... args) {
		try {
			return method.invoke(object, args);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethod(Class<?> class_, String string, Class<?>[] arrclass) {
		Validate.notNull(string);
		Validate.notNull(class_);
		Method method = null;
		try {
			method = class_.getMethod(string, arrclass);
		} catch (final NoSuchMethodException noSuchMethodException) {
			System.out.println(
					"[Reflection] Unable to find the the method " + string + " in class " + class_.getSimpleName());
		}
		return method;
	}

	public static Method getMethod1(Class<?> clazz, String methodName, Class<?>... args) {
		try {
			final Method method = clazz.getMethod(methodName, args);
			method.setAccessible(true);
			return method;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
