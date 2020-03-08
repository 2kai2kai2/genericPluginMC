package factionsManager.dataTypes;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import genericPluginMC.GenericPlugin;

public class ClaimCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
					sender.sendMessage("You must be a part of a faction to use claim commands.");
					return true;
				}

				if (args.length == 0) {
					return false; // Doesn't have any arguments
				} else {
					if (args[0].equals("new")) {
						if (args.length < 2) { // Check that there is a name
							sender.sendMessage("Not enough arguments. Please specify the claim name.");
							return true;
						} else if (args.length > 2) { // Check that the name isn't more than 1 word
							sender.sendMessage("Too many arguments. Claim name must be one word.");
							return true;
						} else {
							if (member.canClaim()) { // Check that this player has perms
								if (faction.getClaim(args[1]) == null) { // Check the claim name isn't used
									if (faction.maxFreeClaims() > faction.usedFreeClaims()) {
										// Check the faction can still claim land
										Claim claim = new Claim(faction, args[1]);
										if (claim.addChunk(player.getWorld().getChunkAt(player.getLocation()))) {
											// Check that the chunk we're in isn't taken, otherwise add it
											faction.getClaims().add(claim);
											sender.sendMessage("Created new claim: " + claim.getName());
											return true;
										} else {
											sender.sendMessage(
													"Could not claim the current chunk. Please create the claim elsewhere.");
											return true;
										}
									} else {
										sender.sendMessage(
												"Your faction has no more free claims. Get more members or develop your other claims.");
										return true;
									}
								} else {
									sender.sendMessage("The specified claim name is already taken: " + args[1]);
									return true;
								}
							} else {
								sender.sendMessage("You do not have permission within your faction to claim land.");
								return true;
							}
						}
					} else if (args[0].equals("delete")) {
						if (args.length < 2) { // Check that there is a name
							sender.sendMessage("Not enough arguments. Please specify the claim name.");
							return true;
						} else if (args.length > 2) { // Check that the name isn't more than 1 word
							sender.sendMessage("Too many arguments. Claim name must be one word.");
							return true;
						} else {
							if (member.canClaim()) { // Check that this player has perms
								Claim claim = faction.getClaim(args[1]);
								if (claim != null) { // Check the claim name isn't used
									faction.getClaims().remove(claim);
									sender.sendMessage("Deleted claim: " + claim.getName());
									return true;
								} else {
									sender.sendMessage("The specified claim name was not recognized: " + args[1]);
									return true;
								}
							} else {
								sender.sendMessage("You do not have permission within your faction to unclaim land.");
								return true;
							}
						}
					} else if (args[0].equals("chunk")) {
						if (member.canClaim()) { // Check that this player has perms
							Claim claim = faction.getClaim(args[1]);
							if (claim != null) { // Check the claim exists
								if (claim.maxChunks() > claim.numChunks()) {
									// Check the faction can still add land to this claim
									Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
									if (claim.addChunk(chunk)) {
										// Check that the chunk we're in isn't taken, otherwise add it
										sender.sendMessage("Added chunk (" + chunk.getX() + ", " + chunk.getZ()
												+ ") to claim: " + claim.getName());
										return true;
									} else {
										sender.sendMessage(
												"Could not claim the current chunk. Please create the claim elsewhere.");
										return true;
									}
								} else {
									sender.sendMessage(
											"This claim is at its size limit. Use other claims or develop this one for more land.");
									return true;
								}
							} else {
								sender.sendMessage("The specified claim name was not recognized: " + args[1]);
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to claim land.");
							return true;
						}
					} else if (args[0].equals("unchunk")) {
						if (member.canClaim()) { // Check that this player has perms
							Claim claim = faction.getClaim(args[1]);
							if (claim != null) { // Check the claim exists
								if (claim.numChunks() > 1) {
									// Check that this would not result in 0 chunks in this claim
									Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
									if (claim.removeChunk(chunk)) {
										// Remove the chunk if it's in the claim, or if it's not then do nothing.
										sender.sendMessage("Removed chunk (" + chunk.getX() + ", " + chunk.getZ()
												+ ") from claim: " + claim.getName());
										return true;
									} else {
										sender.sendMessage("This chunk is already not in the claim.");
										return true;
									}
								} else {
									sender.sendMessage(
											"You cannot remove the last chunk in this claim. Instead, you can delete the claim entirely.");
									return true;
								}
							} else {
								sender.sendMessage("The specified claim name was not recognized: " + args[1]);
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission within your faction to unclaim land.");
							return true;
						}
					} else if (args[0].equals("devrequest")) {
						sender.sendMessage("Not yet implemented.");
						return true;
					}
				}
			}
		}
		return false;
	}
}
