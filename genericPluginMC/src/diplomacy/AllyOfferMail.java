package diplomacy;

import java.util.Map;

import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class AllyOfferMail extends DiploMail {

	public AllyOfferMail(Faction sender, Faction recipient) {
		super("Alliance offer from " + sender.getName(), sender, recipient);
		setDescription(sender.getName() + " has sent us an offer to form an alliance.");
	}

	public AllyOfferMail(Map<String, Object> map) {
		super(map);
	}

	@Override
	public void approve() {
		getSender().addAlly(getRecipient());
		getSender().sendNotifMail(getRecipient(), "Alliance formed",
				"An alliance has been formed between " + getRecipient().getName() + " and " + getSender().getName());
		getRecipient().addAlly(getSender());
		getRecipient().sendNotifMail(getSender(), "Alliance formed",
				"An alliance has been formed between " + getSender().getName() + " and " + getRecipient().getName());
		GenericPlugin.mail.remove(this);
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	@Override
	public void reject() {
		getRecipient().sendNotifMail(getSender(), "Alliance rejection",
				getRecipient().getName() + " has rejected the alliance offer from " + getSender().getName() + ".");
		GenericPlugin.mail.remove(this);
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	public static final String diffKey = "AllyOfferMail";

	@Override
	public String diffKey() {
		return AllyOfferMail.diffKey;
	}
}
