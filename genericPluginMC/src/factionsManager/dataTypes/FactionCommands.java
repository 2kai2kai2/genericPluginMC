package factionsManager.dataTypes;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import diplomacy.DiploMail;
import diplomacy.JoinRequestMail;
import diplomacy.War;
import genericPluginMC.GenericPlugin;

public class FactionCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equals("faction")) {
			if (sender instanceof HumanEntity) {
				// Get the player object
				Player player = null;
				for (Player p : ((HumanEntity) sender).getWorld().getPlayers()) {
					if (p.getUniqueId().compareTo(((HumanEntity) sender).getUniqueId()) == 0) {
						player = p;
					}
				}

				if (args.length == 0) {
					return false; // Doesn't have any arguments
				} else {
					if (args[0].equals("create")) {
						if (args.length > 1) { // This is good, the rest of the arguments will be turned into the
												// multi-word faction name

							// Check if the player is already in a faction
							if (GenericPlugin.getPlayerFaction(player) != null) {
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
									sender.sendMessage("The name \"" + factionName
											+ "\" is already taken. Please choose another.");
									return true;
								}
							}

							// TODO: any other checks?

							Faction faction = new Faction(((HumanEntity) sender).getUniqueId(), factionName);
							GenericPlugin.factions.add(faction);
							player.setDisplayName(
									faction.getMember(player.getUniqueId()).topRole().getPrefix() + player.getName()
											+ faction.getMember(player.getUniqueId()).topRole().getPostfix());
							player.setPlayerListName(
									faction.getMember(player.getUniqueId()).topRole().getPrefix() + player.getName()
											+ faction.getMember(player.getUniqueId()).topRole().getPostfix());
							// Delete any other faction join requests
							for (int i = GenericPlugin.mail.size() - 1; i >= 0; i--) {
								DiploMail mail = GenericPlugin.mail.get(i);
								if (mail instanceof JoinRequestMail && ((JoinRequestMail) mail).getPlayer().equals(player.getUniqueId()))
									GenericPlugin.mail.remove(i);
							}
							
							GenericPlugin.saveData(GenericPlugin.getPlugin());
							sender.sendMessage("Created new faction: " + factionName);
							return true;
						}
					} else if (args[0].equals("join")) {
						if (args.length > 1) {
							// Check if the player is already in a faction
							if (GenericPlugin.getPlayerFaction(player) != null) {
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
									sender.sendMessage("Sent request to join faction: " + f.getName());
									GenericPlugin.mail.add(new JoinRequestMail(player.getDisplayName() + " request to join " + f.getName(), player.getUniqueId(), f));
									GenericPlugin.saveData(GenericPlugin.getPlugin());
									return true;
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
							Faction faction = GenericPlugin.getPlayerFaction(player);
							faction.removePlayer(player);
							sender.sendMessage("Left faction: " + faction.getName());
							if (faction.getMembers().size() == 0) {
								sender.sendMessage("Dissolved faction due to lack of members: " + faction.getName());
								GenericPlugin.factions.remove(faction);

								// Dissolve any wars that this is a leader in or remove it from ones it is
								// participating in
								for (int i = GenericPlugin.wars.size() - 1; i >= 0; i--) {
									War w = GenericPlugin.wars.get(i);
									if (w.getDefenders().get(0) == faction || w.getAttackers().get(0) == faction) {
										GenericPlugin.wars.remove(i);
									} else if (w.getDefenders().contains(faction))
										w.getDefenders().remove(faction);
									else if (w.getAttackers().contains(faction))
										w.getAttackers().remove(faction);
								}

								// Remove any mail sent to this faction
								for (int i = GenericPlugin.mail.size() - 1; i >= 0; i--) {
									if (GenericPlugin.mail.get(i).getRecipient() == faction)
										GenericPlugin.mail.remove(i);
								}
							}
							GenericPlugin.saveData(GenericPlugin.getPlugin());
							return true;
						} else {
							sender.sendMessage("Too many arguments. Did you mean \"/faction leave\"?");
							return true;
						}
					} else if (args[0].equals("list")) {
						if (args.length == 1) { // List all the factions
							sender.sendMessage("FACTIONS:");
							for (Faction faction : GenericPlugin.factions) {
								sender.sendMessage("- " + faction.getName() + ": Members: "
										+ faction.getMembers().size() + " Claims: " + faction.getClaims().size());
							}
							return true;
						} else { // List members of a faction
							// Get the name of the faction to get info on
							String factionName = "";
							for (int i = 1; i < args.length; i++) {
								if (i != 1)
									factionName += " ";
								factionName += args[i];
							}

							Faction faction = GenericPlugin.factionFromName(factionName);
							if (faction == null) {
								sender.sendMessage("The faction you entered was not recognized: " + factionName);
								return true;
							} else {
								sender.sendMessage(faction.getName() + ":");
								for (FactionMember member : faction.getMembers()) {
									OfflinePlayer memberPlayer = player.getServer()
											.getOfflinePlayer(member.getPlayer());
									String playerName;
									if (memberPlayer.getName() != null)
										playerName = member.topRole().getPrefix() + memberPlayer.getName()
												+ member.topRole().getPostfix();
									else
										playerName = "[UNKNOWN]";
									sender.sendMessage("- " + playerName + ": " + member.topRole().getName());
								}
								return true;
							}
						}
					} else if (args[0].equals("role")) {
						if (args.length >= 2) {
							Faction faction = GenericPlugin.getPlayerFaction(player);
							// Check if the player isn't in a faction
							if (faction == null) {
								sender.sendMessage("You must be in a faction to use faction roles.");
								return true;
							} else {
								FactionMember facMember = faction.getMember(player.getUniqueId());
								if (args[1].equals("add")) { // Subcommand role add
									if (facMember.hasPerm(RolePerms.ROLECONTROL)) {
										if (args.length == 3) {
											// Check that it isn't already a role
											if (faction.getRole(args[2]) == null) {
												faction.getRoles().add(new FactionRole(args[2]));
												sender.sendMessage("Created new role: " + args[2]);
												GenericPlugin.saveData(GenericPlugin.getPlugin());
												return true;
											} else {
												sender.sendMessage(
														"You cannot create this role due to it already existing for your faction: "
																+ args[2]);
												return true;
											}
										} else {
											if (args.length < 3)
												sender.sendMessage("Usage: /faction role add <roleName>");
											else // args.length > 3
												sender.sendMessage("Too many arguments. Role name must be one word.");
											return true;
										}
									} else {
										sender.sendMessage(
												"You do not have permissions within your faction to edit roles.");
										return true;
									}
								} else if (args[1].equals("delete")) { // Subcommand role delete
									if (facMember.hasPerm(RolePerms.ROLECONTROL)) {
										if (args.length == 3) {
											// Check that it isn't already a role
											FactionRole toDelete = faction.getRole(args[2]);
											if (toDelete != null) {
												if (!toDelete.isLeader()) { // Make sure not to delete leader role
													// Delete from roles list
													faction.getRoles().remove(toDelete);
													// Remove from all members
													for (FactionMember member : faction.getMembers()) {
														member.removeRole(toDelete);
													}
													sender.sendMessage("Deleted role: " + args[2]);
													GenericPlugin.saveData(GenericPlugin.getPlugin());
													return true;
												} else {
													sender.sendMessage("You cannot change leader roles.");
													return true;
												}
											} else {
												sender.sendMessage(
														"The role you want to delete does not exist: " + args[2]);
												return true;
											}
										} else {
											if (args.length < 3)
												sender.sendMessage("Usage: /faction role delete <roleName>");
											else // args.length > 3
												sender.sendMessage("Too many arguments. Role name must be one word.");
											return true;
										}
									} else {
										sender.sendMessage(
												"You do not have permissions within your faction to edit roles.");
										return true;
									}
								} else if (args[1].equals("give")) { // Subcomand role give
									if (facMember.hasPerm(RolePerms.ROLEGIVE)) {
										if (args.length == 4) {
											// Get player
											OfflinePlayer oPlayer = null;
											for (OfflinePlayer p : GenericPlugin.getPlugin().getServer()
													.getOfflinePlayers()) {
												if (p.getName() != null && p.getName().equalsIgnoreCase(args[2]))
													oPlayer = p;
											}
											// Check if it's a valid player
											if (oPlayer != null) {
												Faction addRFaction = GenericPlugin
														.getPlayerFaction(oPlayer.getUniqueId());
												if (addRFaction == null) { // Check that player is a part of a faction
													sender.sendMessage(
															"The player you specified was not part of a faction");
													return true;
												} else if (addRFaction != faction) {// Check that the player is part of
																					// this faction
													sender.sendMessage(
															"The player you specified was part of another faction");
												} else {
													FactionRole role = faction.getRole(args[3]);
													if (role == null) { // Check that the role exists
														sender.sendMessage(
																"The role you specified does not exist in this faction: "
																		+ args[3]);
														return true;
													} else if (role.isLeader()) { // Don't let them change leader roles
														sender.sendMessage("You cannot change leader roles.");
														return true;
													} else {
														faction.getMember(oPlayer.getUniqueId()).addRole(role);
														sender.sendMessage("Gave player " + oPlayer.getName()
																+ " role: " + role.getName());
														GenericPlugin.saveData(GenericPlugin.getPlugin());
														return true;
													}
												}
											} else {
												sender.sendMessage(
														"The player you specified was not recognized or has never played on this server: "
																+ args[2]);
												return true;
											}

										} else {
											if (args.length == 2)
												sender.sendMessage("Usage: /faction role give <player> <roleName>");
											else if (args.length == 3)
												sender.sendMessage("You must specify the role to give the player.");
											else // args.length > 4
												sender.sendMessage(
														"Too many arguments. Role and player names must be one word.");
											return true;
										}
									} else {
										sender.sendMessage(
												"You do not have permissions within your faction to give roles.");
										return true;
									}
								} else if (args[1].equals("remove")) { // Subcommand role remove
									if (facMember.hasPerm(RolePerms.ROLEGIVE)) {
										if (args.length == 4) {
											// Get player
											OfflinePlayer oPlayer = null;
											for (OfflinePlayer p : GenericPlugin.getPlugin().getServer()
													.getOfflinePlayers()) {
												if (p.getName() != null && p.getName().equalsIgnoreCase(args[2]))
													oPlayer = p;
											}
											// Check if it's a valid player
											if (oPlayer != null) {
												Faction addRFaction = GenericPlugin
														.getPlayerFaction(oPlayer.getUniqueId());
												if (addRFaction == null) { // Check that player is a part of a faction
													sender.sendMessage(
															"The player you specified was not part of a faction");
													return true;
												} else if (addRFaction != faction) {// Check that the player is part of
																					// this faction
													sender.sendMessage(
															"The player you specified was part of another faction");
												} else {
													FactionRole role = faction.getRole(args[3]);
													if (role == null) { // Check that the role exists
														sender.sendMessage(
																"The role you specified does not exist in this faction: "
																		+ args[3]);
														return true;
													} else if (role.isLeader()) { // Don't let them change leader roles
														sender.sendMessage("You cannot change leader roles.");
														return true;
													} else {
														faction.getMember(oPlayer.getUniqueId()).removeRole(role);
														sender.sendMessage("Removed from player " + oPlayer.getName()
																+ " role: " + role.getName());
														GenericPlugin.saveData(GenericPlugin.getPlugin());
														return true;
													}
												}
											} else {
												sender.sendMessage(
														"The player you specified was not recognized or has never played on this server: "
																+ args[2]);
												return true;
											}

										} else {
											if (args.length == 2)
												sender.sendMessage("Usage: /faction role remove <player> <roleName>");
											else if (args.length == 3)
												sender.sendMessage(
														"You must specify the role to remove from the player.");
											else // args.length > 4
												sender.sendMessage(
														"Too many arguments. Role and player names must be one word.");
											return true;
										}
									} else {
										sender.sendMessage(
												"You do not have permissions within your faction to remove roles.");
										return true;
									}
								} else if (args[1].equals("setting")) { // Subcommand role setting
									sender.sendMessage("This is not yet implemented.");
									return true;
								} else if (args[1].equals("list")) {
									sender.sendMessage("Roles for faction " + faction.getName());
									for (FactionRole role : faction.getRoles()) {
										String roleStr = "- " + role.getName() + " | Naming: " + role.getPrefix()
												+ "[PLAYER]" + role.getPostfix() + " | Permissions:";
										if (role.isLeader())
											roleStr += " LEADER";
										for (RolePerms p : RolePerms.values()) {
											if (role.hasPerm(p))
												roleStr += " " + p.toString();
										}
										sender.sendMessage(roleStr);
									}
									return true;
								} else if (args[1].equals("help")) { // Subcommand role help
									sender.sendMessage("========FACTION ROLE HELP========");
									sender.sendMessage("/faction role add <roleName>");
									sender.sendMessage("/faction role delete <roleName>");
									sender.sendMessage("/faction role give <player> <roleName>");
									sender.sendMessage("/faction role delete <player> <roleName>");
									sender.sendMessage("/faction role setting <roleName> [setting] [change]");
									sender.sendMessage("/faction role list");
									sender.sendMessage("/faction role help");
									return true;
								}
							}
						} else {
							// This is where we give role command help
							sender.sendMessage("Try this for roles help: /faction role help");
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
