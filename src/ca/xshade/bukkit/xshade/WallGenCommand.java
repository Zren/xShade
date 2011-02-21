package ca.xshade.bukkit.xshade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ca.xshade.bukkit.towny.Towny;
import ca.xshade.bukkit.towny.TownyException;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;

public class WallGenCommand extends Command {
	Plugin plugin;
	
	public WallGenCommand(Plugin plugin) {
		super("wallgen");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Plugin test = plugin.getServer().getPluginManager().getPlugin("Towny");
		if (test == null && test instanceof Towny)
			return false;
		
		Player player = (Player)sender;
		Towny towny = (Towny)test;
		
		try {
			Resident resident = towny.getTownyUniverse().getResident(player.getName());
			Town town = resident.getTown();
			if (!resident.isMayor() || !town.hasAssistant(resident))
				throw new TownyException("You are not the mayor or assistant of the town.");
			
			
			
		} catch (TownyException e) {
			
		}
		return true;
	}
}

