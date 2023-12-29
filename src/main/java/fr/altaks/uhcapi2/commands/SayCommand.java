package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.util.StringJoiner;
import java.util.stream.Collectors;

public class SayCommand implements IPluginCommand {

    private Main main;

    public SayCommand(Main main){
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "say";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase(getCommandName())){
            StringJoiner joiner = new StringJoiner(" ");
            for(String str: strings){
                joiner.add(str);
            }
            for(Player player : Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList())){
                if(!player.equals(commandSender)) player.sendMessage(Main.MSG_PREFIX + ChatColor.YELLOW + "[" + commandSender.getName() + "] \u00BB " + joiner);
            }
            commandSender.sendMessage(Main.MSG_PREFIX + ChatColor.YELLOW + "[" + commandSender.getName() + "] \u00BB " + joiner);
            return true;
        }
        return false;
    }
}
