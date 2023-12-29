package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WorldCommand implements IPluginCommand {

    private Main main;

    public WorldCommand(Main main){
        this.main = main;
    }


    @Override
    public String getCommandName() {
        return "world";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
