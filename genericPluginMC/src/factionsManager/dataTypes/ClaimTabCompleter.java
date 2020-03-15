package factionsManager.dataTypes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import genericPluginMC.GenericPlugin;

public class ClaimTabCompleter implements TabCompleter {
	
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
		if (label.equals("claim")) {
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
					if (member.hasPerm(RolePerms.CLAIM)) {
						tabs.add("new");
						tabs.add("delete");
						tabs.add("chunk");
						tabs.add("unchunk");
					}
					tabs.add("devrequest");
					tabs.add("list");
					tabs.add("help");
					return keepStarts(tabs, args[args.length - 1]); // Doesn't have any arguments
				} else {
					if (args[0].equals("new")) {
						return null;
					} else if (args[0].equals("delete")) {
						if (args.length == 2) {
							if (member.hasPerm(RolePerms.CLAIM)) { // Check that this player has perms
								ArrayList<String> tabs = new ArrayList<String>();
								for (Claim c : faction.getClaims()) {
									tabs.add(c.getName());
								}
								return keepStarts(tabs, args[args.length - 1]);
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else if (args[0].equals("chunk")) {
						if (member.hasPerm(RolePerms.CLAIM)) { // Check that this player has perms
							if (args.length == 2) {
								ArrayList<String> tabs = new ArrayList<String>();
								for (Claim c : faction.getClaims()) {
									if (c.maxChunks() > c.numChunks()) {
										tabs.add(c.getName());
									}
								}
								return keepStarts(tabs, args[args.length - 1]);
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else if (args[0].equals("unchunk")) {
						if (member.hasPerm(RolePerms.CLAIM)) { // Check that this player has perms
							if (args.length == 2) {
								ArrayList<String> tabs = new ArrayList<String>();
								for (Claim c : faction.getClaims()) {
									if (c.numChunks() > 1 && c.hasChunk(player.getLocation().getChunk())) {
										tabs.add(c.getName());
									}
								}
								return keepStarts(tabs, args[args.length - 1]);
							} else {
								return null;
							}
						} else {
							return null;
						}
					} else if (args[0].equals("devrequest")) {
						if (args.length == 2) { // Just do the one we're in if applicable
							ArrayList<String> tabs = new ArrayList<String>();
							for (Claim c : faction.getClaims()) {
								tabs.add(c.getName());
							}
							return keepStarts(tabs, args[args.length - 1]);
						} else { // Already put in a claim name
							return null;
						}
					} else if (args[0].equals("list")) {
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
