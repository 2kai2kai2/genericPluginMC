package factionsManager.dataTypes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import diplomacy.DiploNotificationMail;
import diplomacy.War;
import genericPluginMC.GenericPlugin;

public class Faction implements ConfigurationSerializable {

	private String name;
	private ArrayList<FactionMember> members;
	private ArrayList<FactionRole> roles;
	private ArrayList<Claim> claims;
	private ArrayList<Faction> allies;
	private ChatColor color;

	public Faction(UUID leader, String name) {
		this.setName(name);
		members = new ArrayList<FactionMember>();
		roles = new ArrayList<FactionRole>();
		allies = new ArrayList<Faction>();
		FactionRole leaderRole = new FactionRole("Leader", "Leader ", " of " + name);
		leaderRole.setLeader(true);
		roles.add(leaderRole);
		roles.add(new FactionRole("Citizen", "", " of " + name));
		FactionMember leaderMember = new FactionMember(this, leader);
		leaderMember.getRoles().add(roles.get(0));
		members.add(leaderMember);
		this.claims = new ArrayList<Claim>();
		this.setColor(ChatColor.WHITE);
	}

	/**
	 * The admin Faction constructor-- should not be used for other purposes.
	 */
	private Faction() {
		this.setName("admin");
		this.members = new ArrayList<FactionMember>();
		this.roles = new ArrayList<FactionRole>();
		this.allies = new ArrayList<Faction>();
		this.claims = new ArrayList<Claim>();
		this.setColor(ChatColor.DARK_PURPLE);
	}

	public static Faction generateAdminFaction() {
		if (GenericPlugin.factionFromName("admin") != null)
			return GenericPlugin.factionFromName("admin");
		else
			return new Faction();
	}

	@SuppressWarnings("unchecked")
	public Faction(Map<String, Object> map) {
		this.setName((String) map.get("name"));
		this.roles = new ArrayList<FactionRole>();
		for (Map<String, Object> roleMap : (ArrayList<Map<String, Object>>) map.get("roles")) {
			this.roles.add(new FactionRole(roleMap));
		}
		this.members = new ArrayList<FactionMember>();
		for (Map<String, Object> memberMap : (ArrayList<Map<String, Object>>) map.get("members")) {
			this.members.add(new FactionMember(memberMap, this));
		}
		this.claims = new ArrayList<Claim>();
		for (Map<String, Object> claimMap : (ArrayList<Map<String, Object>>) map.get("claims")) {
			this.claims.add(new Claim(claimMap, this));
		}
		this.allies = new ArrayList<Faction>();
		for (String allyName : (ArrayList<String>) map.get("allies")) {
			Faction ally = GenericPlugin.factionFromName(allyName);
			if (ally != null) {
				// That faction has already been processed on the list but hasn't added this one
				// as an ally yet because this faction hadn't been loaded yet.
				this.addAlly(ally);
				ally.addAlly(this);
			}
			// else-- That faction hasn't been already loaded so the alliances will be added
			// once it has so that both will be loaded when the alliances are added.
		}
		try {
			this.setColor(ChatColor.valueOf((String) map.get("color")));
		} catch (NullPointerException | IllegalArgumentException e) {
			if (this.getName().equals("admin"))
				this.setColor(ChatColor.DARK_PURPLE);
			else
				this.setColor(ChatColor.WHITE);
		}
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return getColor().toString() + getName() + ChatColor.RESET.toString();
	}

	public void setName(String name) {
		this.name = name;
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

	public FactionMember getMember(String name) {
		for (FactionMember m : getMembers()) {
			if (m.getOfflinePlayer().getName().equalsIgnoreCase(name))
				return m;
		}
		return null;
	}

	public void addPlayer(OfflinePlayer p) {
		if (getMember(p.getUniqueId()) == null) {
			FactionMember member = new FactionMember(this, p.getUniqueId());
			member.addRole(getRoles().get(getRoles().size() - 1));
			getMembers().add(member);
		}
	}

	public void removePlayer(OfflinePlayer p) {
		getMembers().remove(getMember(p.getUniqueId()));
	}

	public boolean hasMemberOnline() {
		for (Player p : GenericPlugin.getPlugin().getServer().getOnlinePlayers()) {
			if (getMember(p.getUniqueId()) != null)
				return true;
		}
		return false;
	}

	public ArrayList<FactionRole> getRoles() {
		return roles;
	}

	public FactionRole getRole(String name) {
		for (FactionRole role : getRoles()) {
			if (role.getName().equalsIgnoreCase(name))
				return role;
		}
		return null;
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

	public Claim getClaim(String name) {
		for (Claim c : getClaims()) {
			if (c.getName().equalsIgnoreCase(name))
				return c;
		}
		return null;
	}

	public int maxFreeClaims() {
		return (int) (1.5 * Math.sqrt(getMembers().size()));
		/*
		 * 1 -> 1; 2 -> 2; 4 -> 3; 8 -> 4; 12-> 5; 16-> 6; 22-> 7; etc. increasing over
		 * time the number of players needed to gain another free claim.
		 */
	}

	public int usedFreeClaims() {
		int num = 0;
		for (Claim c : getClaims()) {
			if (c.getDevLevel() == 0)
				num++;
		}
		return num;
	}

	public ArrayList<War> getWars() {
		ArrayList<War> wars = new ArrayList<War>();
		for (War w : GenericPlugin.wars) {
			if (w.isInvolved(this)) {
				wars.add(w);
			}
		}
		return wars;
	}

	public ArrayList<Faction> getWarEnemies() {
		ArrayList<Faction> enemies = new ArrayList<Faction>();
		for (War w : GenericPlugin.wars) {
			enemies.addAll(w.getEnemies(this));
		}
		return enemies;
	}

	public boolean canPlayerModify(Player p) {
		Faction f = GenericPlugin.getPlayerFaction(p);
		return f == this || (this.getWarEnemies().contains(f) && this.hasMemberOnline())
				|| GenericPlugin.claimOverrides.contains(p);
	}

	public ArrayList<Faction> getAllies() {
		return allies;
	}

	public void addAlly(Faction ally) {
		if (!getAllies().contains(ally))
			getAllies().add(ally);
	}

	public ChatColor getColor() {
		return color;
	}

	public Color getRGBColor() {
		if (getColor().equals(ChatColor.BLACK)) {
			return new Color(0, 0, 0);
		} else if (getColor().equals(ChatColor.DARK_BLUE)) {
			return new Color(0, 0, 170);
		} else if (getColor().equals(ChatColor.DARK_GREEN)) {
			return new Color(0, 170, 0);
		} else if (getColor().equals(ChatColor.DARK_AQUA)) {
			return new Color(0, 170, 170);
		} else if (getColor().equals(ChatColor.DARK_RED)) {
			return new Color(170, 0, 0);
		} else if (getColor().equals(ChatColor.DARK_PURPLE)) {
			return new Color(170, 0, 170);
		} else if (getColor().equals(ChatColor.GOLD)) {
			return new Color(255, 170, 0);
		} else if (getColor().equals(ChatColor.GRAY)) {
			return new Color(170, 170, 170);
		} else if (getColor().equals(ChatColor.DARK_GRAY)) {
			return new Color(85, 85, 85);
		} else if (getColor().equals(ChatColor.BLUE)) {
			return new Color(85, 85, 255);
		} else if (getColor().equals(ChatColor.GREEN)) {
			return new Color(85, 255, 85);
		} else if (getColor().equals(ChatColor.AQUA)) {
			return new Color(85, 255, 255);
		} else if (getColor().equals(ChatColor.RED)) {
			return new Color(255, 85, 85);
		} else if (getColor().equals(ChatColor.LIGHT_PURPLE)) {
			return new Color(255, 85, 255);
		} else if (getColor().equals(ChatColor.YELLOW)) {
			return new Color(255, 255, 85);
		} else {
			return new Color(255, 255, 255);
		}
	}

	public void setColor(ChatColor color) {
		this.color = color;
	}

	public void sendNotifMail(Faction recipient, String title, String message) {
		GenericPlugin.mail.add(new DiploNotificationMail(title, this, recipient, message));
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);

		ArrayList<Map<String, Object>> memberMaps = new ArrayList<Map<String, Object>>();
		for (FactionMember member : getMembers()) {
			memberMaps.add(member.serialize());
		}
		map.put("members", memberMaps);

		ArrayList<Map<String, Object>> roleMaps = new ArrayList<Map<String, Object>>();
		for (FactionRole role : getRoles()) {
			roleMaps.add(role.serialize());
		}
		map.put("roles", roleMaps);

		ArrayList<Map<String, Object>> claimMaps = new ArrayList<Map<String, Object>>();
		for (Claim claim : getClaims()) {
			claimMaps.add(claim.serialize());
		}
		map.put("claims", claimMaps);

		ArrayList<String> allyList = new ArrayList<String>();
		for (Faction faction : getAllies()) {
			allyList.add(faction.getName());
		}
		map.put("allies", allyList);

		map.put("color", getColor().name());

		return map;
	}
}
