package redux.anticheat.check.packets.movement;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import redux.anticheat.Main;
import redux.anticheat.check.Category;
import redux.anticheat.check.PacketCheck;
import redux.anticheat.player.PlayerData;
import redux.anticheat.utils.ReflectionUtils;

public class MoreJump extends PacketCheck {

	public MoreJump() {
		super("MoreJump", 5, 10, null, false, true, Category.MOVEMENT,
				new PacketType[] { PacketType.Play.Client.POSITION }, true, 80);
		this.setDescription("Checks if a player has jump like motions in the air.");
	}

	@Override
	public void listen(PacketEvent e) {
		Player p = e.getPlayer();
		PlayerData pd = Main.getInstance().getPlayerManager().getPlayer(p.getUniqueId());

		boolean onGround = ReflectionUtils.getOnGround(p),
				isReallyOnGround = Main.getInstance().getLocUtils().isOnSolidGround(pd.getNextLocation())
						&& Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation());

		if (System.currentTimeMillis() - pd.join < 1500) {
			return;
		}

		if (pd.getDeltaY() < 0) {
			pd.isFalling = true;
			pd.fallingTicks++;
			if (pd.isRising) {
				pd.isRising = false;
				pd.wasFalling = false;
			}
			pd.lowFlyingInVl = 0;
			if (pd.risingTicks > 0) {
				pd.risingTicks--;
			}
		} else if (pd.getDeltaY() > 0) {
			pd.isRising = true;
			pd.risingTicks++;
			pd.lowFlyingInVl = 0;
			if (pd.wasFalling) {
				pd.wasFalling = false;
			}
			
			if(pd.fallingTicks > 0) {
				pd.fallingTicks--;
			}

			if (pd.isFalling) {
				pd.wasFalling = true;
				pd.isFalling = false;
			}
		} else if (pd.getDeltaY() == 0) {
			pd.isRising = false;
			if (pd.wasFalling) {
				pd.wasFalling = false;
			}
			if (pd.risingTicks > 0) {
				pd.risingTicks--;
			}
			if(pd.fallingTicks > 0) {
				pd.fallingTicks--;
			}
			if (pd.isFalling) {
				pd.wasFalling = true;
				pd.isFalling = false;
				pd.lowFlyingInVl = 0;
			}
		}

		if (pd.flyTicks > 0) {
			pd.lowFlyingInVl = 0;
			pd.isRising = false;
			pd.isFalling = false;
			return;
		}

		if(System.currentTimeMillis() - pd.teleportTicks < 1500) {
			return;
		}
		
		if (pd.teleportTicks > 0) {
			return;
		}

		if (pd.vehicleTicks > 0) {
			return;
		}

		if (Main.getInstance().getLocUtils().canClimb(p) || Main.getInstance().getLocUtils().canClimb(pd.getLastLocation()) || Main.getInstance().getLocUtils().canClimb(pd.getNextLocation())
				|| Main.getInstance().getLocUtils().isInLiquid(pd.getLastLocation()) || Main.getInstance().getLocUtils().isInLiquid(pd.getNextLocation())) {
			return;
		}

		if (System.currentTimeMillis() - pd.getLastOnSlime() < 1500) {
			return;
		}

		if(pd.velocTicks > 0) {
			return;
		}
		
		if(pd.flyTicks > 0 || p.isFlying()) {
			pd.isRising = false;
			pd.isFalling = false;
			pd.wasFalling = false;
			return;
		}
		
		int limit = 20;

		limit += ReflectionUtils.getPingModifier(p);

		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().equals(PotionEffectType.JUMP)) {
				limit *= 1 + (pe.getAmplifier() * 0.2);
			}
		}

		if (!onGround && !isReallyOnGround) {
			if (pd.isRising && pd.offGroundTicks > limit) {
				if (pd.risingTicks > 4) {
					flag(pd, pd.risingTicks + " > 4");
					pd.risingTicks = 0;
				}
			}
		} else {
			if(pd.risingTicks > 0) {
				pd.risingTicks--;
			}
		}

		if (pd.isRising && pd.wasFalling) {
			if (Main.getInstance().getLocUtils().isCollided(pd.getNextLocation(), "AIR") && Main.getInstance().getLocUtils().isCollided(pd.getLastLocation(), "AIR")) {
				if (!Main.getInstance().getLocUtils().isOnSolidGround(pd.getLastLocation())) {
					pd.jumpVl++;
					if (pd.jumpVl > 2) {
						flag(pd, pd.jumpVl + " > " + 2);
						pd.jumpVl = 0;
					}
				} else {
					if (pd.jumpVl > 0) {
						pd.jumpVl--;
					}
				}
			} else {
				if (pd.jumpVl > 0) {
					pd.jumpVl--;
				}
			}
		} else {
			if (pd.jumpVl > 0) {
				pd.jumpVl--;
			}
		}
		

	}

}
