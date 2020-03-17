package factionsManager.factionMap;

import java.awt.Color;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import factionsManager.dataTypes.Claim;
import genericPluginMC.Events;
import genericPluginMC.GenericPlugin;

public class FactionMapRenderer extends MapRenderer {

	private Location lastLoc;
	private Scale lastScale;

	@SuppressWarnings("deprecation")
	public static byte chatColorToMapByte(ChatColor color) {
		Color c;

		if (color.equals(ChatColor.BLACK)) {
			c = new Color(0, 0, 0);
		} else if (color.equals(ChatColor.DARK_BLUE)) {
			c = new Color(0, 0, 170);
		} else if (color.equals(ChatColor.DARK_GREEN)) {
			c = new Color(0, 170, 0);
		} else if (color.equals(ChatColor.DARK_AQUA)) {
			c = new Color(0, 170, 170);
		} else if (color.equals(ChatColor.DARK_RED)) {
			c = new Color(170, 0, 0);
		} else if (color.equals(ChatColor.DARK_PURPLE)) {
			c = new Color(170, 0, 170);
		} else if (color.equals(ChatColor.GOLD)) {
			c = new Color(255, 170, 0);
		} else if (color.equals(ChatColor.GRAY)) {
			c = new Color(170, 170, 170);
		} else if (color.equals(ChatColor.DARK_GRAY)) {
			c = new Color(85, 85, 85);
		} else if (color.equals(ChatColor.BLUE)) {
			c = new Color(85, 85, 255);
		} else if (color.equals(ChatColor.GREEN)) {
			c = new Color(85, 255, 85);
		} else if (color.equals(ChatColor.AQUA)) {
			c = new Color(85, 255, 255);
		} else if (color.equals(ChatColor.RED)) {
			c = new Color(255, 85, 85);
		} else if (color.equals(ChatColor.LIGHT_PURPLE)) {
			c = new Color(255, 85, 255);
		} else if (color.equals(ChatColor.YELLOW)) {
			c = new Color(255, 255, 85);
		} else {
			c = new Color(255, 255, 255);
		}

		return MapPalette.matchColor(c);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (player != null) {
			if (Events.isFactionMap(player.getInventory().getItemInMainHand())
					|| Events.isFactionMap(player.getInventory().getItemInOffHand())) {
				map.setWorld(player.getWorld());
				map.setCenterX(player.getLocation().getBlockX());
				map.setCenterZ(player.getLocation().getBlockZ());

				Location l = player.getLocation();

				if (lastLoc == null || lastLoc.distance(l) > 1 || lastScale == null
						|| !lastScale.equals(map.getScale())) {
					int magnitude = (int) Math.pow(2, map.getScale().ordinal());
					int offset = 64 * magnitude;
					for (int x = 0; x < 128; x++) {
						for (int y = 0; y < 128; y++) {
							Claim c = GenericPlugin.locationOwner(new Location(l.getWorld(),
									l.getX() + magnitude * x - offset, l.getY(), l.getZ() + magnitude * y - offset));
							if (c != null)
								canvas.setPixel(x, y, chatColorToMapByte(c.getOwner().getColor()));
							else
								canvas.setPixel(x, y, MapPalette.TRANSPARENT);
						}
					}
					// canvas.setPixel(63, 63, MapPalette.RED);
					lastLoc = l;
				}
				map.setTrackingPosition(true);
			}
		} else {
			// canvas.drawText(0, 0, new MapFont(), "player required");
		}
	}

}
