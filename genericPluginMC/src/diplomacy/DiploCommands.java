package diplomacy;

import org.bukkit.ChatColor;
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
								// Get the name of this other faction
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
									if (faction.getWarEnemies().contains(dipFaction)) {
										sender.sendMessage("You cannot declare war because you are already at war with "
												+ dipFaction.getName());
										return true;
									} else if (faction.getAllies().contains(dipFaction)) {
										sender.sendMessage("You cannot declare war on " + dipFaction.getName()
												+ " because you are currently allied with them.");
										return true;
									} else if (faction == dipFaction) {
										sender.sendMessage("You cannot declare war upon yourself.");
										return true;
									} else {
										GenericPlugin.wars.add(new War(faction, dipFaction,
												faction.getName() + " war against " + dipFaction.getName()));
										sender.sendMessage("Declared war on " + dipFaction.getName());
										faction.sendNotifMail(dipFaction,
												faction.getName() + " declaration of war against "
														+ dipFaction.getName(),
												faction.getName() + " has declared war against " + dipFaction.getName()
														+ "! You ought to prepare to fight and look for allies.");
										// Remove all alliance offers between them so they can't be accepted midwar
										for (int i = GenericPlugin.mail.size() - 1; i >= 0; i--) {
											DiploMail m = GenericPlugin.mail.get(i);
											if (m instanceof AllyOfferMail) {
												if ((m.getSender() == faction && m.getRecipient() == dipFaction)
														|| (m.getSender() == dipFaction
																&& m.getRecipient() == faction)) {
													GenericPlugin.mail.remove(i);
												}
											}
										}
										GenericPlugin.saveData(GenericPlugin.getPlugin());
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
							if (args.length > 1) {
								// Get the name of this other faction
								String factionName = "";
								for (int i = 1; i < args.length; i++) {
									if (i != 1)
										factionName += " ";
									factionName += args[i];
								}
								Faction dipFaction = GenericPlugin.factionFromName(factionName);
								// Check that the faction is valid
								if (dipFaction != null) {
									// Check that they are at war with each other
									if (faction.getWarEnemies().contains(dipFaction)) {
										sender.sendMessage("Send peace offer to " + dipFaction.getName());
										GenericPlugin.mail.add(new PeaceOfferMail(faction, dipFaction));
										GenericPlugin.saveData(GenericPlugin.getPlugin());
										return true;
									} else {
										sender.sendMessage("You cannot create peace with " + dipFaction.getName()
												+ " because you are not at war.");
										return true;
									}
								} else {
									sender.sendMessage("The specified faction was not recognized: " + factionName);
									return true;
								}
							} else {
								sender.sendMessage("You must include a faction to send peace deal to.");
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("ally")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							if (args.length > 1) {
								// Get the name of this other faction
								String factionName = "";
								for (int i = 1; i < args.length; i++) {
									if (i != 1)
										factionName += " ";
									factionName += args[i];
								}
								Faction dipFaction = GenericPlugin.factionFromName(factionName);

								if (dipFaction != null) {

									if (faction.getAllies().contains(dipFaction)) {
										sender.sendMessage("You cannot send an alliance request to "
												+ dipFaction.getName() + " because you are already allied with them.");
										return true;
									} else if (faction.getWarEnemies().contains(dipFaction)) {
										sender.sendMessage(
												"You cannot send an alliance request to " + dipFaction.getName()
														+ " because you are currently at war with them.");
										return true;
									} else if (faction == dipFaction) {
										sender.sendMessage("You cannot ally yourself.");
										return true;
									} else {
										GenericPlugin.mail.add(new AllyOfferMail(faction, dipFaction));
										sender.sendMessage("Send alliance offer to " + dipFaction.getName());
										GenericPlugin.saveData(GenericPlugin.getPlugin());
										return true;
									}
								} else {
									sender.sendMessage("The specified faction was not recognized: " + factionName);
									return true;
								}
							} else if (args.length == 1) {
								sender.sendMessage("You must specify a faction to send an alliance request to.");
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("unally")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							if (args.length > 1) {
								// Get the name of this other faction
								String factionName = "";
								for (int i = 1; i < args.length; i++) {
									if (i != 1)
										factionName += " ";
									factionName += args[i];
								}
								Faction dipFaction = GenericPlugin.factionFromName(factionName);

								if (dipFaction != null) {
									if (faction.getAllies().contains(dipFaction)) {
										faction.getAllies().remove(dipFaction);
										dipFaction.getAllies().remove(faction);
										sender.sendMessage("Removed ally: " + dipFaction.getName());
										faction.sendNotifMail(dipFaction,
												faction.getName() + " unallied " + dipFaction.getName(),
												faction.getName() + " declared that " + dipFaction.getName()
														+ " would no longer be their ally. This is a terrible betrayal!");
										GenericPlugin.saveData(GenericPlugin.getPlugin());
										return true;
									} else {
										sender.sendMessage("Could not remove " + dipFaction.getName()
												+ " as ally because you were not allied with them.");
										return true;
									}
								} else {
									sender.sendMessage("The specified faction was not recognized: " + factionName);
									return true;
								}
							} else if (args.length == 1) {
								sender.sendMessage("You must specify a faction to unally.");
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("relations")) {
						sender.sendMessage(ChatColor.UNDERLINE.toString() + ChatColor.BOLD.toString() + "======"
								+ faction.getName().toUpperCase() + "======");
						sender.sendMessage(ChatColor.UNDERLINE.toString() + ChatColor.RED.toString() + "At war:");
						for (Faction enemy : faction.getWarEnemies())
							sender.sendMessage(enemy.getName());
						sender.sendMessage(ChatColor.UNDERLINE.toString() + ChatColor.GREEN.toString() + "Allied:");
						for (Faction ally : faction.getAllies())
							sender.sendMessage(ally.getName());
						return true;
					} else if (args[0].equals("mailbox")) {
						sender.sendMessage(
								ChatColor.UNDERLINE.toString() + ChatColor.BOLD.toString() + "======MAIL======");
						for (DiploMail mail : GenericPlugin.recievedMail(faction)) {
							sender.sendMessage(ChatColor.UNDERLINE.toString() + mail.getTitle());
							if (mail instanceof JoinRequestMail)
								sender.sendMessage("From: " + ChatColor.ITALIC
										+ GenericPlugin.getPlugin().getServer()
												.getOfflinePlayer(((JoinRequestMail) mail).getPlayer()).getName()
										+ ChatColor.RESET + "   To: " + ChatColor.ITALIC
										+ mail.getRecipient().getName());
							else
								sender.sendMessage(
										"From: " + ChatColor.ITALIC + mail.getSender().getName() + ChatColor.RESET
												+ "   To: " + ChatColor.ITALIC + mail.getRecipient().getName());
							sender.sendMessage(mail.getDescription());
							if (mail instanceof DiploNotificationMail) {
								player.performCommand("tellraw @p [\"\",{\"text\":\"" + ChatColor.BOLD.toString()
										+ ChatColor.YELLOW.toString()
										+ "[DELETE]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/diplo accept "
										+ GenericPlugin.recievedMail(faction).indexOf(mail) + "\"}}]");
							} else {
								player.performCommand("tellraw @p [\"\",{\"text\":\"" + ChatColor.BOLD.toString()
										+ ChatColor.GREEN.toString()
										+ "[ACCEPT]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/diplo accept "
										+ GenericPlugin.recievedMail(faction).indexOf(mail) + "\"}},{\"text\":\""
										+ ChatColor.RED.toString()
										+ "[REJECT]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/diplo reject "
										+ GenericPlugin.recievedMail(faction).indexOf(mail) + "\"}}]");
							}
						}
						return true;
					} else if (args[0].equals("help")) {
						sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString()
								+ "======DIPLOMATIC HELP======");
						sender.sendMessage("/diplo war <faction>");
						sender.sendMessage("/diplo peace <faction>");
						sender.sendMessage("/diplo ally <faction>");
						sender.sendMessage("/diplo unally <faction>");
						sender.sendMessage("/diplo relations");
						sender.sendMessage("/diplo mailbox");
						sender.sendMessage("/diplo help");
					} else if (args[0].equals("accept")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							if (args.length == 2) {
								try {
									GenericPlugin.recievedMail(faction).get(Integer.parseInt(args[1])).approve();
									this.onCommand(sender, command, label, new String[] { "mailbox" });
									return true;
								} catch (IndexOutOfBoundsException | NumberFormatException e) {
									sender.sendMessage("Invalid mail index: " + args[1]);
									return true;
								}
							} else if (args.length < 2) {
								sender.sendMessage("No mail specified.");
								return true;
							} else { // args.length > 2
								sender.sendMessage("Too many arguments.");
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					} else if (args[0].equals("reject")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							if (args.length == 2) {
								try {
									GenericPlugin.recievedMail(faction).get(Integer.parseInt(args[1])).reject();
									this.onCommand(sender, command, label, new String[] { "mailbox" });
									return true;
								} catch (IndexOutOfBoundsException | NumberFormatException e) {
									sender.sendMessage("Invalid mail index: " + args[1]);
								}
							} else if (args.length < 2) {
								sender.sendMessage("No mail specified.");
								return true;
							} else { // args.length > 2
								sender.sendMessage("Too many arguments.");
								return true;
							}
							return true;
						} else {
							sender.sendMessage("You do not have permission within your faction to do diplomacy.");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
