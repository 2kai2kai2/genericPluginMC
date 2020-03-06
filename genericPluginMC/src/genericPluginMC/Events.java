package genericPluginMC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
			event.getPlayer().setPlayerListName(topRole.getPrefix() + event.getPlayer().getName() + topRole.getPostfix());
			event.setJoinMessage(topRole.getPrefix() + event.getPlayer().getName() + topRole.getPostfix() + " joined the game.");
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
			event.getPlayer().sendMessage("Crossed into chunk " + event.getTo().getChunk().getX() + ", " + event.getTo().getChunk().getZ());


		}
	}
}
