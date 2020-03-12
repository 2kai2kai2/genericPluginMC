package diplomacy;

import java.util.Map;

import factionsManager.dataTypes.Faction;

public class DiploNotificationMail extends DiploMail {

	public static final String diffKey = "DiploNotificationMail";

	public DiploNotificationMail(String title, Faction sender, Faction recipient, String content) {
		super(title, sender, recipient);
		setDescription(content);
	}

	public DiploNotificationMail(Map<String, Object> map) {
		super(map);
	}
	
	@Override
	public String diffKey() {
		return DiploNotificationMail.diffKey;
	}
}
