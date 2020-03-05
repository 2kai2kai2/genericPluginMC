package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Faction {
	
	private String name;
	private ArrayList<FactionMember> members;
	private ArrayList<FactionRole> roles;
	private ArrayList<Claim> claims;
	
	public Faction(UUID leader, String name) {
		this.setName(name);
		members = new ArrayList<FactionMember>();
		roles = new ArrayList<FactionRole>();
		roles.add(new FactionRole("Leader", "Leader ", " of " + name));
		roles.add(new FactionRole("Citizen", "", " of " + name));
		FactionMember leaderMember = new FactionMember(this, leader);
		leaderMember.getRoles().add(roles.get(0));
		members.add(leaderMember);
	}
	
	public ArrayList<FactionMember> getMembers() {
		return members;
	}
	
	public FactionMember getMember(UUID uuid) {
		for (FactionMember m : getMembers()) {
			if (m.getPlayer().compareTo(uuid) == 0) {
				return m;
			}
		}
		return null;
	}
	
	public ArrayList<FactionRole> getRoles() {
		return roles;
	}
	
	public FactionRole topRole(ArrayList<FactionRole> roleList) {
		for (FactionRole role : getRoles()) {
			if (roleList.contains(role)) {
				return role;
			}
		}
		return null;
	}

	public ArrayList<Claim> getClaims() {
		return claims;
	}
	
	public void addPlayer(Player p) {
		if (getMember(p.getUniqueId()) == null) {
			FactionMember member = new FactionMember(this, p.getUniqueId());
			member.addRole(getRoles().get(getRoles().size() - 1));
			getMembers().add(member);
		}
	}
	
	public void removePlayer(Player p) {
		getMembers().remove(getMember(p.getUniqueId()));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
