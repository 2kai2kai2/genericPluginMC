package genericPluginMC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionMember;
import factionsManager.dataTypes.FactionRole;

public class Events implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Faction playerFaction = GenericPlugin.getPlayerFaction(event.getPlayer().getUniqueId());
		if (playerFaction != null) {
			FactionMember member = playerFaction.getMember(event.getPlayer().getUniqueId());
			FactionRole topRole = member.topRole();
			event.getPlayer().setDisplayName(topRole.getPrefix() + event.getPlayer().getName() + topRole.getPostfix());
			event.getPlayer()
					.setPlayerListName(topRole.getPrefix() + event.getPlayer().getName() + topRole.getPostfix());
			event.setJoinMessage(
					topRole.getPrefix() + event.getPlayer().getName() + topRole.getPostfix() + " joined the game.");
		} else {
			event.getPlayer().setDisplayName(event.getPlayer().getName() + " the Homeless");
			event.getPlayer().setPlayerListName(event.getPlayer().getName() + " the Homeless");
			event.setJoinMessage(event.getPlayer().getName() + " the Homeless joined the game.");
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		// If they just moved to a new chunk
		if (event.getFrom().getChunk() != event.getTo().getChunk()) {
			Claim fromClaim = GenericPlugin.chunkOwner(event.getFrom().getChunk());
			Claim toClaim = GenericPlugin.chunkOwner(event.getTo().getChunk());
			if (fromClaim != toClaim) {
				if (toClaim == null)
					event.getPlayer().sendMessage("Entered wilderness.");
				else
					event.getPlayer().sendMessage("Entered " + toClaim.getName());
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Claim claim = GenericPlugin.chunkOwner(event.getBlock().getChunk());
		if (claim == null || claim.getOwner().canPlayerModify(event.getPlayer())) {
			// We're all good! It's wilderness, at war, or in this faction.
		} else {
			// Cancel in other people's territory.
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Claim claim = GenericPlugin.chunkOwner(event.getBlock().getChunk());
		if (claim == null || claim.getOwner().canPlayerModify(event.getPlayer())) {
			// We're all good! It's wilderness, at war, or in this faction.
		} else {
			// Cancel in other people's territory.
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent event) {
		Claim claim = GenericPlugin.chunkOwner(event.getLectern().getChunk());
		if (claim == null || claim.getOwner().canPlayerModify(event.getPlayer())) {
			// We're all good! It's wilderness, at war, or in this faction.
		} else {
			// Cancel in other people's territory.
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			Claim claim = GenericPlugin.chunkOwner(event.getClickedBlock().getChunk());
			if (claim == null || claim.getOwner().canPlayerModify(event.getPlayer())) {
				// We're all good! It's wilderness, at war, or in this faction.
			} else {
				// Cancel in other people's territory.
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getPlayer() != null) {
			Claim claim = GenericPlugin.chunkOwner(event.getBlock().getChunk());
			if (claim == null || claim.getOwner().canPlayerModify(event.getPlayer())) {
				// We're all good! It's wilderness, at war, or in this faction.
			} else {
				// Cancel in other people's territory.
				event.setCancelled(true);
			}
		}
	}
}
