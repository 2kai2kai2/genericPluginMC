package adminManager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import discordBot.Bot;
import discordBot.DiscordPlayer;
import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class FAdminCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equals("fadmin")) {
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
					if (args[0].equals("devrequests")) {
						if (player.hasPermission("genericmc.admin.setdev")) {
							sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString()
									+ "======Development Requests======");
							for (Devrequest request : GenericPlugin.devrequests) {
								// Header
								sender.sendMessage(
										ChatColor.UNDERLINE.toString() + request.getClaim().getOwner().getName() + ": "
												+ request.getClaim().getName());
								// Tellraw for dev changes
								String commandStr = "tellraw " + player.getName() + " [\"\",{\"text\":\"Set Dev: \"}";
								for (int i = 0; i <= 16; i++) {
									String textStr;
									if (request.getClaim().getDevLevel() == i)
										textStr = ChatColor.WHITE.toString() + "[" + i + "]";
									else
										textStr = ChatColor.DARK_GRAY.toString() + "[" + i + "]";
									commandStr += ",{\"text\":\"" + textStr
											+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/fadmin setdev "
											+ request.getClaim().getName() + " " + i + " "
											+ request.getClaim().getOwner().getName() + "\"}}";
								}
								commandStr += "]";
								player.getServer().dispatchCommand(player.getServer().getConsoleSender(), commandStr);

								// Tellraw to view claim
								player.getServer().dispatchCommand(player.getServer().getConsoleSender(), "tellraw "
										+ player.getName() + " [\"\",{\"text\":\"" + ChatColor.BOLD.toString()
										+ ChatColor.WHITE.toString()
										+ "[TELEPORT]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/fadmin spectp "
										+ request.getClaim().getName() + " " + request.getClaim().getOwner().getName()
										+ "\"}}]");
							}
							return true;
						} else {
							sender.sendMessage(command.getPermissionMessage());
							return true;
						}
					} else if (args[0].equals("setdev")) { // /fadmin setdev <claim> <number> [faction]
						if (player.hasPermission("genericmc.admin.setdev")) {
							if (args.length == 3) {
								ArrayList<Claim> possible = new ArrayList<Claim>();
								for (Faction faction : GenericPlugin.factions) {
									Claim claim = faction.getClaim(args[1]);
									if (claim != null)
										possible.add(claim);
								}
								if (possible.size() == 0) {
									sender.sendMessage(
											"There are no claims named " + args[1] + ". Could not set development.");
									return true;
								} else if (possible.size() == 1) {
									int devNum;
									try {
										devNum = Integer.parseInt(args[2]);
									} catch (NumberFormatException e) {
										sender.sendMessage(
												"Specified development number could not be parsed: " + args[2]);
										return true;
									}
									if (0 <= devNum && devNum <= 32) {
										possible.get(0).setDevLevel(devNum);
										sender.sendMessage("Set development level for " + possible.get(0).getName()
												+ " to " + devNum);
										for (int i = GenericPlugin.devrequests.size() - 1; i >= 0; i--) {
											if (GenericPlugin.devrequests.get(i).getClaim() == possible.get(0))
												GenericPlugin.devrequests.remove(i);
										}
										this.onCommand(sender, command, label, new String[] { "devrequests" });
										GenericPlugin.saveData(GenericPlugin.getPlugin());
										return true;
									} else {
										sender.sendMessage("You cannot set the development level to " + devNum);
										return true;
									}
								} else {
									sender.sendMessage("There are " + possible.size() + " factions with a claim named "
											+ args[1] + ". Please specify which using /fadmin setdev " + args[1]
											+ args[2] + " <faction>");
								}
							} else if (args.length > 3) {
								String factionName = "";
								for (int i = 3; i < args.length; i++) {
									if (i != 3)
										factionName += " ";
									factionName += args[i];
								}

								Faction faction = GenericPlugin.factionFromName(factionName);
								if (faction == null) {
									sender.sendMessage("Specified faction was not recognized: " + factionName);
									return true;
								} else {
									Claim claim = faction.getClaim(args[1]);
									if (claim == null) {
										sender.sendMessage("Specified claim was not recognized: " + args[1]);
										return true;
									} else {
										int devNum;
										try {
											devNum = Integer.parseInt(args[2]);
										} catch (NumberFormatException e) {
											sender.sendMessage(
													"Specified development number could not be parsed: " + args[2]);
											return true;
										}
										if (0 <= devNum && devNum <= 32) {
											claim.setDevLevel(devNum);
											sender.sendMessage(
													"Set development level for " + claim.getName() + " to " + devNum);
											for (int i = GenericPlugin.devrequests.size() - 1; i >= 0; i--) {
												if (GenericPlugin.devrequests.get(i).getClaim() == claim)
													GenericPlugin.devrequests.remove(i);
											}
											this.onCommand(sender, command, label, new String[] { "devrequests" });
											GenericPlugin.saveData(GenericPlugin.getPlugin());
											return true;
										} else {
											sender.sendMessage("You cannot set the development level to " + devNum);
											return true;
										}
									}
								}
							} else {
								sender.sendMessage("Not enough arguments.");
								return true;
							}
						} else {
							sender.sendMessage(command.getPermissionMessage());
							return true;
						}
					} else if (args[0].equals("claimoverride")) {
						if (player.hasPermission("genericmc.admin.claimoverride")) {
							if (GenericPlugin.claimOverrides.contains(player)) {
								// Remove them
								GenericPlugin.claimOverrides.remove(player);
								sender.sendMessage("Disabled claim override for " + player.getName());
								return true;
							} else {
								// Add them
								GenericPlugin.claimOverrides.add(player);
								sender.sendMessage("Enabled claim override for " + player.getName());
								return true;
							}
						} else {
							sender.sendMessage(command.getPermissionMessage());
							return true;
						}
					} else if (args[0].equals("help")) {
						sender.sendMessage(
								ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "======FADMIN HELP======");
						sender.sendMessage("/fadmin devrequests");
						sender.sendMessage("/fadmin claimoverride");
						sender.sendMessage("/fadmin help");
						sender.sendMessage("/fadmin spectp [claim] [faction]");
						return true;
					} else if (args[0].equals("spectp")) {
						if (player.hasPermission("genericmc.admin.spectp")) {
							if (args.length == 1) {
								// This is just for canceling and returning to the previous location
								if (GenericPlugin.adminSpecLocs.containsKey(player)) {
									player.teleport(GenericPlugin.adminSpecLocs.get(player));
									player.setGameMode(GameMode.SURVIVAL);
									sender.sendMessage("Teleported back to previous location.");
									return true;
								} else {
									sender.sendMessage(
											"You are not currently observing so cannot return to a previous location.");
									return true;
								}
							} else if (args.length >= 2) {
								Claim claim = null;
								if (args.length == 2) {
									// This only works if the claim name is unique
									ArrayList<Claim> namedClaims = new ArrayList<Claim>();
									for (Faction f : GenericPlugin.factions) {
										Claim c = f.getClaim(args[1]);
										if (c != null)
											namedClaims.add(c);
									}

									if (namedClaims.size() == 1)
										claim = namedClaims.get(0);
									else if (namedClaims.size() == 0) {
										sender.sendMessage("Claim name not recognized: " + args[1]);
										return true;
									} else {
										sender.sendMessage(
												"Multiple factions have claims of this name. Please specify the faction.");
										return true;
									}
								} else {
									// Faction is specified
									String factionName = "";
									for (int i = 2; i < args.length; i++) {
										if (i != 2)
											factionName += " ";
										factionName += args[i];
									}

									Faction f = GenericPlugin.factionFromName(factionName);
									if (f != null) {
										Claim c = f.getClaim(args[1]);
										if (c != null)
											claim = c;
									} else {
										sender.sendMessage("Faction not recognized: " + factionName);
										return true;
									}
								}

								// Add the location to go back to if they aren't already observing
								if (!GenericPlugin.adminSpecLocs.containsKey(player)) {
									GenericPlugin.adminSpecLocs.put(player, player.getLocation());
								}

								player.setGameMode(GameMode.SPECTATOR);
								player.teleport(claim.getChunks().get(0).getBlock(0, 128, 0).getLocation());
								sender.sendMessage("Now observing: " + claim.getName()
										+ ". Use \"/fadmin spectp\" to return to your survival location.");
								return true;
							}
						} else {
							sender.sendMessage(command.getPermissionMessage());
							return true;
						}
					} else if (args[0].equals("claim")) {
						if (args.length > 1) {
							Faction admfac = GenericPlugin.factionFromName("admin");
							if (args[1].equals("new")) {
								if (args.length == 3) {
									if (admfac.getClaim(args[2]) == null) {
										admfac.getClaims().add(new Claim(admfac, args[2]));
										admfac.getClaim(args[2]).addChunk(player.getLocation().getChunk());
										sender.sendMessage("Created new admin claim: " + args[2]);
										return true;
									} else {
										sender.sendMessage(
												"Claim name is already taken for the admin faction: " + args[2]);
										return true;
									}
								} else {
									sender.sendMessage("Invalid number of arguments.");
									return true;
								}
							} else if (args[1].equals("delete")) {
								if (args.length == 3) {
									if (admfac.getClaim(args[2]) != null) {
										admfac.getClaims().remove(admfac.getClaim(args[2]));
										sender.sendMessage("Deleted admin claim: " + args[2]);
										return true;
									} else {
										sender.sendMessage("Claim name not recognized: " + args[2]);
										return true;
									}
								} else {
									sender.sendMessage("Invalid number of arguments.");
									return true;
								}
							} else if (args[1].equals("chunk")) {
								if (args.length == 3) {
									Claim c = admfac.getClaim(args[2]);
									if (c != null) {
										if (c.addChunk(player.getLocation().getChunk())) {
											sender.sendMessage("Claimed chunk for admin claim: " + c.getName());
											return true;
										} else {
											sender.sendMessage("This chunk is already claimed.");
											return true;
										}
									} else {
										sender.sendMessage("Claim name not recognized: " + args[2]);
										return true;
									}
								} else {
									sender.sendMessage("Invalid number of arguments.");
									return true;
								}
							} else if (args[1].equals("unchunk")) {
								if (args.length == 3) {
									Claim c = admfac.getClaim(args[2]);
									if (c != null) {
										if (c.removeChunk(player.getLocation().getChunk())) {
											sender.sendMessage("Unclaimed chunk for admin claim: " + c.getName());
											return true;
										} else {
											sender.sendMessage("This chunk isn't claimed.");
											return true;
										}
									} else {
										sender.sendMessage("Claim name not recognized: " + args[2]);
										return true;
									}
								} else {
									sender.sendMessage("Invalid number of arguments.");
									return true;
								}
							} else if (args[1].equals("list")) {
								sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString()
										+ "======ADMIN CLAIMS======");
								for (Claim c : admfac.getClaims()) {
									sender.sendMessage(ChatColor.UNDERLINE.toString() + c.getName() + ": "
											+ ChatColor.RESET.toString() + " Chunks: " + c.numChunks());
								}
								return true;
							} else {
								sender.sendMessage("You must specify an fadmin claim subcommand to perform.");
								return false;
							}
						} else {
							sender.sendMessage("Please specify an action to take for admin claims.");
							return true;
						}
					} else if (args[0].equals("players")) {
						if (args.length == 1) {
							sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString()
									+ "======ONLINE PLAYERS======");
							for (Player p : GenericPlugin.getPlugin().getServer().getOnlinePlayers()) {
								String message = p.getName() + " | Display Name: " + p.getDisplayName()
										+ " | Discord: ";
								DiscordPlayer discPlayer = DiscordPlayer.getDiscordPlayer(p);
								if (discPlayer != null) {
									message += discPlayer.getDiscordUser().getAsTag();
								} else {
									message += "UNKNOWN";
								}
								sender.sendMessage(message);
							}
						} /*
							 * else if (args.length == 2) {
							 * 
							 * @SuppressWarnings("deprecation") OfflinePlayer p =
							 * GenericPlugin.getPlugin().getServer().getOfflinePlayer(args[1]); String
							 * message = p.getName() + " | Display Name: " + p.getDisplayName() +
							 * " | Discord: "; DiscordPlayer discPlayer = DiscordPlayer.getDiscordPlayer(p);
							 * if (discPlayer != null) { message += discPlayer.getDiscordUser().getAsTag();
							 * } else { message += "UNKNOWN"; } sender.sendMessage(message); }
							 */ else {
							sender.sendMessage("Invalid number of arguments.");
						}
					} else if (args[0].equals("unlink")) {
						if (player.hasPermission("genericmc.admin.unlink") && GenericPlugin.discord != null) {
							if (args.length == 2) {
								DiscordPlayer p;
								try {
									p = DiscordPlayer
											.getDiscordPlayer(Bot.jda.getUserByTag(args[1].replaceAll("@", "").trim()));
								} catch (IllegalArgumentException e) {
									sender.sendMessage("Player not recognized: " + args[1]);
									return true;
								}
								if (p != null) {
									p.getMCOfflinePlayer().setWhitelisted(false);
									GenericPlugin.discPlayers.remove(p);
									GenericPlugin.saveDiscord();
									sender.sendMessage("Player unlinked and removed from whitelist.");
									return true;
								} else {
									sender.sendMessage("Player not recognized: " + args[1]);
									return true;
								}
							} else {
								sender.sendMessage("Invalid number of arguments: /fadmin unlink <discordtag>");
								return true;
							}
						} else {
							sender.sendMessage(command.getPermissionMessage());
							return true;
						}
					} else {
						return false;
					}
				}
			} else {
				sender.sendMessage("You must be a player to use the fadmin command.");
				return true;
			}
		}
		return false;
	}

}
