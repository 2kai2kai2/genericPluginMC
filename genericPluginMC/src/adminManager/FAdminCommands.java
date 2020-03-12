package adminManager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

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
						sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString()
								+ "======Development Requests======");
						for (Devrequest request : GenericPlugin.devrequests) {
							sender.sendMessage(ChatColor.UNDERLINE.toString() + request.getClaim().getOwner().getName()
									+ ": " + request.getClaim().getName());
							String commandStr = "tellraw @p [\"\",{\"text\":\"Set Dev: \"}";
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
							player.performCommand(commandStr);
						}
						return true;
					} else if (args[0].equals("setdev")) { // /fadmin setdev <claim> <number> [faction]
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
									sender.sendMessage("Specified development number could not be parsed: " + args[2]);
									return true;
								}
								if (0 <= devNum && devNum <= 32) {
									possible.get(0).setDevLevel(devNum);
									sender.sendMessage(
											"Set development level for " + possible.get(0).getName() + " to " + devNum);
									for (int i = GenericPlugin.devrequests.size() - 1; i >= 0; i--) {
										if (GenericPlugin.devrequests.get(i).getClaim() == possible.get(0))
											GenericPlugin.devrequests.remove(i);
									}
									this.onCommand(sender, command, label, new String[]{"devrequests"});
									GenericPlugin.saveData(GenericPlugin.getPlugin());
									return true;
								} else {
									sender.sendMessage("You cannot set the development level to " + devNum);
									return true;
								}
							} else {
								sender.sendMessage("There are " + possible.size() + " factions with a claim named "
										+ args[1] + ". Please specify which using /fadmin setdev " + args[1] + args[2]
										+ " <faction>");
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
										this.onCommand(sender, command, label, new String[]{"devrequests"});
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
					} else if (args[0].equals("help")) {
						sender.sendMessage(
								ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "======FADMIN HELP======");
						sender.sendMessage("/fadmin devrequests");
						sender.sendMessage("/fadmin help");
						return true;
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
