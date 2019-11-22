package redux.anticheat.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.check.events.UpdatePackets;
import redux.anticheat.check.packets.combat.Killaura;
import redux.anticheat.check.packets.combat.KillauraB;
import redux.anticheat.check.packets.combat.KillauraC;
import redux.anticheat.check.packets.combat.KillauraD;
import redux.anticheat.check.packets.combat.NoSwing;
import redux.anticheat.check.packets.combat.Reach;
import redux.anticheat.check.packets.combat.aim.AimA;
import redux.anticheat.check.packets.combat.aim.AimB;
import redux.anticheat.check.packets.combat.aim.AimC;
import redux.anticheat.check.packets.movement.FastClimb;
import redux.anticheat.check.packets.movement.FastStairs;
import redux.anticheat.check.packets.movement.Fly;
import redux.anticheat.check.packets.movement.FlyB;
import redux.anticheat.check.packets.movement.FlyC;
import redux.anticheat.check.packets.movement.FlyD;
import redux.anticheat.check.packets.movement.FlyE;
import redux.anticheat.check.packets.movement.FlyF;
import redux.anticheat.check.packets.movement.FlyH;
import redux.anticheat.check.packets.movement.Glide;
import redux.anticheat.check.packets.movement.Jesus;
import redux.anticheat.check.packets.movement.LessY;
import redux.anticheat.check.packets.movement.MoreJump;
import redux.anticheat.check.packets.movement.MovePattern;
import redux.anticheat.check.packets.movement.NoSlow;
import redux.anticheat.check.packets.movement.Nofall;
import redux.anticheat.check.packets.movement.NofallB;
import redux.anticheat.check.packets.movement.NormalMovements;
import redux.anticheat.check.packets.movement.Speed;
import redux.anticheat.check.packets.movement.SpeedB;
import redux.anticheat.check.packets.movement.SpeedC;
import redux.anticheat.check.packets.movement.WeirdY;
import redux.anticheat.check.packets.packet.BadPacketsA;
import redux.anticheat.check.packets.packet.BadPacketsB;
import redux.anticheat.check.packets.packet.BadPacketsC;
import redux.anticheat.check.packets.packet.BadPacketsD;
import redux.anticheat.check.packets.packet.BadPacketsE;
import redux.anticheat.check.packets.packet.BadPacketsF;
import redux.anticheat.check.packets.packet.BadPacketsG;
import redux.anticheat.check.packets.packet.BadPacketsH;
import redux.anticheat.check.packets.player.Scaffold;
import redux.anticheat.player.PlayerData;

public class CheckManager {

	private final List<PacketCheck> checks;
	final private boolean slowServer;

	// Try to minimise impact.
	public CheckManager(boolean slowServer) {
		this.slowServer = slowServer;
		checks = new ArrayList<>();
		final UpdatePackets updates = new UpdatePackets();
		updates.listen();
		final Killaura a = new Killaura();
		final Speed b = new Speed();
		final Reach d = new Reach();
		final BadPacketsA e = new BadPacketsA();
		final KillauraB f = new KillauraB();
		final KillauraC g = new KillauraC();
		final Fly h = new Fly();
		final FlyB i = new FlyB();
		final KillauraD j = new KillauraD();
		final NoSwing l = new NoSwing();
		final AimA m = new AimA();
		final BadPacketsB n = new BadPacketsB();
		final FlyE o = new FlyE();
		final FlyC p = new FlyC();
		final Nofall q = new Nofall();
		final BadPacketsD r = new BadPacketsD();
		final NoSlow s = new NoSlow();
		final Jesus u = new Jesus();
		final BadPacketsC v = new BadPacketsC();
		final WeirdY y = new WeirdY();
		final AimB z = new AimB();
		final FlyD aa = new FlyD();
		final MoreJump ab = new MoreJump();
		final NofallB ac = new NofallB();
		final BadPacketsE k = new BadPacketsE();
		final LessY x = new LessY();
		final MovePattern ad = new MovePattern();
		final FlyF ae = new FlyF();
		final SpeedB af = new SpeedB();
		final Scaffold ag = new Scaffold();
		final BadPacketsF ah = new BadPacketsF();
		final FastClimb c = new FastClimb();
		final BadPacketsG ai = new BadPacketsG();
		final BadPacketsH aj = new BadPacketsH();
		final FastStairs ak = new FastStairs();
		final NormalMovements al = new NormalMovements();
		final FlyH am = new FlyH();
		final Glide an = new Glide();
		final SpeedC ao = new SpeedC();
		final AimC ap = new AimC();
		
		checks.add(a);
		checks.add(b);
		checks.add(c);
		checks.add(d);
		checks.add(e);
		checks.add(f);
		checks.add(g);
		checks.add(h);
		checks.add(i);
		checks.add(j);
		checks.add(l);
		checks.add(m);
		checks.add(n);
		checks.add(o);
		checks.add(p);
		checks.add(q);
		checks.add(r);
		checks.add(s);
		checks.add(u);
		checks.add(v);
		checks.add(y);
		checks.add(z);
		checks.add(aa);
		checks.add(ab);
		checks.add(ac);
		checks.add(k);
		checks.add(x);
		checks.add(ad);
		checks.add(ae);
		checks.add(af);
		checks.add(ag);
		checks.add(ah);
		checks.add(ai);
		checks.add(aj);
		checks.add(ak);
		checks.add(al);
		checks.add(ah);
		checks.add(am);
		checks.add(an);
		checks.add(ao);
		checks.add(ap);

		loadCheckFiles();
	}

	private void loadCheckFiles() {
		try {
			for (Category c : Category.values()) {
				File dir = new File(Main.getInstance().getDataFolder() + File.separator + "checks" + File.separator
						+ c.name().toLowerCase());
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}

			for (PacketCheck c : checks) {
				File dir = new File(Main.getInstance().getDataFolder() + File.separator + "checks" + File.separator
						+ c.getCategory().name().toLowerCase(), c.getName().toLowerCase() + ".yml");
				YamlConfiguration yml;
				if (!dir.exists()) {
					dir.createNewFile();

					yml = YamlConfiguration.loadConfiguration(dir);
					yml.set("category", c.getCategory().name());
					yml.set("enabled", c.isEnabled());
					yml.set("maxvl", c.getMaxViolations());
					yml.set("minvl", c.getMinViolations());
					yml.set("setback", c.shouldSetback());
					yml.set("severity", c.getSeverity());
					yml.save(dir);
				} else {
					yml = YamlConfiguration.loadConfiguration(dir);
					try {
						c.setEnabled(yml.getBoolean("enabled"));
						c.setCategory(Category.valueOf(yml.getString("category")));
						c.setMaxViolations(yml.getInt("maxvl"));
						c.setMinViolations(yml.getInt("minvl"));
						c.setSetback(yml.getBoolean("setback"));
						c.setSeverity(yml.getDouble("severity") > 100 ? 100 : yml.getDouble("severity"));
					} catch (Exception e) {
						yml.set("category", c.getCategory().name());
						yml.set("enabled", c.isEnabled());
						yml.set("maxvl", c.getMaxViolations());
						yml.set("minvl", c.getMinViolations());
						yml.set("severity", c.getSeverity());
						yml.save(dir);
						return;
					}
				}

				c.loadCustomSettings();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runCheck(PacketEvent e) {
		for (final PacketCheck c : checks) {
			if (c.isEnabled()) {
				for (final PacketType pt : c.getType()) {
					if (pt.equals(e.getPacketType())) {
						PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
						if (pd != null) {
							c.listen(e);
						}
					}
				}
			}
		}
	}

	public void disable() {
		if (!checks.isEmpty()) {
			for (final PacketCheck c : checks) {
				if (c.isEnabled() && c.canLearn()) {
					c.saveData();
				}
			}
		}

		checks.clear();
	}

	public List<PacketCheck> getChecks() {
		return checks;
	}

	public boolean isSlowServer() {
		return slowServer;
	}

	public int getChecks(Category ca) {
		int tr = 0;
		for(PacketCheck c : checks) {
			if(c.getCategory().equals(ca)) {
				tr++;
			}
		}
		return tr;
	}
	
	public List<PacketCheck> get(Category ca) {
		ArrayList<PacketCheck> check = new ArrayList<PacketCheck>();
		for(PacketCheck c : checks) {
			if(c.getCategory().equals(ca)) {
				check.add(c);
			}
		}
		
		Collections.sort(check, new Comparator<PacketCheck>() {
            @Override
            public int compare(PacketCheck item, PacketCheck t1) {
                String s1 = item.getName();
                String s2 = t1.getName();
                return s1.compareToIgnoreCase(s2);
            }

        });
		return check;
	}

}
