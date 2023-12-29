package fr.altaks.uhcapi2.commands.dev;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConfigLoad implements IPluginCommand {

    private Main main;

    public ConfigLoad(Main main){
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "configload";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase(getCommandName())){
            if(main.getGameManager().getChosenGameMode() != null){
                commandSender.sendMessage("§aLoading config...");
                main.getGameManager().loadGame();
            } else {
                commandSender.sendMessage("§cYou must choose a gamemode before loading a config");
            }
            return true;
        }
        return false;
    }
}
