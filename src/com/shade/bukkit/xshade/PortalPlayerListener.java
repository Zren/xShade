package com.shade.bukkit.xshade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.taylorkelly.teleplus.AimBlock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.shade.bukkit.util.Direction;
import com.shade.bukkit.util.MinecraftTools;
import com.shade.bukkit.util.SetBlockTask;
import com.shade.bukkit.util.TeleportTask;

public class PortalPlayerListener extends PlayerListener {
	private Map<Player,Portal[]> portalList = new HashMap<Player,Portal[]>();
	private Map<Block,Portal> portalBases = new HashMap<Block,Portal>();
	private int portalsPerPlayer = 2;
	private Plugin plugin;
	private final int halfWidth = 1; // width = halfWidth*2 + 1
	private final int height = 3;
	private final int portalDelay = 20000 / 50; // 30s -> MC ticks
	private final int buildSpeed = 10;
	private long lastTeleport = 0;
	private int minDelay = 1000;
	private Material portalBorder = Material.OBSIDIAN;
	private Material portalLiquid = Material.PORTAL;
	
	
	
	public PortalPlayerListener(Plugin plugin, int portalsPerPlayer) {
		this.plugin = plugin;
		this.portalsPerPlayer = portalsPerPlayer;
	}
	
	public boolean mustWait() {
		long now = System.currentTimeMillis();
		if (now - lastTeleport > minDelay) {
			lastTeleport = now;
			return false;
		} else
			return true;
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;
		
		Block lastBlock = event.getFrom().getBlock();
		if (lastBlock.getType() != portalLiquid) {
			Block standingBlock = event.getTo().getBlock();
			if (standingBlock.getType() == portalLiquid) {
				if (checkPortal(event.getPlayer(), standingBlock))
					event.setCancelled(true);
				int[][] offset = new int[][]{{1,0},{0,1},{-1,0},{0,-1}};
				for (int i = 0; i < offset.length; i++)
					if (checkPortal(event.getPlayer(),MinecraftTools.getBlockOffset(standingBlock, offset[i][0], 0, offset[i][1]))) {
						event.setCancelled(true);
						break;
					}	
			}
		}
	}
	
	public boolean checkPortal(Player player, Block block) {
		if (block.getType() == portalLiquid) {
			Portal portal = portalBases.get(block);
			if (portal != null) {
				Portal link = portal.getLink();
				if (link.hasBase()) {
					if (mustWait())
						return true;
					int id  = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new TeleportTask(player, link.getExit()));
					if (id == -1)
						System.out.println("[Portal] Error while scheduling teleport.");
					else
						System.out.println("[Portal] "+player.getName()+" -> "+link.getExit().toString());
				} else
					player.sendMessage("[Portal] No linked portal");
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == Material.GLOWSTONE_DUST) {
				// Left click (Portal 1)
				try {
					setPortal(player, 0, getLineOfSightBlock(player), getRotation(player));
				} catch (Exception e) {
					player.sendMessage("[Portal] " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onPlayerItem(PlayerItemEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().getType() == Material.GLOWSTONE_DUST) {
			// Right Click (Portal 2)
			try {
				setPortal(player, 1, getLineOfSightBlock(player), getRotation(player));
			} catch (Exception e) {
				player.sendMessage("[Portal] " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public Block getLineOfSightBlock(Player player) {
		AimBlock aimBlock = new AimBlock(player);
		Block target = aimBlock.getFaceBlock();
		return target;
	}
	
	public Direction getRotation(Player player) {
		double xRotation = (player.getLocation().getYaw() - 90) % 360 ;
		if (xRotation < 0)
			xRotation += 360;
		if (0 <= xRotation && xRotation < 45)
			return Direction.N;
		else if (45 <= xRotation && xRotation < 135)
			return Direction.E;
		else if (135 <= xRotation && xRotation < 225)
			return Direction.S;
		else if (225 <= xRotation && xRotation < 315)
			return Direction.W;
		else if (315 <= xRotation && xRotation < 360.0)
			return Direction.N;
		else
			return Direction.UNKOWN;
	}
	
	public Portal[] getPlayerPortals(Player player) {
		Portal[] playerPortals = portalList.get(player);
		if (playerPortals == null) {
			playerPortals = new Portal[portalsPerPlayer];
			for (int i = 0; i < portalsPerPlayer; i++)
				playerPortals[i] = new Portal();
			for (int i = 0; i < portalsPerPlayer; i++)
				playerPortals[i].setLink(playerPortals[getNextPortalId(i)]);
			portalList.put(player, playerPortals);
		}
		return playerPortals;
	}
	
	public int getNextPortalId(int current) {
		if (current + 1 >= portalsPerPlayer)
			return 0;
		else
			return current + 1;
	}
	
	public void setPortal(Player player, int portalNum, Block base, Direction rotation) throws Exception {
		Portal[] playerPortals = getPlayerPortals(player);
		if (playerPortals[portalNum].hasBase())
			destroyPortal(playerPortals[portalNum]);
		
		addPortalBase(playerPortals[portalNum], base);
		playerPortals[portalNum].setRotation(rotation);
		playerPortals[portalNum].setExit(playerPortals[portalNum].getBase().getLocation());
		buildPortal(playerPortals[portalNum]);
	}
		
	private void addPortalBase(Portal portal, Block base) {
		portalBases.put(base, portal);
		portal.setBase(base);
	}
	
	private void removePortalBase(Portal portal) {
		portalBases.remove(portal.getBase());
		portal.setBase(null);
	}

	private void buildPortal(Portal portal) throws Exception {
		if (!portal.hasBase())
			throw new NullPointerException("Base not set");
		
		if (portal.getRotation() == Direction.UNKOWN)
			throw new Exception("Error calculating rotation.");
		
		Block base = portal.getBase();
		
		// Remember the old materials
		List<Material> oldMaterials = new ArrayList<Material>();
		for (int y = -1; y < height+1; y++) {
			for (int x = -1; x <= halfWidth+1; x++) {
				if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
					oldMaterials.add(base.getWorld().getBlockAt(base.getX()+x, base.getY()+y, base.getZ()).getType());
				else
					oldMaterials.add(base.getWorld().getBlockAt(base.getX(), base.getY()+y, base.getZ()+x).getType());
			}
		}
		portal.setOldMaterials(oldMaterials);
		
		// Portal Frame
		List<Block> portalFrameBlocks = new ArrayList<Block>();
		for (int x = -1; x <= halfWidth+1; x++) { // Bottom
			Block block;
			if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
				block = base.getWorld().getBlockAt(base.getX()+x, base.getY()-1, base.getZ());
			else
				block = base.getWorld().getBlockAt(base.getX(), base.getY()-1, base.getZ()+x);
			portalFrameBlocks.add(block);
		}
		for (int y = 0; y < height; y++) { // Far pillar
			Block block;
			if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
				block = base.getWorld().getBlockAt(base.getX()+halfWidth+1, base.getY()+y, base.getZ());
			else
				block = base.getWorld().getBlockAt(base.getX(), base.getY()+y, base.getZ()+halfWidth+1);
			portalFrameBlocks.add(block);
		}
		for (int x = halfWidth+1; x >= -1; x--) { // Top
			Block block;
			if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
				block = base.getWorld().getBlockAt(base.getX()+x, base.getY()+height, base.getZ());
			else
				block = base.getWorld().getBlockAt(base.getX(), base.getY()+height, base.getZ()+x);
			portalFrameBlocks.add(block);
			
		}
		for (int y = height-1; y >= 0; y--) { // Near pillar
			Block block;
			if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
				block = base.getWorld().getBlockAt(base.getX()-1, base.getY()+y, base.getZ());
			else
				block = base.getWorld().getBlockAt(base.getX(), base.getY()+y, base.getZ()-1);
			portalFrameBlocks.add(block);
		}
		
		// Portal Liquid
		/*List<Block> portalBlocks = new ArrayList<Block>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x <= halfWidth; x++) { 
				Block block;
				if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
					block = base.getWorld().getBlockAt(base.getX()+x, base.getY()+y, base.getZ());
				else
					block = base.getWorld().getBlockAt(base.getX(), base.getY()+y, base.getZ()+x);
				portalBlocks.add(block);
			}
		}*/
		
		//System.out.println("[Portal] Build");
		
		int delay = 0;
		int id;
		for (Block block : portalFrameBlocks) {
			id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SetBlockTask(block, portalBorder), delay);
			if (id == -1)
				throw new Exception("Error while scheduling set block (build frame).");
			delay += buildSpeed;
		}
		id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SetBlockTask(base, Material.FIRE), delay);
		if (id == -1)
			throw new Exception("Error while scheduling set block (build).");
		/*for (Block block : portalBlocks) {
			id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SetBlockTask(block, portalLiquid), delay);
			if (id == -1)
				throw new Exception("Error while scheduling set block (build).");
		}*/
		id  = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RemovePortalTask(this, portal), delay+portalDelay);
		if (id == -1)
			throw new Exception("Error while scheduling destroy.");
	}

	public void destroyPortal(Portal portal) throws Exception {
		if (!portal.hasBase())
			return;
		
		//System.out.println("[Portal] Destroy");
		Block base = portal.getBase();
		int i = 0;
		for (int y = -1; y < height+1; y++) {
			for (int x = -1; x <= halfWidth+1; x++) {
				Block block;
				if (portal.getRotation() == Direction.E || portal.getRotation() == Direction.W)
					block = base.getWorld().getBlockAt(base.getX()+x, base.getY()+y, base.getZ());
				else
					block = base.getWorld().getBlockAt(base.getX(), base.getY()+y, base.getZ()+x);
				
				int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SetBlockTask(block, portal.getOldMaterials().get(i)));
				if (id == -1)
					throw new Exception("Error while scheduling set block (destroy).");
				
				i += 1;
			}
		}
		
		removePortalBase(portal);
	}
}