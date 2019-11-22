package redux.anticheat.utils;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HoverText {

	private static Class<?> IBASE;
	private static Method CONVERT_TO_IBASE;
	private final List<AMText> Text = new ArrayList<>();

	public enum ClickableType {
		RunCommand("run_command"), SuggestCommand("suggest_command"), OpenURL("open_url");

		public String action;

		ClickableType(String Action) {
			action = Action;
		}
	}

	public class AMText {

		private String Message = "";

		private final Map<String, Map.Entry<String, String>> Modifiers = new HashMap<String, Map.Entry<String, String>>();

		public AMText(String Text) {
			Message = Text;
		}

		public String getMessage() {
			return Message;
		}

		public String getFormattedMessage() {
			String Chat = "{\"text\":\"" + Message + "\"";
			for (final String Event : Modifiers.keySet()) {
				final Map.Entry<String, String> Modifier = Modifiers.get(Event);
				Chat += ",\"" + Event + "\":{\"action\":\"" + Modifier.getKey() + "\",\"value\":" + Modifier.getValue()
						+ "}";
			}
			Chat += "}";
			return Chat;
		}

		public AMText addHoverText(String... Text) {
			final String event = "hoverEvent";
			final String key = "show_text";
			String value = "";
			if (Text.length == 1) {
				value = "{\"text\":\"" + Text[0] + "\"}";
			} else {
				value = "{\"text\":\"\",\"extra\":[";
				for (final String Message : Text) {
					value += "{\"text\":\"" + Message + "\"},";
				}
				value = value.substring(0, value.length() - 1);
				value += "]}";
			}
			final Map.Entry<String, String> Values = new AbstractMap.SimpleEntry<String, String>(key, value);
			Modifiers.put(event, Values);
			return this;
		}

		public AMText addHoverItem(ItemStack Item) {
			try {
				final String event = "hoverEvent";
				final String key = "show_item";
				final Object cfi = ReflectionUtils.getCBClass("CraftItemStack");
				final Object nmscopy = cfi.getClass().getMethod("asNMSCopy", Item.getClass()).invoke(Item);
				final String value = (String) nmscopy.getClass().getMethod("getTag").invoke(nmscopy);
				final Map.Entry<String, String> values = new AbstractMap.SimpleEntry<String, String>(key, value);
				Modifiers.put(event, values);
				return this;
			} catch (final Exception e) {
				e.printStackTrace();
				return this;
			}
		}

		public AMText setClickEvent(ClickableType Type, String Value) {
			final String event = "clickEvent";
			final String key = Type.action;
			final Map.Entry<String, String> values = new AbstractMap.SimpleEntry<String, String>(key,
					"\"" + Value + "\"");
			Modifiers.put(event, values);
			return this;
		}

	}

	public AMText addText(String Message) {
		final AMText text = new AMText(Message);
		Text.add(text);
		return text;
	}

	public String getFormattedMessage() {
		String chat = "[\"\",";
		for (final AMText text : Text) {
			chat += text.getFormattedMessage() + ",";
		}
		chat = chat.substring(0, chat.length() - 1);
		chat += "]";
		return chat;
	}

	public void sendToPlayer(Player player) {
		try {
			final Object message = convertToIBase(getFormattedMessage());

			final Object packet = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(IBASE, Byte.TYPE)
					.newInstance(message, Byte.valueOf((byte) 1));

			final Object handle = ReflectionUtils.getPlayer(player);

			if (handle != null) {
				final Object connection = handle.getClass().getField("playerConnection").get(handle);
				connection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(connection,
						packet);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private Object convertToIBase(String string) {
		if (CONVERT_TO_IBASE == null) {
			try {
				Method method;
				IBASE = ReflectionUtils.getNMSClass("IChatBaseComponent");
				Class<?> class_ = null;
				try {
					class_ = ReflectionUtils.getNMSClass("IChatBaseComponent$ChatSerializer");
				} catch (final Exception exception) {
					// empty catch block
				}
				if (class_ == null) {
					try {
						class_ = ReflectionUtils.getNMSClass("ChatSerializer");
					} catch (final Exception exception) {
						// empty catch block
					}
				}
				if ((method = ReflectionUtils.getMethod(class_, "a", new Class[] { String.class })) != null) {
					CONVERT_TO_IBASE = method;
					return convertToIBase(string);
				}
				throw new UnsupportedOperationException();
			} catch (final Exception exception) {
				System.out.println("Unable to find ChatSerializer#a are you running a custom build?");
				exception.printStackTrace();
			}
		} else {
			try {
				return CONVERT_TO_IBASE.invoke(null, string);
			} catch (final Exception exception) {
				exception.printStackTrace();
			}
		}
		return null;
	}

}
