package diplomacy;

import java.util.Map;

import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class PeaceOfferMail extends DiploMail {

	public static final String diffKey = "PeaceOfferMail";

	public PeaceOfferMail(Faction sender, Faction recipient) {
		super(sender.getName() + " peace offer to " + recipient.getName(), sender, recipient);
		setDescription(sender.getName() + " wants peace with " + recipient.getName() + ". Shall we accept?");
	}

	public PeaceOfferMail(Map<String, Object> map) {
		super(map);
	}

	public void approve() {
		for (int i = GenericPlugin.wars.size() - 1; i >= 0; i--) {
			War war = GenericPlugin.wars.get(i);
			// Check that they are fighting each other in this war
			if (war.getEnemies(getRecipient()).contains(getSender())) {
				// If both are war leaders then end the war
				if ((war.getAttackers().get(0) == getSender() && war.getDefenders().get(0) == getRecipient())
						|| (war.getAttackers().get(0) == getRecipient() && war.getDefenders().get(0) == getSender())) {
					GenericPlugin.wars.remove(i);
					for (Faction f : war.getAttackers()) {
						getRecipient().sendNotifMail(f, "End of " + war.getName(),
								getRecipient().getName() + " has accepted a peace offer from " + getSender().getName()
										+ ", ending " + war.getName() + ".");
					}
					for (Faction f : war.getDefenders()) {
						getRecipient().sendNotifMail(f, "End of " + war.getName(),
								getRecipient().getName() + " has accepted a peace offer from " + getSender().getName()
										+ ", ending " + war.getName() + ".");
					}
					// Sender is attacker war leader; remove recipient from defenders
				} else if (war.getAttackers().get(0) == getSender() && war.getDefenders().contains(getRecipient())) {
					war.getDefenders().remove(getRecipient());
					for (Faction f : war.getAttackers()) {
						getRecipient().sendNotifMail(f,
								"Peace from " + getRecipient().getName() + " in " + war.getName(),
								getRecipient().getName() + " has accepted a peace offer from " + getSender().getName()
										+ ", making them no longer involved in " + war.getName()
										+ ". However, the war goes on for the rest of us!");
					}
					for (Faction f : war.getDefenders()) {
						getRecipient().sendNotifMail(f,
								"Betrayed by " + getRecipient().getName() + " in " + war.getName(),
								getRecipient().getName() + " has accepted a peace offer from " + getSender().getName()
										+ ", and they have abandoned us in " + war.getName() + ".");
					}

					// Sender is defender war leader; remove recipient from attackers
				} else if (war.getDefenders().get(0) == getSender() && war.getAttackers().contains(getRecipient())) {
					war.getAttackers().remove(getRecipient());
					for (Faction f : war.getAttackers()) {
						getRecipient().sendNotifMail(f,
								"Betrayed by " + getRecipient().getName() + " in " + war.getName(),
								getRecipient().getName() + " has accepted a peace offer from " + getSender().getName()
										+ ", and they have abandoned us in " + war.getName() + ".");
					}
					for (Faction f : war.getDefenders()) {
						getRecipient().sendNotifMail(f,
								"Peace from " + getRecipient().getName() + " in " + war.getName(),
								getRecipient().getName() + " has accepted a peace offer from " + getSender().getName()
										+ ", making them no longer involved in " + war.getName()
										+ ". However, the war goes on for the rest of us!");
					}

					// Recipient is attacker war leader; remove sender from defenders
				} else if (war.getAttackers().get(0) == getRecipient() && war.getDefenders().contains(getSender())) {
					war.getDefenders().remove(getSender());
					for (Faction f : war.getAttackers()) {
						getSender().sendNotifMail(f, "Peace from " + getSender().getName() + " in " + war.getName(),
								getSender().getName() + " has sued for peace from " + getRecipient().getName()
										+ ", making them no longer involved in " + war.getName()
										+ ". However, the war goes on for the rest of us!");
					}
					for (Faction f : war.getDefenders()) {
						getSender().sendNotifMail(f, "Betrayed by " + getSender().getName() + " in " + war.getName(),
								getSender().getName() + " asked for and gotten a peace deal from "
										+ getRecipient().getName() + ", and they have abandoned us in " + war.getName()
										+ ".");
					}

					// Recipient is defender war leader; remove sender from attackers
				} else if (war.getDefenders().get(0) == getRecipient() && war.getAttackers().contains(getSender())) {
					war.getAttackers().remove(getSender());
					for (Faction f : war.getAttackers()) {
						getSender().sendNotifMail(f, "Betrayed by " + getSender().getName() + " in " + war.getName(),
								getSender().getName() + " asked for and gotten a peace deal from "
										+ getRecipient().getName() + ", and they have abandoned us in " + war.getName()
										+ ".");
					}
					for (Faction f : war.getDefenders()) {
						getSender().sendNotifMail(f, "Peace from " + getSender().getName() + " in " + war.getName(),
								getSender().getName() + " has sued for peace from " + getRecipient().getName()
										+ ", making them no longer involved in " + war.getName()
										+ ". However, the war goes on for the rest of us!");
					}

				}
			}
		}
		// Remove all peace offers between these nations
		for (int i = GenericPlugin.mail.size() - 1; i >= 0; i--) {
			DiploMail mail = GenericPlugin.mail.get(i);
			if (mail instanceof PeaceOfferMail) {
				if ((mail.getSender() == this.getSender() && mail.getRecipient() == this.getRecipient())
						|| (mail.getSender() == this.getRecipient() && mail.getRecipient() == this.getSender())) {
					GenericPlugin.mail.remove(i);
				}
			}
		}
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	public void reject() {
		getRecipient().sendNotifMail(getSender(), "Peace offer rejected by " + getRecipient().getName(),
				getRecipient().getName() + " has rejected the very generous peace offer from " + getSender().getName()
						+ ". We must show them the error of their ways.");
		GenericPlugin.mail.remove(this);
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	@Override
	public String diffKey() {
		return diffKey;
	}

}
