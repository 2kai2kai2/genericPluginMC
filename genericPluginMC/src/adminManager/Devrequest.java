package adminManager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class Devrequest implements ConfigurationSerializable {

	private Claim claim;

	public Devrequest(Claim claim) {
		this.claim = claim;
	}

	public Devrequest(Map<String, Object> map) {
		Faction faction = GenericPlugin.factionFromName((String) map.get("faction"));
		if (faction != null) {
			this.claim = faction.getClaim((String) map.get("claim"));
		}
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("faction", getClaim().getOwner().getName());
		map.put("claim", getClaim().getName());
		return map;
	}

	public Claim getClaim() {
		return claim;
	}
}
