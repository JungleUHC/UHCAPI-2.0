package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTPCommand implements IPluginCommand {

    @Override
    public String getCommandName() {
        return "worldtp";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && sender.hasPermission("uhcapi2.worldtp") && sender instanceof Player){
            Player player = (Player) sender;
            player.teleport(new Location(
                    Bukkit.getWorld(args[0]),
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2]),
                    Double.parseDouble(args[3])
            ));
            return true;
        }
        return false;
    }
}
