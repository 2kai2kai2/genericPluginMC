package discordBot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import javax.security.auth.login.LoginException;

import org.bukkit.OfflinePlayer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Bot {

	public static HashMap<Faction, Role> factionRoles = new HashMap<Faction, Role>();

	public static JDA jda;

	public static boolean init() {
		String token = GenericPlugin.config.getString("discord-token");
		if (token != null && !token.equalsIgnoreCase("null")) {
			try {
				jda = new JDABuilder(token).setEventManager(new AnnotatedEventManager()).addEventListeners(new Bot())
						.build();
				return true;
			} catch (LoginException e) {
				GenericPlugin.logger.log(Level.WARNING, "Invalid discord token. Disabling Discord integration.");
				return false;
			}
		} else {
			GenericPlugin.logger.log(Level.INFO, "No discord token provided. Disabling discord integration.");
			return false;
		}
	}

	public static Guild guild() {
		if (jda.getGuilds().size() > 0)
			return jda.getGuilds().get(0);
		else
			return null;
	}

	public static void updateFactionRoles() {
		if (guild() != null) {
			// Remove all roles that no longer have a valid faction
			ArrayList<Faction> toRemove = new ArrayList<Faction>();
			for (Entry<Faction, Role> entry : factionRoles.entrySet()) {
				if (!GenericPlugin.factions.contains(entry.getKey())) {
					toRemove.add(entry.getKey());
				}
			}
			for (Faction f : toRemove) {
				factionRoles.get(f).delete().queue();
				factionRoles.remove(f);
			}

			// Add all roles that need to be added to either
			for (Faction f : GenericPlugin.factions) {
				if (!f.getName().equalsIgnoreCase("admin")) {
					List<Role> possible = guild().getRolesByName(f.getName(), true);
					if (possible.size() == 0) {
						// There is no role of this name on discord; add it
						factionRoles.put(f, guild().createRole().setName(f.getName()).setColor(f.getRGBColor())
								.setMentionable(true).reason("Creating faction role").complete());
					} else {
						// There is at least one role matching the name on discord; take the first one
						// as the faction role
						factionRoles.put(f, possible.get(0));
					}
				}
			}

			// Correct roles' settings
			for (Entry<Faction, Role> entry : factionRoles.entrySet()) {
				if (!entry.getValue().getColor().equals(entry.getKey().getRGBColor()))
					entry.getValue().getManager().setColor(entry.getKey().getRGBColor()).queue();
				if (!entry.getValue().isMentionable())
					entry.getValue().getManager().setMentionable(true);
			}

			// Correct players' roles
			for (DiscordPlayer p : GenericPlugin.discPlayers) {
				Member m = guild().getMemberById(p.getDiscordID());
				if (m != null) { // Check the User is still in the guild
					ArrayList<Role> roleToRemove = new ArrayList<Role>();
					for (Role r : m.getRoles()) {
						if (factionRoles.containsValue(r) && !factionRoles.get(p.getFaction()).equals(r)) {
							roleToRemove.add(r);
						}
					}
					for (Role r : roleToRemove) {
						guild().removeRoleFromMember(m, r).queue();
					}
					if (p.getFaction() != null && !m.getRoles().contains(factionRoles.get(p.getFaction())))
						guild().addRoleToMember(m, factionRoles.get(p.getFaction())).queue();
				}
			}
		}
	}

	public static TextChannel whitelistChannel() {
		if (GenericPlugin.discord != null) {
			long channelID = GenericPlugin.discord.getLong("whitelist-channel");
			if (channelID > 0) {
				TextChannel channel = jda.getTextChannelById(channelID);
				return channel;
			} else
				return null;
		} else
			return null;
	}

	@SubscribeEvent
	public void onReady(ReadyEvent event) {
		GenericPlugin.logger.log(Level.INFO, "Connected to " + event.getGuildTotalCount() + " guild(s).");
		updateFactionRoles();
	}

	@SubscribeEvent
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (!event.getAuthor().isBot()) {
			MessageChannel channel = event.getMessage().getChannel();
			TextChannel whitelist = whitelistChannel();
			if (whitelist == null) {
				try {
					whitelist = event.getGuild().getTextChannelsByName("whitelist", true).get(0);
				} catch (IndexOutOfBoundsException e) {
					return;
				}
			}

			// If it's the whitelist channel
			if (channel.getIdLong() == whitelist.getIdLong()) {
				String username = event.getMessage().getContentRaw().trim();
				URL authenticatorURL;
				try {
					authenticatorURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
				} catch (MalformedURLException e) {
					return;
				}
				HttpURLConnection connection;
				try {
					connection = (HttpURLConnection) authenticatorURL.openConnection();
				} catch (IOException e) {
					channel.sendMessage("Unable to connect to account database.").queue();
					return;
				}
				connection.setDoOutput(true);
				connection.setInstanceFollowRedirects(false);
				try {
					connection.setRequestMethod("GET");
				} catch (ProtocolException e) {
					return;
				}
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("charset", "utf-8");
				String jsonStr;
				try {
					connection.connect();
					InputStream inStream = connection.getInputStream();
					Scanner s = new Scanner(inStream, "UTF-8");
					jsonStr = s.useDelimiter("\\Z").next();
					s.close();
				} catch (IOException e) {
					return;
				} catch (NoSuchElementException e) {
					channel.sendMessage("Minecraft account not recognized or something else went wrong: " + username)
							.queue();
					return;
				}
				JsonObject json = new JsonParser().parse(jsonStr).getAsJsonObject();
				if (json.has("error")) {
					channel.sendMessage("Minecraft account not recognized: " + username).queue();
					return;
				} else {
					String rawUUID = json.get("id").getAsString();
					String strUUID = rawUUID.substring(0, 8) + "-" + rawUUID.substring(8, 12) + "-"
							+ rawUUID.substring(12, 16) + "-" + rawUUID.substring(16, 20) + "-" + rawUUID.substring(20);

					OfflinePlayer player = GenericPlugin.getPlugin().getServer()
							.getOfflinePlayer(UUID.fromString(strUUID));
					DiscordPlayer minecraftDiscordPlayer = DiscordPlayer.getDiscordPlayer(player);
					DiscordPlayer discordDiscordPlayer = DiscordPlayer.getDiscordPlayer(event.getAuthor());
					if (discordDiscordPlayer != null) {
						channel.sendMessage("This Discord user already has a linked Minecraft account: "
								+ discordDiscordPlayer.getMCOfflinePlayer().getName()).queue();
					} else if (minecraftDiscordPlayer != null) {
						channel.sendMessage("This Minecraft account already has a linked Discord user: "
								+ minecraftDiscordPlayer.getDiscordUser().getName()).queue();
					} else { // This means neither is linked
						GenericPlugin.discPlayers
								.add(new DiscordPlayer(event.getAuthor().getIdLong(), player.getUniqueId()));
						channel.sendMessage(
								"Linked accounts: " + event.getAuthor().getAsMention() + " and " + player.getName())
								.queue();
						player.setWhitelisted(true);
						GenericPlugin.saveDiscord();
						updateFactionRoles();
					}
				}
			}
		}
	}

}
