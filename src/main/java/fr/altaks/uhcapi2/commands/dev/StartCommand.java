package fr.altaks.uhcapi2.commands.dev;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements IPluginCommand {

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    private final Main main;

    public StartCommand(Main main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && sender instanceof Player)  {
            main.getGameManager().start();
            Main.logInfo("Game has been forcefully started by " + sender.getName());
            return true;
        }
        return false;
    }
}
