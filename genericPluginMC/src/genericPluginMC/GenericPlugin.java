package genericPluginMC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import adminManager.Devrequest;
import adminManager.FAdminCommands;
import adminManager.FAdminTabCompleter;
import adminManager.SpecTP;
import diplomacy.AllyOfferMail;
import diplomacy.DiploCommands;
import diplomacy.DiploMail;
import diplomacy.DiploNotificationMail;
import diplomacy.DiploTabCompleter;
import diplomacy.JoinRequestMail;
import diplomacy.PeaceOfferMail;
import diplomacy.War;
import discordBot.Bot;
import discordBot.DiscordPlayer;
import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.ClaimCommands;
import factionsManager.dataTypes.ClaimTabCompleter;
import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionCommands;
import factionsManager.dataTypes.FactionMember;
import factionsManager.dataTypes.FactionRole;
import factionsManager.dataTypes.FactionTabCompleter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class GenericPlugin extends JavaPlugin {

	public static FileConfiguration config;
	public static FileConfiguration data;
	public static FileConfiguration discord;

	public static ArrayList<Faction> factions;
	public static ArrayList<War> wars;
	public static ArrayList<DiploMail> mail;
	public static ArrayList<Devrequest> devrequests;
	public static ArrayList<Player> claimOverrides;
	public static ArrayList<DiscordPlayer> discPlayers;

	public static ArrayList<SpecTP> adminSpecLocs;

	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;
	public static Logger logger;

	@Override
	public void onEnable() {
		// Vault
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> serviceEcon = getServer().getServicesManager()
					.getRegistration(Economy.class);
			if (serviceEcon != null)
				econ = serviceEcon.getProvider();
			RegisteredServiceProvider<Permission> servicePerms = getServer().getServicesManager()
					.getRegistration(Permission.class);
			if (servicePerms != null)
				perms = servicePerms.getProvider();
			RegisteredServiceProvider<Chat> serviceChat = getServer().getServicesManager().getRegistration(Chat.class);
			if (serviceChat != null)
				chat = serviceChat.getProvider();
		}

		// Serialization
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionRole.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(War.class);

		// Initialize arrays
		factions = new ArrayList<Faction>();
		wars = new ArrayList<War>();
		mail = new ArrayList<DiploMail>();
		devrequests = new ArrayList<Devrequest>();
		claimOverrides = new ArrayList<Player>();

		adminSpecLocs = new ArrayList<SpecTP>();

		logger = this.getLogger();

		// Configs
		config = this.getConfig();
		config.addDefault("allow-wars", true);
		config.addDefault("allow-faction-map", true);
		config.addDefault("chat-include-group", true);
		config.addDefault("discord-token", "null");
		config.options().copyDefaults(true);
		this.saveDefaultConfig();

		this.saveResource("data.yml", false);
		data = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "data.yml"));
		loadData(getPlugin());

		// Events
		getServer().getPluginManager().registerEvents(new Events(), this);

		// Commands
		this.getCommand("faction").setExecutor(new FactionCommands());
		this.getCommand("faction").setTabCompleter(new FactionTabCompleter());
		this.getCommand("claim").setExecutor(new ClaimCommands());
		this.getCommand("claim").setTabCompleter(new ClaimTabCompleter());
		this.getCommand("diplo").setExecutor(new DiploCommands());
		this.getCommand("diplo").setTabCompleter(new DiploTabCompleter());
		this.getCommand("fadmin").setExecutor(new FAdminCommands());
		this.getCommand("fadmin").setTabCompleter(new FAdminTabCompleter());

		// Setup admin faction if doesn't exist
		if (GenericPlugin.factionFromName("admin") == null)
			factions.add(Faction.generateAdminFaction());

		// Discord
		if (Bot.init()) {
			this.saveResource("discord.yml", false);
			discord = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "discord.yml"));
			discPlayers = new ArrayList<DiscordPlayer>();
			loadDiscord();
		}
	}

	@Override
	public void onDisable() {
		saveData(getPlugin());
		for (SpecTP spec : adminSpecLocs) {
			spec.end();
		}
		if (discord != null)
			GenericPlugin.saveDiscord();
	}

	public static JavaPlugin getPlugin() {
		return getPlugin(GenericPlugin.class);
	}

	public static Faction factionFromName(String name) {
		for (Faction f : factions) {
			if (f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		return null;
	}

	public static Faction getPlayerFaction(UUID player) {
		for (Faction f : factions) {
			for (FactionMember m : f.getMembers()) {
				if (player.compareTo(m.getPlayer()) == 0) {
					return f;
				}
			}
		}
		return null;
	}

	public static Faction getPlayerFaction(OfflinePlayer player) {
		return getPlayerFaction(player.getUniqueId());
	}

	public static Claim chunkOwner(Chunk chunk) {
		for (Faction f : factions) {
			for (Claim c : f.getClaims()) {
				if (c.hasChunk(chunk))
					return c;
			}
		}
		return null;
	}

	public static Claim locationOwner(Location loc) {
		for (Faction f : factions) {
			for (Claim cl : f.getClaims()) {
				for (Chunk ch : cl.getChunks()) {
					if (Math.floor(loc.getX() / 16) == ch.getX() && Math.floor(loc.getZ() / 16) == ch.getZ())
						return cl;
				}
			}
		}
		return null;
	}

	public static ArrayList<DiploMail> recievedMail(Faction f) {
		ArrayList<DiploMail> recMail = new ArrayList<DiploMail>();
		for (DiploMail m : mail) {
			if (m.getRecipient() == f)
				recMail.add(m);
		}
		return recMail;
	}

	public static void updateDisplayNames() {
		for (Player player : getPlugin().getServer().getOnlinePlayers()) {
			String displayName = "";

			// Vault Permission info
			if (perms != null && config.getBoolean("chat-include-group") && perms.hasGroupSupport()) {
				try {
					String group = perms.getPrimaryGroup("NULL", player);
					if (group != null) {
						String groupPrefix;
						if (chat == null) {
							groupPrefix = "[" + group + "]";
						} else {
							groupPrefix = chat.getGroupPrefix("NULL", group);
							// Accept '&' color codes
							groupPrefix = groupPrefix.replace('&', ChatColor.COLOR_CHAR);
						}
						displayName = ChatColor.RESET.toString() + groupPrefix + ChatColor.RESET.toString() + " ";
					}
				} catch (Exception e) {
					// perms.getPrimaryGroup() didn't work.
				}
			}

			// Faction info
			Faction faction = getPlayerFaction(player);
			if (faction == null) {
				displayName += ChatColor.GRAY.toString() + player.getName() + " the Homeless"
						+ ChatColor.RESET.toString();
			} else {
				FactionMember member = faction.getMember(player.getUniqueId());
				FactionRole topRole = member.topRole();
				if (topRole == null) // Member has no roles
					displayName += faction.getColor().toString() + player.getName() + " of " + faction.getName()
							+ ChatColor.RESET.toString();
				else
					displayName += faction.getColor().toString() + topRole.getPrefix() + player.getName()
							+ topRole.getPostfix() + ChatColor.RESET.toString();
			}
			player.setDisplayName(displayName);
			player.setPlayerListName(displayName);
		}
	}

	public static void saveData(JavaPlugin p) {
		ArrayList<Map<String, Object>> factionMaps = new ArrayList<Map<String, Object>>();
		for (Faction f : factions)
			factionMaps.add(f.serialize());
		data.set("factions", factionMaps);

		ArrayList<Map<String, Object>> warMaps = new ArrayList<Map<String, Object>>();
		for (War w : wars)
			warMaps.add(w.serialize());
		data.set("wars", warMaps);

		ArrayList<Map<String, Object>> mailMaps = new ArrayList<Map<String, Object>>();
		for (DiploMail m : mail)
			mailMaps.add(m.serialize());
		data.set("mail", mailMaps);

		ArrayList<Map<String, Object>> devMaps = new ArrayList<Map<String, Object>>();
		for (Devrequest d : devrequests)
			devMaps.add(d.serialize());
		data.set("devrequests", devMaps);

		try {
			data.save(new File(p.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static void loadData(JavaPlugin p) {
		ArrayList<Map<String, Object>> factionMaps = (ArrayList<Map<String, Object>>) data.get("factions");
		if (factionMaps != null) {
			for (Map<String, Object> map : factionMaps) {
				factions.add(new Faction(map));
			}
		}

		ArrayList<Map<String, Object>> warMaps = (ArrayList<Map<String, Object>>) data.get("wars");
		if (warMaps != null) {
			for (Map<String, Object> map : warMaps) {
				wars.add(new War(map));
			}
		}

		ArrayList<Map<String, Object>> mailMaps = (ArrayList<Map<String, Object>>) data.get("mail");
		if (mailMaps != null) {
			for (Map<String, Object> map : mailMaps) {
				String diffKey = (String) map.get("diffKey");
				if (diffKey.equals(AllyOfferMail.diffKey))
					mail.add(new AllyOfferMail(map));
				else if (diffKey.equals(DiploNotificationMail.diffKey))
					mail.add(new DiploNotificationMail(map));
				else if (diffKey.equals(PeaceOfferMail.diffKey))
					mail.add(new PeaceOfferMail(map));
				else if (diffKey.equals(JoinRequestMail.diffKey))
					mail.add(new JoinRequestMail(map));
			}
		}

		ArrayList<Map<String, Object>> devMaps = (ArrayList<Map<String, Object>>) data.get("devrequests");
		if (devMaps != null) {
			for (Map<String, Object> map : devMaps) {
				devrequests.add(new Devrequest(map));
			}
		}
	}

	public static void saveDiscord() {
		ArrayList<Map<String, Object>> playerMaps = new ArrayList<Map<String, Object>>();
		for (DiscordPlayer player : discPlayers) {
			playerMaps.add(player.serialize());
		}
		discord.set("players", playerMaps);

		try {
			discord.save(new File(getPlugin().getDataFolder(), "discord.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadDiscord() {
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, Object>> playerMaps = (ArrayList<Map<String, Object>>) discord.get("players");
		if (playerMaps != null) {
			for (Map<String, Object> map : playerMaps) {
				discPlayers.add(new DiscordPlayer(map));
			}
		}
	}
}
