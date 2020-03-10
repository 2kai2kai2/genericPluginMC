package diplomacy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionMember;
import factionsManager.dataTypes.RolePerms;
import genericPluginMC.GenericPlugin;

public class DiploCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equals("diplo")) {
			if (sender instanceof HumanEntity) {
				// Get the player object
				Player player = null;
				for (Player p : ((HumanEntity) sender).getWorld().getPlayers()) {
					if (p.getUniqueId().compareTo(((HumanEntity) sender).getUniqueId()) == 0) {
						player = p;
					}
				}
				// Get more info on player
				Faction faction = GenericPlugin.getPlayerFaction(player);
				FactionMember member = null;
				if (faction != null)
					member = faction.getMember(player.getUniqueId());
				else {
					sender.sendMessage("You must be a part of a faction to use diplo commands.");
					return true;
				}

				if (args.length == 0) {
					return false; // Doesn't have any arguments
				} else {
					if (args[0].equals("war")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							if (args.length > 1) {
								// Get the name of this new faction
								String factionName = "";
								for (int i = 1; i < args.length; i++) {
									if (i != 1)
										factionName += " ";
									factionName += args[i];
								}
								Faction dipFaction = GenericPlugin.factionFromName(factionName);
								// Check that the faction is valid
								if (dipFaction != null) {
									// Check that they aren't already at war with each other
									if (!faction.getWarEnemies().contains(dipFaction)) {
										GenericPlugin.wars.add(
												new War(faction, dipFaction, faction + " war against " + dipFaction));
										sender.sendMessage("Declared war on " + dipFaction);
										// GenericPlugin.saveData(GenericPlugin.getPlugin());
										return true;
									} else {
										sender.sendMessage("You cannot declare war because you are already at war with "
												+ dipFaction.getName());
										return true;
									}
								} else {
									sender.sendMessage("The specified faction was not recognized: " + factionName);
									return true;
								}
							} else {
								sender.sendMessage("You must include a faction to declare war upon.");
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("peace")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							sender.sendMessage("Not yet implemented.");
							return true;
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("ally")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							sender.sendMessage("Not yet implemented.");
							return true;
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("mailbox")) {
						sender.sendMessage("Not yet implemented.");
						return true;
					} else if (args[0].equals("help")) {
						sender.sendMessage("======DIPLOMATIC HELP======");
						sender.sendMessage("/diplo war <faction>");
						sender.sendMessage("/diplo ally <faction>");
						sender.sendMessage("/diplo help");
					}
				}
			}
		}
		return false;
	}

}
