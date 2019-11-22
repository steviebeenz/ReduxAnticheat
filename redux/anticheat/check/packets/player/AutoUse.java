package redux.anticheat.check.packets.player;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;

public class AutoUse extends PacketCheck {

	public AutoUse(String name, int minViolations, int maxViolations, List<String> punishments, boolean canLearn,
			boolean enabled, Category category, PacketType[] type, boolean setback, double severity) {
		super(name, minViolations, maxViolations, punishments, canLearn, enabled, category, type, setback, severity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void listen(PacketEvent e) {
		// TODO Auto-generated method stub

	}

}
