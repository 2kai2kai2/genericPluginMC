package genericPluginMC;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionCommands;
import factionsManager.dataTypes.FactionMember;

public class GenericPlugin extends JavaPlugin {
	
	public static ArrayList<Faction> factions;
	
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
	
	@Override
	public void onEnable() {
		factions = new ArrayList<Faction>();
		getServer().getPluginManager().registerEvents(new Events(), this);
		this.getCommand("faction").setExecutor(new FactionCommands());
	}
	
	@Override
	public void onDisable() {
		
	}
}
