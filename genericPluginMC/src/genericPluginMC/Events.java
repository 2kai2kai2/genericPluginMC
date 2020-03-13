package genericPluginMC;

import org.bukkit.ChatColor;
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

public class Events implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		GenericPlugin.updateDisplayNames();
		event.setJoinMessage(event.getPlayer().getDisplayName() + " joined the game.");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		// If they just moved to a new chunk
		if (event.getFrom().getChunk() != event.getTo().getChunk()) {
			Claim fromClaim = GenericPlugin.chunkOwner(event.getFrom().getChunk());
			Claim toClaim = GenericPlugin.chunkOwner(event.getTo().getChunk());
			if (fromClaim != toClaim) {
				if (toClaim == null)
					event.getPlayer().sendTitle("", ChatColor.DARK_GREEN.toString() + "Entered wilderness", 8, 60, 12);
				else
					event.getPlayer().sendTitle("", "Entered " + toClaim.getName(), 8, 60, 12);
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
