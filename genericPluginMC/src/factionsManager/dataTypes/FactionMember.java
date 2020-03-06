package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.UUID;

public class FactionMember {

	private UUID player;
	private Faction faction;
	private ArrayList<FactionRole> roles;

	public FactionMember(Faction faction, UUID player) {
		this.faction = faction;
		this.player = player;
		this.roles = new ArrayList<FactionRole>();
	}

	public ArrayList<FactionRole> getRoles() {
		return roles;
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

	public FactionRole topRole() {
		return getFaction().topRole(getRoles());
	}

	public boolean canGiveRole() {
		for (FactionRole role : getRoles()) {
			if (role.isRoleGive())
				return true;
		}
		return false;
	}

	public boolean canControlRole() {
		for (FactionRole role : getRoles()) {
			if (role.isRoleControl())
				return true;
		}
		return false;
	}

	public boolean canClaim() {
		for (FactionRole role : getRoles()) {
			if (role.isCanClaim())
				return true;
		}
		return false;
	}
}
