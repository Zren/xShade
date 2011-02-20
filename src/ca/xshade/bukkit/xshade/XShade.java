package ca.xshade.bukkit.xshade;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class XShade extends JavaPlugin {
	//public PortalPlayerListener portalListener = new PortalPlayerListener(this, 2);
	public VoxelTeleportPlayerListener voxelTeleportListener = new VoxelTeleportPlayerListener();
	
	public XShade(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		registerEvents();

	}
	
	public BlockListener blockListener = new BlockListener() {
		public void onBlockRightClick(BlockRightClickEvent event) {
			event.getPlayer().sendMessage(event.getItemInHand().toString());
			event.getPlayer().sendMessage(event.getBlock().toString());
		}
		
		public void onBlockPlace(BlockPlaceEvent event) {
			
		}
	};
	
	
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	String command = cmd.getName().toLowerCase();
    	if (command.equals("wallgen")) {
    		return (new WallGenCommand(this)).execute(sender, commandLabel, args);
    	} else {
    		if (sender instanceof Player) {
    			Player player = (Player)sender;
    			player.sendMessage(player.getLocation().toString());
    		}
    	}
    	
    	return false;
    }

	
	private void registerEvents() {
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, portalListener, Priority.Low, this);
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, portalListener, Priority.Normal, this);
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, portalListener, Priority.Normal, this);
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, voxelTeleportListener, Priority.Normal, this);
	}
}

