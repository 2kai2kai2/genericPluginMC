package diplomacy;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public abstract class DiploMail implements ConfigurationSerializable {

	private long timeSent;
	private String title;
	private Faction sender;
	private Faction recipient;
	private String description;

	public DiploMail(String title, Faction sender, Faction recipient) {
		this.timeSent = System.currentTimeMillis();
		this.title = title;
		this.sender = sender;
		this.recipient = recipient;
	}

	public DiploMail(Map<String, Object> map) {
		this((String) map.get("title"), GenericPlugin.factionFromName((String) map.get("sender")),
				GenericPlugin.factionFromName((String) map.get("recipient")));
		this.timeSent = (long) map.get("sent");
		setDescription((String) map.get("description"));
	}

	public long getTimeSent() {
		return timeSent;
	}

	public String getTitle() {
		return title;
	}

	public Faction getSender() {
		return sender;
	}

	public Faction getRecipient() {
		return recipient;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void approve() {
		GenericPlugin.mail.remove(this);
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	public void reject() {
		GenericPlugin.mail.remove(this);
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	public abstract String diffKey();

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sent", getTimeSent());
		map.put("sender", getSender().getName());
		map.put("recipient", getRecipient().getName());
		map.put("title", getTitle());
		map.put("description", getDescription());
		map.put("diffKey", diffKey());
		return map;
	}
}