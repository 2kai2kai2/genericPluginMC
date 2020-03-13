package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class FactionMember implements ConfigurationSerializable {

	private UUID player;
	private Faction faction;
	private ArrayList<FactionRole> roles;

	public FactionMember(Faction faction, UUID player) {
		this.faction = faction;
		this.player = player;
		this.roles = new ArrayList<FactionRole>();
	}

	@SuppressWarnings("unchecked")
	public FactionMember(Map<String, Object> map, Faction f) {
		this(f, UUID.fromString((String) map.get("uuid")));
		for (String roleName : (ArrayList<String>) map.get("roles")) {
			this.addRole(f.getRole(roleName));
		}
	}

	public ArrayList<FactionRole> getRoles() {
		return roles;
	}

	public FactionRole topRole() {
		return getFaction().topRole(getRoles());
	}

	public boolean addRole(FactionRole role) {
		if (!getRoles().contains(role)) {
			roles.add(role);
			return true;
		}
		return false;
	}

	public boolean removeRole(FactionRole role) {
		if (getRoles().contains(role)) {
			roles.remove(role);
			return true;
		}
		return false;
	}

	public UUID getPlayer() {
		return player;
	}

	public Faction getFaction() {
		return faction;
	}

	public boolean hasPerm(RolePerms perm) {
		for (FactionRole role : getRoles()) {
			if (role.hasPerm(perm) || role.isLeader())
				return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", this.getPlayer().toString());
		map.put("faction", this.getFaction().getName());
		ArrayList<String> roleList = new ArrayList<String>();
		for (FactionRole r : this.getRoles()) {
			roleList.add(r.getName());
		}
		map.put("roles", roleList);
		return map;
	}
}
