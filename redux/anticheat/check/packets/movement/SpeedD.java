package redux.anticheat.check.packets.movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;

public class SpeedD extends PacketCheck {

	public SpeedD() {
		super("Speed [D]", 5, 0, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 85);
	}

	@Override
	public void listen(PacketEvent e) {
		
	}

}
