package ca.xshade.bukkit.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportTask implements Runnable {
	Player player;
	Location location;
	
	public TeleportTask(Player player, Location location) {
		this.player = player;
		this.location = location;
	}
	
	@Override
	public void run() {
		player.teleportTo(location);
	}

}
