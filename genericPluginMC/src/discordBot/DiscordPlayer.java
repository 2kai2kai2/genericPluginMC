package discordBot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionMember;
import genericPluginMC.GenericPlugin;
import net.dv8tion.jda.api.entities.User;

public class DiscordPlayer implements ConfigurationSerializable {

	private long discordID;
	private UUID MCPlayer;

	public DiscordPlayer(long discordID, UUID minecraftPlayer) {
		this.discordID = discordID;
		this.MCPlayer = minecraftPlayer;
	}
	
	public DiscordPlayer(Map<String, Object> map) {
		this((long) map.get("discord"), UUID.fromString((String) map.get("minecraft")));
	}

	public long getDiscordID() {
		return this.discordID;
	}

	public UUID getMCUUID() {
		return this.MCPlayer;
	}

	public User getDiscordUser() {
		if (Bot.jda != null)
			return Bot.jda.retrieveUserById(getDiscordID()).complete();
		else
			return null;
	}

	public OfflinePlayer getMCOfflinePlayer() {
		return GenericPlugin.getPlugin().getServer().getOfflinePlayer(getMCUUID());
	}
	
	public Faction getFaction() {
		return GenericPlugin.getPlayerFaction(getMCUUID());
	}
	
	public FactionMember getFactionMember() {
		Faction f = getFaction();
		if (f != null)
			return f.getMember(getMCUUID());
		else
			return null;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("discord", getDiscordID());
		map.put("minecraft", getMCUUID().toString());
		return map;
	}
	
	public static DiscordPlayer getDiscordPlayer(long discordID) {
		for (DiscordPlayer d : GenericPlugin.discPlayers) {
			if (d.getDiscordID() == discordID)
				return d;
		}
		return null;
	}
	
	public static DiscordPlayer getDiscordPlayer(UUID MCUUID) {
		for (DiscordPlayer d : GenericPlugin.discPlayers) {
			if (d.getMCUUID().equals(MCUUID))
				return d;
		}
		return null;
	}
	
	public static DiscordPlayer getDiscordPlayer(OfflinePlayer MCPlayer) {
		return getDiscordPlayer(MCPlayer.getUniqueId());
	}
	
	public static DiscordPlayer getDiscordPlayer(User discordUser) {
		return getDiscordPlayer(discordUser.getIdLong());
	}
}
