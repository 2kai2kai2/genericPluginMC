package factionsManager.dataTypes;

public class FactionRole {

	private String prefix;
	private String postfix;
	private String name;
	private boolean leader;
	private boolean roleControl;
	private boolean canClaim;
	private boolean roleGive;

	public FactionRole(String name, String prefix, String postfix) {
		this.setName(name);
		this.setPrefix(prefix);
		this.setPostfix(postfix);
		this.setLeader(false);
		this.setRoleControl(false);
		this.setRoleGive(false);
		this.setCanClaim(false);
	}

	public FactionRole(String name) {
		this(name, "", "");
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRoleControl() {
		return roleControl;
	}

	public void setRoleControl(boolean roleControl) {
		this.roleControl = roleControl;
	}

	public boolean isLeader() {
		return leader;
	}

	public void setLeader(boolean leader) {
		this.leader = leader;
		this.setCanClaim(true);
		this.setRoleControl(true);
		this.setRoleGive(true);
	}

	public boolean isCanClaim() {
		return canClaim;
	}

	public void setCanClaim(boolean canClaim) {
		this.canClaim = canClaim;
	}

	public boolean isRoleGive() {
		return roleGive;
	}

	public void setRoleGive(boolean roleGive) {
		this.roleGive = roleGive;
	}
}
