package redux.anticheat.tasks;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.scheduler.BukkitRunnable;

public class TpsTask extends BukkitRunnable {

	public double tps;
	public long sec;
	public long curSec;
	public int ticks;
	public int delay;

	@Override
	public void run() {
		sec = (System.currentTimeMillis() / 1000);

		if (curSec == sec) {
			ticks++;
		} else {
			curSec = sec;
			tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
			ticks = 0;

			if ((++delay % 300) == 0) {
				delay = 0;
			}
		}

	}

	public double roundTPS() {
		BigDecimal round = new BigDecimal(tps);
		round = round.setScale(2, RoundingMode.CEILING);
		return round.doubleValue();
	}

}
