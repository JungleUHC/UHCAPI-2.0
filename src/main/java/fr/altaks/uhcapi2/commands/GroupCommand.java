package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class GroupCommand implements IPluginCommand {

    private Main main;

    public GroupCommand(Main main){
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "group";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    private int groupSize = 0;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player && command.getName().equalsIgnoreCase(getCommandName())){
            if(main.getGameManager().getHost().equals(commandSender) || main.getGameManager().getCoHosts().contains(commandSender)) {
                if(args.length > 0){
                    switch (args[0].toLowerCase()){
                        case "set":
                            String groupsValue = args[1];
                            if(Pattern.matches("^[0-9]+$", groupsValue)){
                                // value is integer, change group size and broadcast message using titles
                                groupSize = Integer.parseInt(groupsValue);
                                for(Player player : Bukkit.getOnlinePlayers()){
                                    player.sendTitle("§a§lGroupes de " + groupSize, "");
                                }
                                Bukkit.broadcastMessage("§aLes groupes ont été définis à " + groupsValue + " par les hôtes de la partie.");
                            } else {
                                commandSender.sendMessage(Main.MSG_PREFIX + "§cLa valeur spécifiée n'est pas un nombre entier / n'est pas valide.");
                            }
                            return true;
                        case "alert":
                            for(Player player : Bukkit.getOnlinePlayers()){
                                player.sendTitle("§a§lGroupes de " + groupSize, "");
                            }
                            return true;
                        case "rtp":
                            if(args.length < 2){
                                commandSender.sendMessage(Main.MSG_PREFIX + "§cVous devez spécifier un rayon ou un joueur.");
                                return true;
                            }
                            String rtpValue = args[1];
                            if(Pattern.matches("^[0-9]+$", rtpValue)){
                                // value is integer (radius)
                                int radius = Integer.parseInt(rtpValue);
                                for(Player player : Bukkit.getOnlinePlayers()){
                                    if(!player.equals(commandSender)){
                                        int randomX = (int) (Math.random() * radius * 2) - radius;
                                        int randomZ = (int) (Math.random() * radius * 2) - radius;

                                        player.teleport(new Location(
                                                player.getWorld(),
                                                randomX,
                                                player.getWorld().getHighestBlockYAt(randomX, randomZ) + 1,
                                                randomZ
                                        ));
                                    }
                                }
                                Bukkit.broadcastMessage("§aTous les joueurs ont été téléportés aléatoirement sur la carte dans un rayon de " + radius + " blocs par les hôtes de la partie.");
                                return true;
                            } else {
                                // value is string (player name)

                                Player target = Bukkit.getPlayer(rtpValue);
                                if(target != null){
                                    int radius = (int) (target.getWorld().getWorldBorder().getSize() / 2);

                                    int randomX = (int) (Math.random() * radius * 2) - radius;
                                    int randomZ = (int) (Math.random() * radius * 2) - radius;

                                    target.teleport(new Location(
                                            target.getWorld(),
                                            randomX,
                                            target.getWorld().getHighestBlockYAt(randomX, randomZ) + 1,
                                            randomZ
                                    ));
                                    commandSender.sendMessage("§aLe joueur " + target.getName() + " a été téléporté aléatoirement sur la carte");
                                    target.sendMessage("§aVous avez été téléporté aléatoirement sur la carte par les hôtes de la partie.");
                                    return true;
                                } else {
                                    commandSender.sendMessage(Main.MSG_PREFIX + "§cLe joueur " + rtpValue + " n'existe pas ou n'est pas connecté.");
                                    return true;
                                }
                            }
                        default:
                            return false;
                    }
                } else {
                    commandSender.sendMessage(
                            "/g : Cette commande est un alias pour la commande \"/groupe\". Elle sert de point d'accès central à toutes les commandes \"groupe\" disponibles. Lorsque vous la saisissez, vous obtenez une liste complète des commandes à votre disposition.\n" +
                                "/g set (valeur) : En utilisant cette commande, vous avez la possibilité de définir les groupes dans le jeu en attribuant une valeur spécifique à chaque groupe. \n" +
                                "/g alert : Cette commande est utilisée pour faire apparaître un message de groupe à tous les joueurs de la partie. \n" +
                                "/g rtp (rayon) : Utilisez cette commande pour téléporter tous les joueurs (à l'exception de vous-même) de manière aléatoire sur la carte dans un rayon spécifié.\n" +
                                "/g rtp (pseudo) : Cette commande permet de téléporter un joueur désigné de manière aléatoire sur la carte.\n");
                    return true;
                }
            } else {
                commandSender.sendMessage(Main.MSG_PREFIX + "§cVous n'êtes pas le host de la partie ni un des co-hosts.");
            }
        }
        return false;
    }
}
