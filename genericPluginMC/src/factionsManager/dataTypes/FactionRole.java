package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import genericPluginMC.GenericPlugin;

public class FactionRole implements ConfigurationSerializable {

	private String prefix;
	private String postfix;
	private String name;
	private boolean leader;
	private ArrayList<Integer> perms;

	public FactionRole(String name, String prefix, String postfix) {
		this.setName(name);
		this.setPrefix(prefix);
		this.setPostfix(postfix);
		this.setLeader(false);
		this.perms = new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public FactionRole(Map<String, Object> map) {
		this((String) map.get("name"), (String) map.get("prefix"), (String) map.get("postfix"));
		this.setLeader((boolean) map.get("leader"));
		this.perms = (ArrayList<Integer>) map.get("perms");
	}

	public FactionRole(String name) {
		this(name, "", "");
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		GenericPlugin.updateDisplayNames();
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
		GenericPlugin.updateDisplayNames();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLeader() {
		return leader;
	}

	public void setLeader(boolean leader) {
		this.leader = leader;
	}

	public boolean hasPerm(RolePerms perm) {
		return this.perms.contains(perm.ordinal());
	}

	public void givePerm(RolePerms perm) {
		if (!hasPerm(perm))
			this.perms.add(perm.ordinal());
	}

	public void removePerm(RolePerms perm) {
		if (hasPerm(perm)) {
			for (int i = 0; i < this.perms.size(); i++) {
				if (this.perms.get(i) == perm.ordinal())
					this.perms.remove(i);
			}
		}
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("prefix", this.getPrefix());
		map.put("postfix", this.getPostfix());
		map.put("name", this.getName());
		map.put("leader", this.isLeader());
		map.put("perms", this.perms);
		return map;
	}
}
