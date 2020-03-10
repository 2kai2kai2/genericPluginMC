package genericPluginMC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import diplomacy.DiploCommands;
import diplomacy.War;
import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.ClaimCommands;
import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionCommands;
import factionsManager.dataTypes.FactionMember;
import factionsManager.dataTypes.FactionRole;

public class GenericPlugin extends JavaPlugin {

	public static FileConfiguration config;
	public static FileConfiguration data;

	public static ArrayList<Faction> factions;
	public static ArrayList<War> wars;

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

	public static Faction getPlayerFaction(Player player) {
		return getPlayerFaction(player.getUniqueId());
	}

	public static Faction factionFromName(String name) {
		for (Faction f : factions) {
			if (f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		return null;
	}

	@Override
	public void onEnable() {
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionRole.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(Claim.class);

		factions = new ArrayList<Faction>();
		wars = new ArrayList<War>();

		config = this.getConfig();
		config.addDefault("allow-wars", true);
		config.options().copyDefaults(true);
		this.saveDefaultConfig();

		this.saveResource("data.yml", false);
		data = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "data.yml"));
		loadData(getPlugin());

		getServer().getPluginManager().registerEvents(new Events(), this);
		this.getCommand("faction").setExecutor(new FactionCommands());
		this.getCommand("claim").setExecutor(new ClaimCommands());
		this.getCommand("diplo").setExecutor(new DiploCommands());
	}

	@Override
	public void onDisable() {
		saveData(getPlugin());
	}

	public static JavaPlugin getPlugin() {
		return getPlugin(GenericPlugin.class);
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

	public static void saveData(JavaPlugin p) {
		System.out.println("Saving data=-=asdf[awer fjs;dfj");
		ArrayList<Map<String, Object>> factionMaps = new ArrayList<Map<String, Object>>();
		for (Faction f : factions)
			factionMaps.add(f.serialize());
		System.out.println(factionMaps);
		data.set("factions", factionMaps);
		try {
			data.save(new File(p.getDataFolder(), "data.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadData(JavaPlugin p) {
		// boolean keepUnique,
		System.out.println(data);
		@SuppressWarnings({ "unchecked" })
		ArrayList<Map<String, Object>> factionMaps = (ArrayList<Map<String, Object>>) data.get("factions");
		System.out.println(factionMaps);
		if (factionMaps != null) {
			for (Map<String, Object> map : factionMaps) {
				factions.add(new Faction(map));
			}
		}
	}
}
