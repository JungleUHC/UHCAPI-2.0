package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.regex.Pattern;

public class VoteCommand implements IPluginCommand {

    private Main main;

    public VoteCommand(Main main) {
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "vote";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && commandSender instanceof Player && args.length == 2){

            if(!Pattern.matches("^[0-9]+$", args[0])){
                commandSender.sendMessage(Main.MSG_PREFIX + "§cLe premier argument doit être un vote valide.");
                return false;
            }
            if(!Arrays.asList("true", "false").contains(args[1])){
                commandSender.sendMessage(Main.MSG_PREFIX + "§cLe deuxième argument doit être un vote valide.");
                return false;
            }

            String voteId = args[0];
            Boolean voteValue = Boolean.valueOf(args[1]);

            main.getGameManager().getVoteService().addVote(Integer.parseInt(voteId), (Player) commandSender, voteValue);
            return true;
        }
        return false;
    }
}
