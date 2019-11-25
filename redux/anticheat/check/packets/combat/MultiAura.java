package redux.anticheat.check.packets.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;

public class MultiAura extends PacketCheck {

	public MultiAura() {
		super("Killaura [Multi]", 10, null, false, true, Category.COMBAT,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }, false, 85);
	}

	@Override
	public void listen(PacketEvent e) {

	}

}
