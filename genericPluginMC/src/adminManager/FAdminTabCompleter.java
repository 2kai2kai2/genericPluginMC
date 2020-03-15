package adminManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.Faction;
import genericPluginMC.GenericPlugin;

public class FAdminTabCompleter implements TabCompleter {

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
		if (label.equals("fadmin")) {
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
					if (player.hasPermission("genericmc.admin.setdev")) {
						tabs.add("devrequests");
						tabs.add("setdev");
					}
					if (player.hasPermission("genericmc.admin.claimoverride")) {
						tabs.add("claimoverride");
					}
					tabs.add("help");
					return keepStarts(tabs, args[args.length - 1]); // Doesn't have any arguments
				} else {
					if (args[0].equals("devrequests")) {
						if (player.hasPermission("genericmc.admin.setdev")) {
							return null;
						} else {
							return null;
						}
					} else if (args[0].equals("setdev")) { // /fadmin setdev <claim> <number> [faction]
						if (player.hasPermission("genericmc.admin.setdev")) {
							if (args.length == 2) {
								ArrayList<String> tabs = new ArrayList<String>();
								for (Faction f : GenericPlugin.factions) {
									for (Claim c : f.getClaims()) {
										tabs.add(c.getName());
									}
								}
								return keepStarts(tabs, args[args.length - 1]);
							} else if (args.length == 3) { // Specifying the number
								return null;
							} else if (args.length == 4) {
								ArrayList<String> tabs = new ArrayList<String>();
								for (Faction f : GenericPlugin.factions) {
									if (f.getClaim(args[1]) != null)
										tabs.add(f.getName());
								}
								return keepStarts(tabs, args[args.length - 1]);
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else if (args[0].equals("claimoverride")) {
						return null;
					} else if (args[0].equals("help")) {
						return null;
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
