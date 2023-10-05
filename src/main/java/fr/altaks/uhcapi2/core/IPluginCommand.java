package fr.altaks.uhcapi2.core;

import fr.altaks.uhcapi2.core.util.TabCompleteUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public interface IPluginCommand extends TabExecutor {

    String getCommandName();

    boolean listensEvents();

    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length != 0) return TabCompleteUtil.getDefaultImprovedCompleter(args[args.length - 1]);
        return null;
    }
}
