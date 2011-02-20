package ca.xshade.bukkit.xshade;

import me.taylorkelly.teleplus.AimBlock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;

public class VoxelTeleportPlayerListener extends PlayerListener {
	@Override
	public void onPlayerItem(PlayerItemEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().getType() == Material.GLOWSTONE_DUST) {
			player.teleportTo(getLineOfSightBlock(player).getLocation());
		}
	}
	
	public Block getLineOfSightBlock(Player player) {
		AimBlock aimBlock = new AimBlock(player);
		Block target = aimBlock.getFaceBlock();
		return target;
	}
}