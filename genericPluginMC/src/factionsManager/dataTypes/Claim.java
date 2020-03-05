package factionsManager.dataTypes;

import java.util.ArrayList;

import org.bukkit.Chunk;

import genericPluginMC.GenericPlugin;

public class Claim {

	public static Claim getChunkClaim(Chunk chunk) {
		for (Faction f : GenericPlugin.factions) {
			for (Claim c : f.getClaims()) {
				for (Chunk ch : c.getChunks()) {
					if (ch.getX() == chunk.getX() && ch.getZ() == chunk.getZ()
							&& ch.getWorld().getUID().compareTo(chunk.getWorld().getUID()) == 0) {
						return c;
					}
				}
			}
		}
		return null;
	}
	
	private Faction owner;
	private ArrayList<Chunk> chunks;

	public Claim(Faction owner) {
		this.setOwner(owner);
	}

	public ArrayList<Chunk> getChunks() {
		return chunks;
	}

	public boolean addChunk(Chunk chunk) {

		return false;
	}

	public Faction getOwner() {
		return owner;
	}

	public void setOwner(Faction owner) {
		this.owner = owner;
	}
}
