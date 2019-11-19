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
	private List<AMText> Text = new ArrayList<>();

	public enum ClickableType {
		RunCommand("run_command"), SuggestCommand("suggest_command"), OpenURL("open_url");

		public String action;

		ClickableType(String Action) {
			this.action = Action;
		}
	}

	public class AMText {

		private String Message = "";

		private Map<String, Map.Entry<String, String>> Modifiers = new HashMap<String, Map.Entry<String, String>>();

		public AMText(String Text) {
			this.Message = Text;
		}

		public String getMessage() {
			return this.Message;
		}

		public String getFormattedMessage() {
			String Chat = "{\"text\":\"" + this.Message + "\"";
			for (String Event : this.Modifiers.keySet()) {
				Map.Entry<String, String> Modifier = this.Modifiers.get(Event);
				Chat += ",\"" + Event + "\":{\"action\":\"" + Modifier.getKey() + "\",\"value\":" + Modifier.getValue()
						+ "}";
			}
			Chat += "}";
			return Chat;
		}

		public AMText addHoverText(String... Text) {
			String event = "hoverEvent";
			String key = "show_text";
			String value = "";
			if (Text.length == 1)
				value = "{\"text\":\"" + Text[0] + "\"}";
			else {
				value = "{\"text\":\"\",\"extra\":[";
				for (String Message : Text)
					value += "{\"text\":\"" + Message + "\"},";
				value = value.substring(0, value.length() - 1);
				value += "]}";
			}
			Map.Entry<String, String> Values = new AbstractMap.SimpleEntry<String, String>(key, value);
			this.Modifiers.put(event, Values);
			return this;
		}

		public AMText addHoverItem(ItemStack Item) {
			try {
				String event = "hoverEvent";
				String key = "show_item";
				Object cfi = ReflectionUtils.getCBClass("CraftItemStack");
				Object nmscopy = cfi.getClass().getMethod("asNMSCopy", Item.getClass()).invoke(Item);
				String value = (String) nmscopy.getClass().getMethod("getTag").invoke(nmscopy);
				Map.Entry<String, String> values = new AbstractMap.SimpleEntry<String, String>(key, value);
				this.Modifiers.put(event, values);
				return this;
			} catch (Exception e) {
				e.printStackTrace();
				return this;
			}
		}

		public AMText setClickEvent(ClickableType Type, String Value) {
			String event = "clickEvent";
			String key = Type.action;
			Map.Entry<String, String> values = new AbstractMap.SimpleEntry<String, String>(key, "\"" + Value + "\"");
			this.Modifiers.put(event, values);
			return this;
		}

	}

	public AMText addText(String Message) {
		AMText text = new AMText(Message);
		this.Text.add(text);
		return text;
	}

	public String getFormattedMessage() {
		String chat = "[\"\",";
		for (AMText text : this.Text)
			chat += text.getFormattedMessage() + ",";
		chat = chat.substring(0, chat.length() - 1);
		chat += "]";
		return chat;
	}

	public void sendToPlayer(Player player) {
		try {
			Object message = convertToIBase(this.getFormattedMessage());
			
			Object packet = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(IBASE, Byte.TYPE).newInstance(message, Byte.valueOf((byte) 1));

			Object handle = ReflectionUtils.getPlayer(player);

			if (handle != null) {
				Object connection = handle.getClass().getField("playerConnection").get(handle);
				connection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(connection,
						packet);
			}
		} catch (Exception e) {
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
				} catch (Exception exception) {
					// empty catch block
				}
				if (class_ == null) {
					try {
						class_ = ReflectionUtils.getNMSClass("ChatSerializer");
					} catch (Exception exception) {
						// empty catch block
					}
				}
				if ((method = ReflectionUtils.getMethod(class_, "a", new Class[] { String.class })) != null) {
					CONVERT_TO_IBASE = method;
					return convertToIBase(string);
				}
				throw new UnsupportedOperationException();
			} catch (Exception exception) {
				System.out.println("Unable to find ChatSerializer#a are you running a custom build?");
				exception.printStackTrace();
			}
		} else {
			try {
				return CONVERT_TO_IBASE.invoke(null, string);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return null;
	}

}
