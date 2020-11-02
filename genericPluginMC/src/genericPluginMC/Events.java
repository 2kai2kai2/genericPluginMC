package genericPluginMC;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import adminManager.SpecTP;
import diplomacy.DiploMail;
import diplomacy.DiploNotificationMail;
import factionsManager.dataTypes.Claim;
import factionsManager.dataTypes.Faction;
import factionsManager.dataTypes.FactionMember;
import factionsManager.dataTypes.RolePerms;
import factionsManager.factionMap.FactionMapRenderer;

public class Events implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		GenericPlugin.updateDisplayNames();
		event.setJoinMessage(event.getPlayer().getDisplayName() + " joined the game.");

		Faction f = GenericPlugin.getPlayerFaction(event.getPlayer());
		if (f != null) {
			boolean notifMail = false;
			FactionMember m = f.getMember(event.getPlayer().getUniqueId());
			ArrayList<DiploMail> facMail = GenericPlugin.recievedMail(f);
			if (m.hasPerm(RolePerms.DIPLO)) {
				if (facMail.size() > 0)
					notifMail = true;
			} else {
				for (DiploMail mail : facMail) {
					if (mail instanceof DiploNotificationMail) {
						notifMail = true;
						break;
					}
				}
			}

			if (notifMail) {
				event.getPlayer().sendMessage("Your faction has " + facMail.size() + " mail items.");
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (GenericPlugin.claimOverrides.contains(event.getPlayer()))
			GenericPlugin.claimOverrides.remove(event.getPlayer());
		SpecTP.endForPlayer(event.getPlayer());
		event.setQuitMessage(event.getPlayer().getDisplayName() + " left the game.");
	}

	public static boolean isFactionMap(ItemStack stack) {
		return stack != null && stack.getType().equals(Material.FILLED_MAP) && stack.getItemMeta().hasDisplayName()
				&& stack.getItemMeta().getDisplayName().equals("Factions Map");
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		ItemStack stack = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (isFactionMap(stack) && GenericPlugin.config.getBoolean("allow-faction-map")) {
			MapMeta meta = (MapMeta) stack.getItemMeta();
			meta.setColor(Color.PURPLE);
			meta.getMapView().getRenderers().clear();
			meta.getMapView().addRenderer(new FactionMapRenderer());
		}
	}

	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		ItemStack stack = event.getItemDrop().getItemStack();
		// Faction map scaling
		if (isFactionMap(stack)) {
			if (event.getPlayer().isSneaking() && GenericPlugin.config.getBoolean("allow-faction-map")) {
				MapView view = ((MapMeta) stack.getItemMeta()).getMapView();
				if (view.getScale().equals(Scale.CLOSEST))
					view.setScale(Scale.CLOSE);
				else if (view.getScale().equals(Scale.CLOSE))
					view.setScale(Scale.NORMAL);
				else if (view.getScale().equals(Scale.NORMAL))
					view.setScale(Scale.FAR);
				else if (view.getScale().equals(Scale.FAR))
					view.setScale(Scale.FARTHEST);
				else if (view.getScale().equals(Scale.FARTHEST))
					view.setScale(Scale.CLOSEST);
				event.setCancelled(true);
			}
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
					event.getPlayer().sendTitle("", ChatColor.DARK_GREEN.toString() + "Entered wilderness", 8, 60, 12);
				else
					event.getPlayer().sendTitle("",
							toClaim.getOwner().getColor().toString() + "Entered " + toClaim.getName(), 8, 60, 12);
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

	public static final Material[] unMinable = new Material[] { Material.IRON_BLOCK, Material.IRON_DOOR,
			Material.IRON_BARS, Material.IRON_TRAPDOOR, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE,
			Material.BLUE_CONCRETE, Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE,
			Material.BLACK_CONCRETE, Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE,
			Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE,
			Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.CYAN_GLAZED_TERRACOTTA,
			Material.PURPLE_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA,
			Material.GREEN_GLAZED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA,
			Material.WHITE_GLAZED_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA,
			Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA,
			Material.PINK_GLAZED_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
			Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA,
			Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA, Material.TERRACOTTA,
			Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
			Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA,
			Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
			Material.CHISELED_STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.MOSSY_STONE_BRICKS,
			Material.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_WALL,
			Material.STONE_BRICKS, Material.STONE_BRICK_STAIRS, Material.STONE_BRICK_SLAB, Material.STONE_BRICK_WALL,
			Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_STAIRS,
			Material.MOSSY_COBBLESTONE_WALL, Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_SLAB,
			Material.POLISHED_ANDESITE_STAIRS, Material.ANDESITE, Material.ANDESITE_SLAB, Material.ANDESITE_STAIRS,
			Material.ANDESITE_WALL, Material.POLISHED_DIORITE, Material.POLISHED_DIORITE_SLAB,
			Material.POLISHED_DIORITE_STAIRS, Material.DIORITE, Material.DIORITE_SLAB, Material.DIORITE_STAIRS,
			Material.DIORITE_WALL, Material.POLISHED_GRANITE, Material.POLISHED_GRANITE_SLAB,
			Material.POLISHED_GRANITE_STAIRS, Material.GRANITE, Material.GRANITE_SLAB, Material.GRANITE_STAIRS,
			Material.GRANITE_WALL, Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB, Material.COBBLESTONE,
			Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS, Material.COBBLESTONE_WALL, Material.STONE,
			Material.STONE_SLAB, Material.STONE_STAIRS, Material.DARK_OAK_PLANKS, Material.DARK_OAK_LOG,
			Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD,
			Material.DARK_OAK_DOOR, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE, Material.DARK_OAK_SLAB,
			Material.DARK_OAK_STAIRS, Material.ACACIA_PLANKS, Material.ACACIA_LOG, Material.ACACIA_WOOD,
			Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD, Material.ACACIA_DOOR, Material.ACACIA_FENCE,
			Material.ACACIA_FENCE_GATE, Material.ACACIA_SLAB, Material.ACACIA_STAIRS, Material.JUNGLE_PLANKS,
			Material.JUNGLE_LOG, Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD,
			Material.JUNGLE_DOOR, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE, Material.JUNGLE_SLAB,
			Material.JUNGLE_STAIRS, Material.BIRCH_PLANKS, Material.BIRCH_LOG, Material.BIRCH_WOOD,
			Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD, Material.BIRCH_DOOR, Material.BIRCH_FENCE,
			Material.BIRCH_FENCE_GATE, Material.BIRCH_SLAB, Material.BIRCH_STAIRS, Material.SPRUCE_PLANKS,
			Material.SPRUCE_LOG, Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD,
			Material.SPRUCE_DOOR, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE, Material.SPRUCE_SLAB,
			Material.SPRUCE_STAIRS, Material.OAK_PLANKS, Material.OAK_LOG, Material.OAK_WOOD, Material.STRIPPED_OAK_LOG,
			Material.STRIPPED_OAK_WOOD, Material.OAK_DOOR, Material.OAK_FENCE, Material.OAK_FENCE_GATE,
			Material.OAK_SLAB, Material.PETRIFIED_OAK_SLAB, Material.OAK_STAIRS };

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Claim claim = GenericPlugin.chunkOwner(event.getBlock().getChunk());
		if (claim == null) {
			// Wilderness
		} else if (claim.getOwner().getMember(event.getPlayer().getUniqueId()) != null
				|| GenericPlugin.claimOverrides.contains(event.getPlayer())) {
			// Own faction
		} else if (claim.getOwner().canPlayerModify(event.getPlayer())) {
			// At war -- Check if it's something you shouldn't break
			for (Material mat : unMinable) {
				if (mat.equals(event.getBlock().getType())) {
					event.setCancelled(true);
					break;
				}
			}
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
