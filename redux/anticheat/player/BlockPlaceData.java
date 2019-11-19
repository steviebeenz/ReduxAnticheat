package redux.anticheat.player;

import org.bukkit.block.Block;

public class BlockPlaceData {
	
	private Long time;
	private Block placed, placedAgainst;
	
	public BlockPlaceData(Long time, Block placed, Block placedAgainst) {
		this.time = time;
		this.placed = placed;
		this.placedAgainst = placedAgainst;
	}

	public Long getTime() {
		return time;
	}

	public Block getPlaced() {
		return placed;
	}

	public Block getPlacedAgainst() {
		return placedAgainst;
	}

}
