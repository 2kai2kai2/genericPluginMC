package factionsManager.dataTypes;

public class FactionRole {
	
	private String prefix;
	private String postfix;
	private String name;
	
	public FactionRole(String name, String prefix, String postfix) {
		this.setName(name);
		this.prefix = prefix;
		this.setPostfix(postfix);
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
}
