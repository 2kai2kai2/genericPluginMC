package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import genericPluginMC.GenericPlugin;

public class Claim implements ConfigurationSerializable {

	private int devLevel;
	private String name;

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

	public Claim(Faction owner, String name, int dev) {
		this.setOwner(owner);
		this.setName(name);
		this.setDevLevel(dev);
		this.chunks = new ArrayList<Chunk>();
	}

	public Claim(Faction owner, String name) {
		this(owner, name, 0);
	}

	@SuppressWarnings("unchecked")
	public Claim(Map<String, Object> map, Faction f) {
		this(f, (String) map.get("name"), (int) map.get("devLevel"));
		for (HashMap<String, Object> cMap : (ArrayList<HashMap<String, Object>>) map.get("chunks")) {
			this.chunks.add(GenericPlugin.getPlugin().getServer().getWorld(UUID.fromString((String) cMap.get("world")))
					.getChunkAt((int) cMap.get("x"), (int) cMap.get("z")));
		}
	}

	public ArrayList<Chunk> getChunks() {
		return chunks;
	}

	public boolean addChunk(Chunk chunk) {
		// Check if it's already claimed
		for (Faction f : GenericPlugin.factions) {
			for (Claim claim : f.getClaims()) {
				if (claim.hasChunk(chunk)) {
					return false; // This means it's already claimed
				}
			}
		}
		// If not, then do it
		getChunks().add(chunk);
		return true;
	}

	public boolean removeChunk(Chunk chunk) {
		if (hasChunk(chunk)) {
			for (int i = 0; i < getChunks().size(); i++) {
				Chunk c = getChunks().get(i);
				if (chunksEqual(c, chunk)) {
					getChunks().remove(c);
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasChunk(Chunk chunk) {
		for (Chunk c : getChunks()) {
			if (chunksEqual(c, chunk))
				return true;
		}
		return false;
	}

	public boolean hasNeighboringChunk(Chunk chunk) {
		int x = chunk.getX();
		int z = chunk.getZ();
		return hasChunk(chunk.getWorld().getChunkAt(x + 1, z)) || hasChunk(chunk.getWorld().getChunkAt(x - 1, z))
				|| hasChunk(chunk.getWorld().getChunkAt(x, z + 1)) || hasChunk(chunk.getWorld().getChunkAt(x, z - 1));
	}

	public Faction getOwner() {
		return owner;
	}

	public void setOwner(Faction owner) {
		this.owner = owner;
	}

	public static boolean chunksEqual(Chunk a, Chunk b) {
		return a.getWorld().getUID().compareTo(b.getWorld().getUID()) == 0 && a.getX() == b.getX()
				&& a.getZ() == b.getZ();
	}

	public int getDevLevel() {
		return devLevel;
	}

	public void setDevLevel(int devLevel) {
		this.devLevel = devLevel;
	}

	public int maxChunks() {
		// Currently it's an initial 32 plus 32 per level of development where free
		// claims are given same as level 1
		return Math.min(1, getDevLevel()) * 32 + 32; // TODO: Balance this and add config
	}

	public int numChunks() {
		return getChunks().size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", this.getName());
		map.put("devLevel", this.getDevLevel());
		map.put("faction", this.getOwner().getName());
		ArrayList<Map<String, Object>> chunkMaps = new ArrayList<Map<String, Object>>();
		for (Chunk c : this.getChunks()) {
			HashMap<String, Object> cMap = new HashMap<String, Object>();
			cMap.put("x", c.getX());
			cMap.put("z", c.getZ());
			cMap.put("world", c.getWorld().getUID().toString());
			chunkMaps.add(cMap);
		}
		map.put("chunks", chunkMaps);
		return map;
	}
}
