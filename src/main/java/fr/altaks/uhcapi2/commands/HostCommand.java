package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import fr.altaks.uhcapi2.core.events.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class HostCommand implements IPluginCommand {

    private Main main;

    public HostCommand(Main main){
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "host";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && sender instanceof Player){
            if(args.length == 0){
                sender.sendMessage(
                        "/h : Cette commande est un alias pour la commande \"/host\". Elle sert de point d'accès central à toutes les commandes \"host\" disponibles. Lorsque vous la saisissez, vous obtenez une liste complète des commandes à votre disposition.\n" +
                            "/h menu : En utilisant cette commande, vous pouvez ouvrir une interface spécifique qui offre un accès facile à diverses options de configuration et de gestion de la partie.\n" +
                            "/h sethost : Cette commande permet de définir l'hôte principal de la partie, dont le nom s'affichera dans le tableau des scores en jeu.\n" +
                            "/h add (pseudo) : En utilisant cette commande, vous pouvez ajouter un co-host, ce qui lui donne un accès complet aux menus de configuration sans la possibilité de lancer la partie.\n" +
                            "/h remove (pseudo) : Cette commande vous permet de supprimer un co-host précédemment ajouté.\n" +
                            "/h chat (message) : permet d'envoyer un message dans le chat visible uniquement par les host\n" +
                            "/h kick (pseudo) : Vous pouvez expulser un joueur désigné vers le hub en utilisant cette commande.\n" +
                            "/h gm (valeur) : Avec cette commande, vous pouvez basculer entre les différents modes de jeu, notamment le mode de survie, le mode créatif, le mode aventure et le mode spectateur.\n" +
                            "/h heal (pseudo / all) : Utilisez cette commande pour soigner un joueur spécifique en entrant son nom, ou pour soigner tous les joueurs en saisissant \"all\".\n" +
                            "/h revive (pseudo) : Cette commande permet de ressusciter un joueur qui a été éliminé au cours de la partie.\n" +
                            "/h switch (pseudo & pseudo) : En utilisant cette commande, vous pouvez échanger les rôles, les inventaires et d'autres éléments entre deux joueurs spécifiques.\n" +
                            "/h killoffline : Cette commande tue tous les joueurs qui sont déconnectés depuis plus de 10 secondes.\n" +
                            "/h force (pvp/roles/border) : Vous pouvez forcer le déclenchement instantané des timers pour le PvP, l'annonce des rôles ou la réduction de la bordure en utilisant cette commande.\n" +
                            "/h spec : Cette commande affiche la liste des joueurs spectateurs dans la partie.\n" +
                            "/h spec add (pseudo) : Vous pouvez ajouter un joueur en tant que spectateur sans lui attribuer de rôle en utilisant cette commande.\n" +
                            "/h vote (message) : En saisissant cette commande, vous pouvez créer un sondage qui apparaît dans le chat, permettant aux joueurs de voter avec \"Oui\" ou \"Non\". Les résultats du vote sont visibles par tous à la fin.\n" +
                            "/h say (msg) : Vous pouvez envoyer un message en utilisant cette commande, qui sera visible par tous les joueurs en jeu grâce à la commande /say.");
                return true;
            }

            if(main.getGameManager().getHost() == null || (!main.getGameManager().getHost().equals(sender) && !main.getGameManager().getCoHosts().contains(sender))){
                sender.sendMessage("Vous n'avez pas la permission d'utiliser cette commande");
                return true;
            }

            Player player = (Player)sender;

            switch (args[0].toLowerCase()){
                case "menu": {
                    main.getGameManager().getHostMainMenu().open(player);
                    return true;
                }
                case "sethost": {
                    if(!main.getGameManager().getHost().equals(sender)){
                        sender.sendMessage("Vous devez être l'hôte principal pour utiliser cette commande");
                        return true;
                    }
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un pseudo");
                        return true;
                    }

                    Player target = main.getServer().getPlayer(args[1]);
                    if(target == null){
                        sender.sendMessage("Ce joueur n'est pas connecté");
                        return true;
                    }

                    main.getGameManager().setHost(target);
                    main.getGameManager().addCoHost(player);
                    sender.sendMessage("Vous avez défini " + target.getName() + " comme hôte de la partie");
                    return true;
                }
                case "add": {
                    if(!main.getGameManager().getHost().equals(sender)){
                        sender.sendMessage("Vous devez être l'hôte principal pour utiliser cette commande");
                        return true;
                    }
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un pseudo");
                        return true;
                    }

                    Player target = main.getServer().getPlayer(args[1]);
                    if(target == null){
                        sender.sendMessage("Ce joueur n'est pas connecté");
                        return true;
                    }

                    main.getGameManager().addCoHost(target);
                    sender.sendMessage("Vous avez ajouté " + target.getName() + " en tant que co-hôte");
                    return true;
                }
                case "remove": {
                    if(!main.getGameManager().getHost().equals(sender)){
                        sender.sendMessage("Vous devez être l'hôte principal pour utiliser cette commande");
                        return true;
                    }
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un pseudo");
                        return true;
                    }

                    Player target = main.getServer().getPlayer(args[1]);
                    if(target == null){
                        sender.sendMessage("Ce joueur n'est pas connecté");
                        return true;
                    }

                    main.getGameManager().removeCoHost(target);
                    sender.sendMessage("Vous avez retiré " + target.getName() + " des co-hôtes");
                    return true;
                }
                case "chat": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un message");
                        return true;
                    }

                    StringJoiner message = new StringJoiner(" ");
                    for(int i = 1; i < args.length; i++) message.add(args[i]);

                    String msg = Main.MSG_PREFIX + "§cHôte [" + player.getName() + "] §7\u00BB §c" + message;

                    main.getGameManager().getHost().sendMessage(msg);
                    for(Player p : main.getGameManager().getCoHosts()) p.sendMessage(msg);
                    return true;
                }
                case "kick": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un pseudo");
                        return true;
                    }

                    Player target = main.getServer().getPlayer(args[1]);
                    if(target == null){
                        sender.sendMessage("Ce joueur n'est pas connecté");
                        return true;
                    }

                    target.kickPlayer("Vous avez été expulsé de la partie par un host");
                    return true;
                }
                case "gm": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un mode de jeu");
                        return true;
                    }

                    GameMode gm = player.getGameMode();

                    String gameMode = args[1].toLowerCase();
                    if(Pattern.matches("^[0-9]+$", gameMode)) {
                        switch (Integer.parseInt(gameMode)){
                            case 0: gm = GameMode.SURVIVAL; break;
                            case 1: gm = GameMode.CREATIVE; break;
                            case 2: gm = GameMode.ADVENTURE; break;
                            case 3: gm = GameMode.SPECTATOR; break;
                            default:
                                sender.sendMessage("Veuillez saisir un mode de jeu valide");
                                return true;
                        }
                    } else {
                        try {
                            gm = GameMode.valueOf(gameMode.toUpperCase());
                        } catch (IllegalArgumentException e){
                            sender.sendMessage("Veuillez saisir un mode de jeu valide");
                            return true;
                        }
                    }

                    player.setGameMode(gm);
                    player.sendMessage("Vous êtes désormais en mode " + WordUtils.capitalizeFully(gm.name()));
                    return true;
                }
                case "heal": {
                    List<Player> targets = new ArrayList<>();
                    if(args.length < 2){
                        targets.add(player);
                    } else {
                        if(args[1].equalsIgnoreCase("all")){
                            targets.addAll(Bukkit.getOnlinePlayers());
                        } else {
                            Player target = main.getServer().getPlayer(args[1]);
                            if(target == null){
                                sender.sendMessage("Ce joueur n'est pas connecté");
                                return true;
                            }
                            targets.add(target);
                        }
                    }

                    for(Player target : targets){
                        target.setHealth(20);
                        target.setFoodLevel(20);
                        target.setSaturation(20);
                        target.sendMessage("Vous avez été soigné par un host");
                    }
                    return true;
                }
                case "revive": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un pseudo");
                        return true;
                    }

                    String target = args[1];
                    Bukkit.getPluginManager().callEvent(new HostCallPlayerReviveEvent(target));
                    return true;
                }
                case "switch": {
                    if(args.length < 3){
                        sender.sendMessage("Veuillez saisir deux pseudos");
                        return true;
                    }

                    String player1 = args[1];
                    String player2 = args[2];
                    Bukkit.getPluginManager().callEvent(new HostCallPlayerSwitchEvent(player1, player2));
                    return true;
                }
                case "killoffline": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un pseudo");
                        return true;
                    }

                    String target = args[1];
                    Bukkit.getPluginManager().callEvent(new HostCallKillOfflinePlayerEvent(target));
                    return true;
                }
                case "force": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir une valeur");
                        return true;
                    }
                    switch (args[1].toLowerCase()){
                        case "pvp":
                            Bukkit.broadcastMessage(Main.MSG_PREFIX + ChatColor.RED + "Le PvP va être activé dans 5 secondes (activation forcée par un host)");
                            new BukkitRunnable(){

                                int timer = 5;

                                @Override
                                @SuppressWarnings("deprecation")
                                public void run() {
                                    if(timer != 0) {
                                        for(Player player : Bukkit.getOnlinePlayers()){
                                            player.sendTitle(ChatColor.RED + "PvP actif dans " + timer, "");
                                        }
                                        timer--;
                                    } else {
                                        for(World world : Bukkit.getWorlds()){
                                            world.setPVP(true);
                                        }
                                        Bukkit.broadcastMessage(Main.MSG_PREFIX + "§cLe PvP est désormais activé");
                                        cancel();
                                    }
                                }

                            }.runTaskTimer(main, 0, 20L);
                            break;
                        case "border":
                            Bukkit.broadcastMessage(Main.MSG_PREFIX + ChatColor.RED + "La bordure va être réduite dans 5 secondes (activation forcée par un host)");
                            new BukkitRunnable(){

                                int timer = 5;

                                @Override
                                @SuppressWarnings("deprecation")
                                public void run() {
                                    if(timer != 0) {
                                        for(Player player : Bukkit.getOnlinePlayers()){
                                            player.sendTitle(ChatColor.RED + "Bordure réduite dans " + timer, "");
                                        }
                                        timer--;
                                    } else {
                                        main.getGameManager().getGameController().getGameBorderController().forceBorderReduction();
                                        Bukkit.broadcastMessage(Main.MSG_PREFIX + "§cLa réduction de la bordure a été lancée");
                                        cancel();
                                    }
                                }

                            }.runTaskTimer(main, 0, 20L);
                            break;
                        case "roles":
                            Bukkit.broadcastMessage(Main.MSG_PREFIX + ChatColor.RED + "Les rôles vont être attribués dans 5 secondes (activation forcée par un host)");
                            new BukkitRunnable(){

                                int timer = 5;

                                @Override
                                @SuppressWarnings("deprecation")
                                public void run() {
                                    if(timer != 0) {
                                        for(Player player : Bukkit.getOnlinePlayers()){
                                            player.sendTitle(ChatColor.RED + "Rôles définis dans " + timer, "");
                                        }
                                        timer--;
                                    } else {
                                        Bukkit.getPluginManager().callEvent(new HostCallForcedRolesAttributionEvent());
                                        Bukkit.broadcastMessage(Main.MSG_PREFIX + "§cLes rôles ont été attribués");
                                        cancel();
                                    }
                                }

                            }.runTaskTimer(main, 0, 20L);
                            break;
                    }

                    return true;
                }
                case "spec": {
                    if(args.length < 2){
                        Bukkit.getPluginManager().callEvent(new HostCallForSpectatorListEvent(player));
                    } else {
                        String target = args[1];
                        Bukkit.getPluginManager().callEvent(new HostCallSetSpectatingEvent(target));
                    }
                    return true;
                }
                case "vote": {
                    StringJoiner msg = new StringJoiner(" ");
                    for(int i = 1; i < args.length; i++) msg.add(ChatColor.YELLOW + args[i]);

                    main.getGameManager().getVoteService().startVote(ChatColor.YELLOW + "[Vote demandé par " + player.getName() +"] \u00BB " +  msg);
                    return true;
                }
                case "say": {
                    if(args.length < 2){
                        sender.sendMessage("Veuillez saisir un message");
                        return true;
                    }

                    StringJoiner message = new StringJoiner(" ");
                    for(int i = 1; i < args.length; i++) message.add(args[i]);

                    String msg = ChatColor.YELLOW + "Hôte [" + player.getName() + "] §7\u00BB " + ChatColor.YELLOW + message;
                    Bukkit.broadcastMessage(msg);

                    return true;
                }
            }
        }
        return false;
    }
}
