package diplomacy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class JoinRequestMail extends DiploMail {

	private UUID player;

	public JoinRequestMail(String title, UUID sender, Faction recipient) {
		super(title, null, recipient);
		this.player = sender;
	}

	public JoinRequestMail(String title, OfflinePlayer sender, Faction recipient) {
		this(title, sender.getUniqueId(), recipient);
	}

	public JoinRequestMail(Map<String, Object> map) {
		super(map);
		this.player = UUID.fromString((String) map.get("player"));
	}

	public UUID getPlayer() {
		return player;
	}

	public void approve() {
		// Double check they aren't in a faction already
		if (GenericPlugin.getPlayerFaction(getPlayer()) == null) {
			OfflinePlayer offlinePlayer = GenericPlugin.getPlugin().getServer().getOfflinePlayer(getPlayer());
			getRecipient().addPlayer(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				((Player) offlinePlayer)
						.sendMessage("Your request to join " + getRecipient().getName() + " has been approved.");
			}
		}
		// Delete this and all other join requests for this player so that they can't
		// join 2 factions
		for (int i = GenericPlugin.mail.size() - 1; i >= 0; i--) {
			DiploMail mail = GenericPlugin.mail.get(i);
			if (mail instanceof JoinRequestMail && ((JoinRequestMail) mail).getPlayer().equals(this.getPlayer()))
				GenericPlugin.mail.remove(i);
		}
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	public void reject() {
		OfflinePlayer offlinePlayer = GenericPlugin.getPlugin().getServer().getOfflinePlayer(getPlayer());
		if (offlinePlayer.isOnline())
			((Player) offlinePlayer)
					.sendMessage("Your request to join " + getRecipient().getName() + " has been rejected.");
		GenericPlugin.mail.remove(this);
		GenericPlugin.saveData(GenericPlugin.getPlugin());
	}

	public static final String diffKey = "JoinRequestMail";

	@Override
	public String diffKey() {
		return diffKey;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sent", getTimeSent());
		map.put("player", getPlayer().toString());
		map.put("sender", null);
		map.put("recipient", getRecipient().getName());
		map.put("title", getTitle());
		map.put("description", getDescription());
		map.put("diffKey", diffKey());
		return map;
	}

}
