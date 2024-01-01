package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements IPluginCommand {

    private Main main;

    public RulesCommand(Main main){
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "rules";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && commandSender instanceof Player){
            // open game config inventory
            main.getGameManager().getHostMainMenu().open((Player) commandSender);
        }
        return false;
    }
}
