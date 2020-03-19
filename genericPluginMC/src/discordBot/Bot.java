package discordBot;

import java.util.logging.Level;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import genericPluginMC.GenericPlugin;

public class Bot {
	
	public static void init() {
		String token = GenericPlugin.config.getString("discord-token");
		if (token != null && !token.equalsIgnoreCase("null")) {
			DiscordClientBuilder builder = new DiscordClientBuilder(token);
			DiscordClient client = builder.build();
			if (client.isConnected())
				System.out.println("CONNECTED ");
			//client.getEventDispatcher().on(eventClass)
		} else {
			GenericPlugin.logger.log(Level.INFO, "No discord token provided. Disabling discord integration.");
		}
	}
	
}
