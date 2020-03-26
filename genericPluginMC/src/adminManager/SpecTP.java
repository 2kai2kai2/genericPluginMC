package adminManager;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import genericPluginMC.GenericPlugin;

public class SpecTP {
	
	private Location loc;
	private GameMode gm;
	private Player player;
	
	public SpecTP(Player player, Location location, GameMode gamemode) {
		this.player = player;
		this.loc = location;
		this.gm = gamemode;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public GameMode getGameMode() {
		return gm;
	}
	
	public boolean end() {
		if (player.teleport(loc)) {
			player.setGameMode(gm);
			return GenericPlugin.adminSpecLocs.remove(this);
		} else {
			return false;
		}
	}
}
