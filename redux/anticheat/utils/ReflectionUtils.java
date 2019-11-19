package redux.anticheat.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionUtils {

	private static String nmsPrefix, cbPrefix;
	
	static {
		 nmsPrefix = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
         cbPrefix = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
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

	}

	public static Object getPlayer(Player p) {
		try {
			final Class<?> cp = Class.forName(cbPrefix + "entity.CraftPlayer");
			final Method getHandle = cp.getMethod("getHandle");
			final Object entityPlayer = getHandle.invoke(p);
			return entityPlayer;
		} catch (Exception e) {
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

	public static Method getMethod(Class<?> class_, String string, Class<?>[] arrclass) {
        Validate.notNull((Object)string);
        Validate.notNull(class_);
        Method method = null;
        try {
            method = class_.getMethod(string, arrclass);
        }
        catch (NoSuchMethodException noSuchMethodException) {
           System.out.println("[Reflection] Unable to find the the method " + string + " in class " + class_.getSimpleName());
        }
        return method;
    }

}
