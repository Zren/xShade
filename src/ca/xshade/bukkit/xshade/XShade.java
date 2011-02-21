package ca.xshade.bukkit.xshade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class XShade extends JavaPlugin {
	//public PortalPlayerListener portalListener = new PortalPlayerListener(this, 2);
	public VoxelTeleportPlayerListener voxelTeleportListener = new VoxelTeleportPlayerListener();

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		registerEvents();

	}
	
	public PlayerListener playerListener = new PlayerListener() {
		public void onPlayerJoin(org.bukkit.event.player.PlayerEvent event) {
			if (event.getPlayer().getName().equals("Shadeness")) {
				event.getPlayer().getInventory().addItem(new ItemStack(259));
			}
		}
	};
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	String command = cmd.getName().toLowerCase();
    	if (command.equals("wallgen")) {
    		return (new WallGenCommand(this)).execute(sender, commandLabel, args);
    	}
    	
    	return false;
    }

	
	private void registerEvents() {
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		
		
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, portalListener, Priority.Low, this);
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, portalListener, Priority.Normal, this);
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, portalListener, Priority.Normal, this);
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, voxelTeleportListener, Priority.Normal, this);
	}
}

