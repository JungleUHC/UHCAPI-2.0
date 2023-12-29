package fr.altaks.uhcapi2.commands.dev;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ValidateCommand implements IPluginCommand {

    private Main main;

    public ValidateCommand(Main main){
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "validate";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 0){
                player.sendMessage(Main.MSG_PREFIX + "Note du développeur : Cette commande est à usage interne uniquement.");
                return false;
            }

            if(!main.getGameManager().getHost().equals(player)){
                player.sendMessage(Main.MSG_PREFIX + "§cVous n'êtes pas le host de la partie ni un des co-hosts.");
                return false;
            }

            switch (args[0]){
                case "startinv":
                    main.getGameManager().getGameController().getGameInvsController().validateInventoryCloning(player, true);
                    break;
                case "deathinv":
                    main.getGameManager().getGameController().getGameInvsController().validateInventoryCloning(player, false);
                    break;
                default:
                    player.sendMessage(Main.MSG_PREFIX + "§cVeuillez préciser ce que vous voulez valider.");
                    break;
            }
            return true;
        }
        return false;
    }
}
