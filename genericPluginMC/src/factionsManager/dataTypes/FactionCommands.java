package factionsManager.dataTypes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import genericPluginMC.GenericPlugin;

public class FactionCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equals("faction")) {
			if (sender instanceof HumanEntity) {
				if (args.length == 0) {
					return false; // Doesn't have any arguments
				} else {
					if (args[0].equals("create")) {
						if (args.length > 1) { // This is good, the rest of the arguments will be turned into the
												// multi-word faction name

							// Check if the player is already in a faction
							if (GenericPlugin.getPlayerFaction(((HumanEntity) sender).getUniqueId()) != null) {
								sender.sendMessage("You cannot be in a faction if you want to create a new one.");
								return true;
							}

							// Get the name of this new faction
							String factionName = "";
							for (int i = 1; i < args.length; i++) {
								if (i != 1)
									factionName += " ";
								factionName += args[i];
							}
							
							// Check that this name is not already taken
							for (Faction f : GenericPlugin.factions) {
								if (f.getName().equalsIgnoreCase(factionName)) {
									sender.sendMessage("The name \"" + factionName + "\" is already taken. Please choose another.");
									return true;
								}
							}
							
							// TODO: any other checks?
							
							Faction faction = new Faction(((HumanEntity) sender).getUniqueId(), factionName);
							GenericPlugin.factions.add(faction);
							for (Player p : ((HumanEntity) sender).getWorld().getPlayers()) {
								if (p.getUniqueId().compareTo(((HumanEntity) sender).getUniqueId()) == 0) {
									p.setDisplayName(faction.getMember(p.getUniqueId()).topRole().getPrefix() + p.getName() + faction.getMember(p.getUniqueId()).topRole().getPostfix());
									p.setPlayerListName(faction.getMember(p.getUniqueId()).topRole().getPrefix() + p.getName() + faction.getMember(p.getUniqueId()).topRole().getPostfix());

								}
							}
							sender.sendMessage("Created new faction: " + factionName);
							return true;
							
						}
					} else if (args[0].equals("join")) {
						if (args.length > 1) {
							// Check if the player is already in a faction
							if (GenericPlugin.getPlayerFaction(((HumanEntity) sender).getUniqueId()) != null) {
								sender.sendMessage("You cannot be in a faction if you want to join a different one.");
								return true;
							}
							
							// Get the name of the faction to join
							String factionName = "";
							for (int i = 1; i < args.length; i++) {
								if (i != 1)
									factionName += " ";
								factionName += args[i];
							}
							
							for (Faction f : GenericPlugin.factions) {
								if (f.getName().equalsIgnoreCase(factionName)) {
									// TODO: Make this be a request rather than just joining
									Player player = null;
									for (Player p : ((HumanEntity) sender).getWorld().getPlayers()) {
										if (p.getUniqueId().compareTo(((HumanEntity) sender).getUniqueId()) == 0) {
											player = p;
										}
									}
									if (player != null) {
										f.addPlayer(player);
										sender.sendMessage("Joined faction: " + factionName);
										return true;
									} else {
										return false; // Something went terribly wrong. Let's just ignore it.
									}
									
								}
							}
							// This means that the faction wasn't recognized
							sender.sendMessage("The faction \"" + factionName + "\" was not recognized.");
							return true;
						} else {
							sender.sendMessage("Please specify the faction to join.");
							return true;
						}
					} else if (args[0].equals("leave")) {
						if (args.length == 1) {
							Player player = null;
							for (Player p : ((HumanEntity) sender).getWorld().getPlayers()) {
								if (p.getUniqueId().compareTo(((HumanEntity) sender).getUniqueId()) == 0) {
									player = p;
								}
							}
							if (player != null) {
								GenericPlugin.getPlayerFaction(((HumanEntity) sender).getUniqueId()).removePlayer(player);
							} else {
								return false; // Something went terribly wrong. Let's just ignore it.
							}
						} else {
							sender.sendMessage("Too many arguments. Did you mean \"/faction leave\"?");
							return true;
						}
					}
				}
			} else {
				sender.sendMessage("You must be a player to use the faction command.");
				return true;
			}
		}
		return false;
	}

}
