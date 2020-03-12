package diplomacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class War implements ConfigurationSerializable {
	private String name;
	private ArrayList<Faction> attackers;
	private ArrayList<Faction> defenders;

	public War(Faction attacker, Faction defender, String name) {
		attackers = new ArrayList<Faction>();
		attackers.add(attacker);
		defenders = new ArrayList<Faction>();
		defenders.add(defender);
		setName(name);
	}

	@SuppressWarnings("unchecked")
	public War(Map<String, Object> map) {
		this.setName((String) map.get("name"));
		ArrayList<String> attackerNames = (ArrayList<String>) map.get("attackers");
		ArrayList<String> defenderNames = (ArrayList<String>) map.get("defenders");

		for (String aName : attackerNames) {
			Faction faction = GenericPlugin.factionFromName(aName);
			if (faction != null)
				attackers.add(faction);
		}
		for (String dName : defenderNames) {
			Faction faction = GenericPlugin.factionFromName(dName);
			if (faction != null)
				attackers.add(faction);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Faction> getAttackers() {
		return attackers;
	}

	public ArrayList<Faction> getDefenders() {
		return defenders;
	}

	public ArrayList<Faction> getEnemies(Faction faction) {
		if (getAttackers().contains(faction))
			return getDefenders();
		else if (getDefenders().contains(faction))
			return getAttackers();
		else
			return new ArrayList<Faction>();
	}

	public boolean isInvolved(Faction faction) {
		return getAttackers().contains(faction) || getDefenders().contains(faction);
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", getName());
		ArrayList<String> attackerNames = new ArrayList<String>();
		for (Faction f : getAttackers())
			attackerNames.add(f.getName());
		ArrayList<String> defenderNames = new ArrayList<String>();
		for (Faction f : getDefenders())
			defenderNames.add(f.getName());
		map.put("attackers", attackerNames);
		map.put("defenders", defenderNames);
		return map;
	}
}
