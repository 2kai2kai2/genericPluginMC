package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import genericPluginMC.GenericPlugin;

public class FactionTabCompleter implements TabCompleter {

	public static ArrayList<String> keepStarts(ArrayList<String> list, String prefix) {
		ArrayList<String> newList = new ArrayList<String>();
		for (String str : list) {
			if (str.toLowerCase().startsWith(prefix.toLowerCase()))
				newList.add(str);
		}
		newList.sort(String.CASE_INSENSITIVE_ORDER);
		return newList;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof HumanEntity) {
			// Get the player object
			Player player = null;
			for (Player p : ((HumanEntity) sender).getWorld().getPlayers()) {
				if (p.getUniqueId().compareTo(((HumanEntity) sender).getUniqueId()) == 0) {
					player = p;
				}
			}

			if (args.length == 1) {
				ArrayList<String> tabs = new ArrayList<String>();
				if (GenericPlugin.getPlayerFaction(player) == null) {
					tabs.add("create");
					tabs.add("join");
				} else {
					tabs.add("leave");
					tabs.add("role");
					if (GenericPlugin.getPlayerFaction(player).getMember(player.getUniqueId()).isLeader()) {
						tabs.add("color");
					}
				}
				tabs.add("map");
				tabs.add("list");
				// tabs.add("help");
				return keepStarts(tabs, args[args.length - 1]); // Doesn't have any arguments
			} else {
				if (args[0].equals("create")) {
					return null;
				} else if (args[0].equals("join")) {
					if (args.length == 2) {
						if (GenericPlugin.getPlayerFaction(player) == null) {
							ArrayList<String> tabs = new ArrayList<String>();
							for (Faction f : GenericPlugin.factions)
								tabs.add(f.getName());
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else {
						return null;
					}
				} else if (args[0].equals("leave")) {
					return null;
				} else if (args[0].equals("list")) {
					if (args.length == 2) { // List all the factions
						ArrayList<String> tabs = new ArrayList<String>();
						for (Faction f : GenericPlugin.factions)
							tabs.add(f.getName());
						return keepStarts(tabs, args[args.length - 1]);
					} else {
						return null;
					}
				} else if (args[0].equals("role")) {
					Faction faction = GenericPlugin.getPlayerFaction(player);
					if (faction != null) {
						FactionMember facMember = faction.getMember(player.getUniqueId());
						if (args.length == 2) {
							ArrayList<String> tabs = new ArrayList<String>();
							if (facMember.hasPerm(RolePerms.ROLECONTROL)) {
								tabs.add("add");
								tabs.add("delete");
								tabs.add("setting");
							}
							if (facMember.hasPerm(RolePerms.ROLEGIVE)) {
								tabs.add("give");
								tabs.add("remove");
							}
							tabs.add("list");
							tabs.add("help");
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							if (args[1].equals("add")) { // Subcommand role add
								if (facMember.hasPerm(RolePerms.ROLECONTROL)) {
									return null;
								} else {
									return null;
								}
							} else if (args[1].equals("delete")) { // Subcommand role delete
								if (facMember.hasPerm(RolePerms.ROLECONTROL)) {
									if (args.length == 3) {
										ArrayList<String> tabs = new ArrayList<String>();
										for (FactionRole role : faction.getRoles()) {
											if (!role.isLeader())
												tabs.add(role.getName());
										}
										return keepStarts(tabs, args[args.length - 1]);
									} else {
										return null;
									}
								} else {
									return null;
								}
							} else if (args[1].equals("give")) { // Subcomand role give
								if (facMember.hasPerm(RolePerms.ROLEGIVE)) {
									if (args.length == 3) {
										ArrayList<String> tabs = new ArrayList<String>();
										for (FactionMember member : faction.getMembers()) {
											tabs.add(member.getOfflinePlayer().getName());
										}
										return keepStarts(tabs, args[args.length - 1]);
									} else if (args.length == 4) {
										ArrayList<String> tabs = new ArrayList<String>();
										FactionMember member = faction.getMember(args[2]);
										if (member == null) {
											// List all the non-leader roles
											for (FactionRole role : faction.getRoles()) {
												if (!role.isLeader())
													tabs.add(role.getName());
											}
										} else {
											// List all the non-leader roles the member doesn't have
											for (FactionRole role : faction.getRoles()) {
												if (!member.getRoles().contains(role) && !role.isLeader())
													tabs.add(role.getName());
											}
										}
										return keepStarts(tabs, args[args.length - 1]);
									} else {
										return null;
									}
								} else {
									return null;
								}
							} else if (args[1].equals("remove")) { // Subcommand role remove
								if (facMember.hasPerm(RolePerms.ROLEGIVE)) {
									if (args.length == 3) {
										ArrayList<String> tabs = new ArrayList<String>();
										for (FactionMember member : faction.getMembers()) {
											tabs.add(member.getOfflinePlayer().getName());
										}
										return keepStarts(tabs, args[args.length - 1]);
									} else if (args.length == 4) {
										ArrayList<String> tabs = new ArrayList<String>();
										FactionMember member = faction.getMember(args[2]);
										if (member == null) {
											// List all the non-leader roles
											for (FactionRole role : faction.getRoles()) {
												if (!role.isLeader())
													tabs.add(role.getName());
											}
										} else {
											// List all the non-leader roles the member has
											for (FactionRole role : faction.getRoles()) {
												if (member.getRoles().contains(role) && !role.isLeader())
													tabs.add(role.getName());
											}
										}
										return keepStarts(tabs, args[args.length - 1]);
									} else {
										return null;
									}
								} else {
									return null;
								}
							} else if (args[1].equals("setting")) { // Subcommand role setting
								// /faction role perm --- this isn't enough
								if (args.length == 3) {
									ArrayList<String> tabs = new ArrayList<String>();
									for (FactionRole role : faction.getRoles()) {
										tabs.add(role.getName());
									}
									return keepStarts(tabs, args[args.length - 1]);
								} else if (args.length > 3) {
									if (facMember.hasPerm(RolePerms.ROLECONTROL)) {
										if (args.length == 4) {
											ArrayList<String> tabs = new ArrayList<String>();
											tabs.add("prefix");
											tabs.add("postfix");
											for (RolePerms perm : RolePerms.values())
												tabs.add(perm.toString());
											return keepStarts(tabs, args[args.length - 1]);
										} else if (args.length == 5) {
											RolePerms perm = null;
											for (RolePerms p : RolePerms.values()) {
												if (p.name().equalsIgnoreCase(args[3])) {
													perm = p;
													break;
												}
											}
											if (perm == null)
												return null;
											else {
												ArrayList<String> tabs = new ArrayList<String>();
												tabs.add("true");
												tabs.add("false");
												return keepStarts(tabs, args[args.length - 1]);
											}
										} else {
											return null;
										}
									} else {
										return null;
									}
								} else {
									return null;
								}
							} else if (args[1].equals("list")) {
								return null;
							} else if (args[1].equals("help")) { // Subcommand role help
								return null;
							} else {
								return null;
							}
						}
					} else {
						return null;
					}
				} else if (args[0].equals("map")) {
					return null;
				} else if (args[0].equals("color")) {
					if (args.length == 2) {
						if (GenericPlugin.getPlayerFaction(player).getMember(player.getUniqueId()).isLeader()) {
							ArrayList<String> tabs = new ArrayList<String>();
							for (ChatColor color : ChatColor.values()) {
								if (color.isColor())
									tabs.add(color.name().toLowerCase());
							}
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

}
