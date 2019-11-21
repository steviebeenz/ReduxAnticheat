package redux.anticheat.check.packets.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;

public class BadInteraction extends PacketCheck {

	/*
	 * Behind/through solid wall
	 * Time between interacts
	 * 
	 * 
	 */
	
	public BadInteraction() {
		super("BadInteraction", 5, 10, null, false, true, Category.PLAYER, new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 60);
	}

	@Override
	public void listen(PacketEvent e) {
	}
	
	

}
