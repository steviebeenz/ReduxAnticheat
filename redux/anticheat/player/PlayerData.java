package redux.anticheat.player;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;

import redux.anticheat.Main;
import redux.anticheat.check.packets.combat.aim.AimA.HitData;
import redux.anticheat.check.packets.movement.WeirdY.MoveData;
import redux.anticheat.utils.ReflectionUtils;

public class PlayerData {

	public int packets = 0, swingPackets = 0, attackEntityPackets = 0, blockPlacePacket = 0;
	private int lessTimes = 0;

	private final UUID id;
	private final Player player;
	private boolean alerts = false;
	public Long lastMovement = 0L, lastSwing = 0L, lastAdded = 0L, lastTeleported = 0L, lastFlyingA = 0L, flyingA = 0L;

	private Location lastLocation = null, nextLocation = null;
	private double deltaY, deltaXZ, pDeltaY;

	private String protocolVersion;

	public int violations, sameY, nonDivisible, moreGCD, velocTicks, gcdFlags = 0, hitTicks, aimVl;
	private long lastOnSlime;
	public double onGroundTicks, offGroundTicks, flyB = 0, lastDeltaXZ = 0, lastFlyC;

	public int flyCvl;
	public int nfVl;
	public int nf2vl;
	public boolean isbeingpunished;
	public int blockAboveTicks;
	public int iceTicks;
	public AlertSeverity severity;
	public int flyingInVl;
	public double blockingTicks;
	public double inventoryTicks;
	public double sneakingTicks;
	public int jesus;
	public long join;
	public int badPacketsC;
	public int vehicleTicks;
	public int flyTicks;
	public int flyingInPackets;
	public long resetPacketsD;
	public int deathHits;
	public int nullEntity;
	public ArrayList<HitData> hits = new ArrayList<HitData>();
	public ArrayList<MoveData> moves = new ArrayList<MoveData>();
	public int maxYflag;
	public long lastDamage;
	public long lastVelocity;
	public long lastSentHit;
	public long lastSentLook;
	public int lastSentCount;
	private long delay = 1000, lastAlert = 0;
	public int teleportTicks;
	public int moreInventorySpeed;
	public int flyDvl;
	public boolean isFalling;
	public boolean isRising;
	public int risingTicks;
	public boolean wasFalling;
	public int jumpVl;
	public PacketType lastMovePacket;
	public int patternFall;
	public int lowFlyingInVl;
	public int killauraPackets;
	public int badPacketsE;
	public int movePattern;
	public double curDY;
	public double curDelta;
	public long lastBlockPlace;
	public BlockPlaceData blockPlaceData, lastBlockPlaceData;
	public int eatTicks;
	public int ticksOnLadder;
	public int stairTicks;
	public int jumpStairsTick;
	public int antiCactus;
	public long lastSent;
	public int blockPlace;
	public int position;
	public int positionLook;
	public boolean wasSetBack;
	public int fallingTicks;

	public void delete() {
		hits.clear();
		moves.clear();
		if (Main.getInstance().reduxCommand.debugEnabled.contains(player.getUniqueId())) {
			Main.getInstance().reduxCommand.debugEnabled.remove(player.getUniqueId());
		}
	}

	public PlayerData(Player p) {
		id = p.getUniqueId();
		player = p;
		join = System.currentTimeMillis();

		if (player.hasPermission("redux.alerts")) {
			alerts = true;
			severity = AlertSeverity.LOW;
		}
	}

	public boolean isAlerts() {
		return alerts;
	}

	public void setAlerts(boolean alerts) {
		this.alerts = alerts;
	}

	public Long getLastMovement() {
		return lastMovement;
	}

	public void setLastMovement(Long lastMovement) {
		this.lastMovement = lastMovement;
	}

	public UUID getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public Long getLastSwing() {
		return lastSwing;
	}

	public void setLastSwing(Long lastSwing) {
		this.lastSwing = lastSwing;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public int getViolations() {
		return violations;
	}

	public void setViolations(int violations) {
		this.violations = violations;
	}

	public Long getLastAdded() {
		return lastAdded;
	}

	public void setLastAdded(Long lastAdded) {
		this.lastAdded = lastAdded;
	}

	public Long getLastTeleported() {
		return lastTeleported;
	}

	public void setLastTeleported(Long lastTeleported) {
		this.lastTeleported = lastTeleported;
	}

	public Location getNextLocation() {
		return nextLocation;
	}

	public void setNextLocation(Location nextLocation) {
		this.nextLocation = nextLocation;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public int getPackets() {
		return packets;
	}

	public void resetPackets() {
		packets = 0;
	}

	public void addPacket() {
		packets += 1;
	}

	public void addSwingPackets() {
		swingPackets += 1;
	}

	public int getSwingPackets() {
		return swingPackets;
	}

	public void resetSwingPackets() {
		swingPackets = 0;
	}

	public int getAttackEntityPackets() {
		return attackEntityPackets;
	}

	public void addAttackEntityPacket() {
		attackEntityPackets += 1;
	}

	public void resetAttackEntityPackets() {
		attackEntityPackets = 0;
	}

	public void setPackets(int packets) {
		this.packets = packets;
	}

	public int getLessTimes() {
		return lessTimes;
	}

	public void addLessTimes() {
		lessTimes += 1;
	}

	public void resetLessTimes() {
		lessTimes = 0;
	}

	public void resetAllCounters() {
		resetLessTimes();
		resetAttackEntityPackets();
		resetInteractEntityPackets();
		resetSwingPackets();
		resetPackets();
		resetBlockPlacePacket();
	}

	public void setLastOnSlime(long currentTimeMillis) {
		lastOnSlime = currentTimeMillis;
	}

	public Long getLastOnSlime() {
		return lastOnSlime;
	}

	public double getLastDeltaXZ() {
		return lastDeltaXZ;
	}

	public void setLastDeltaXZ(double lastSpeed) {
		lastDeltaXZ = lastSpeed;
	}

	public double getDeltaXZ() {
		return deltaXZ;
	}

	public void setDeltaXZ(double deltaXZ) {
		this.deltaXZ = deltaXZ;
	}

	public double getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(double deltaY) {
		this.deltaY = deltaY;
	}

	public double getPreviousDeltaY() {
		return pDeltaY;
	}

	public void setPreviousDeltaY(double d) {
		pDeltaY = d;
	}

	public void addPlacePacket() {
		blockPlacePacket += 1;
	}

	public void resetBlockPlacePacket() {
		blockPlacePacket = 0;
	}

	public void setBlockPlacePackets(int blockPlacePacket) {
		this.blockPlacePacket = blockPlacePacket;
	}

	public void addInteractEntityPacket() {

	}

	public void resetInteractEntityPackets() {
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getLastAlert() {
		return lastAlert;
	}

	public void setLastAlert(long currentTimeMillis) {
		this.lastAlert = currentTimeMillis;
	}

	public void setDown() {
		wasSetBack(true);
		
		Block b = Main.getInstance().getLocUtils().getBlockUnder(lastLocation);
		if (b != null && ReflectionUtils.getPing(player) <= 100) {
			if (b.getType().isSolid()) {
				if (!b.getType().name().contains("CACTUS")) {
					Location newLoc = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY() + 1,
							b.getLocation().getZ());
					getPlayer().teleport(newLoc);
					this.setLastLocation(newLoc);
					this.setNextLocation(newLoc);
					teleportTicks = 0;
					return;
				}
			}
		}

		getPlayer().teleport(lastLocation);
		this.setLastLocation(lastLocation);
		this.setNextLocation(lastLocation);

		teleportTicks = 0;
	}

	public void wasSetBack(boolean b) {
		this.wasSetBack = b;
	}

	public double getVelocity() {
		double veloc = 0;
		veloc += getPlayer().getVelocity().getX();
		veloc += getPlayer().getVelocity().getY();
		veloc += getPlayer().getVelocity().getZ();
		return veloc;
	}

}
