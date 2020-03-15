package diplomacy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionMember;
import factionsManager.dataTypes.RolePerms;
import genericPluginMC.GenericPlugin;

public class DiploTabCompleter implements TabCompleter {

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
					return null;
				}

				if (args.length == 1) {
					ArrayList<String> tabs = new ArrayList<String>();
					if (member.hasPerm(RolePerms.DIPLO)) {
						tabs.add("war");
						tabs.add("peace");
						tabs.add("ally");
						tabs.add("unally");
					}
					tabs.add("relations");
					tabs.add("mailbox");
					tabs.add("help");
					return keepStarts(tabs, args[args.length - 1]); // Doesn't have any arguments
				} else {
					if (args[0].equals("war")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							// Show only nations we are at peace with and are not allied with (so can
							// attack)
							ArrayList<String> tabs = new ArrayList<String>();
							for (Faction f : GenericPlugin.factions) {
								if (!f.getWarEnemies().contains(faction) && !f.getAllies().contains(faction))
									tabs.add(f.getName());
							}
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else if (args[0].equals("peace")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							// Show only nations we are at war with and can peace with
							ArrayList<String> tabs = new ArrayList<String>();
							for (War w : faction.getWars()) {
								// If attacker war leader, can peace any defenders
								if (w.getAttackers().get(0) == faction) {
									for (Faction f : w.getDefenders())
										tabs.add(f.getName());
								}
								// If defender war leader, can peace any attackers
								else if (w.getDefenders().get(0) == faction) {
									for (Faction f : w.getAttackers())
										tabs.add(f.getName());
								}
								// If attacker ally, can peace defender war leader
								else if (w.getAttackers().contains(faction)) {
									tabs.add(w.getDefenders().get(0).getName());
								}
								// If defender ally, can peace attacker war leader
								else if (w.getDefenders().contains(faction)) {
									tabs.add(w.getAttackers().get(0).getName());
								}
							}
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else if (args[0].equals("ally")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							// Add everybody who isn't already an ally or at war
							ArrayList<String> tabs = new ArrayList<String>();
							for (Faction f : GenericPlugin.factions) {
								if (!f.getWarEnemies().contains(faction) && !f.getAllies().contains(faction))
									tabs.add(f.getName());
							}
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else if (args[0].equals("unally")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							// Add all allies
							ArrayList<String> tabs = new ArrayList<String>();
							for (Faction f : faction.getAllies())
								tabs.add(f.getName());
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else if (args[0].equals("relations")) {
						return null;
					} else if (args[0].equals("mailbox")) {
						return null;
					} else if (args[0].equals("help")) {
						return null;
					} else if (args[0].equals("accept")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							ArrayList<String> tabs = new ArrayList<String>();
							for (int i = 0; i < GenericPlugin.recievedMail(faction).size(); i++)
								tabs.add("" + i);
							return keepStarts(tabs, args[args.length - 1]);
						} else {
							return null;
						}
					} else if (args[0].equals("reject")) {
						// If the player can do diplo
						if (member.hasPerm(RolePerms.DIPLO)) {
							ArrayList<String> tabs = new ArrayList<String>();
							for (int i = 0; i < GenericPlugin.recievedMail(faction).size(); i++)
								tabs.add("" + i);
							return keepStarts(tabs, args[args.length - 1]);
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
		} else {
			return null;
		}
	}

}
