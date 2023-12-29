package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class WorldCommand implements IPluginCommand {

    private Main main;

    private final List<Material> waterTypes = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);
    private final List<Material> woodTypes = Arrays.asList(Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2);

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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase(getCommandName()) && commandSender instanceof Player){
            if(args.length == 0) {
                commandSender.sendMessage(
                        "/w : Cette commande est un alias pour la commande \"/world\". Elle sert de point d'accès central à toutes les commandes \"world\" disponibles. Lorsque vous la saisissez, vous obtenez une liste complète des commandes à votre disposition.\n" +
                        "/w tp : En utilisant cette commande, vous pouvez vous tp afin de voir le monde généré.\n" +
                        "/w load : En utilisant cette commande, vous pouvez lancer la pré-génération de la carte du jeu. Cela permet de créer la carte avant que les joueurs ne rejoignent la partie, ce qui peut contribuer à améliorer la fluidité du gameplay en réduisant les temps de chargement.\n" +
                        "/w fill (valeur) : Cette commande est utilisée pour remplacer l'eau dans une zone spécifiée par de la dirt (grass) (biome en herbe verte). La valeur détermine la taille du rayon autour du point d'origine où cette opération sera effectuée. Cela peut être utile pour modifier le terrain et personnaliser certaines zones du monde de jeu.\n" +
                        "/w center (valeur) : En utilisant cette commande, vous pouvez supprimer tous les arbres présents dans une zone spécifiée à partir du centre de la carte. Ensuite, des arbres de type \"roofed\" (forêt sombre) sont plantés pour recouvrir l'ensemble de la zone. Cette commande permet de créer des zones boisées uniformes ou de modifier la végétation du monde selon vos besoins.");
                return true;
            }

            Player player = (Player)commandSender;
            World world = Bukkit.getWorld("world");
            World game = Bukkit.getWorld("game");

            switch (args[0].toLowerCase()){
                case "tp":
                    if(game == null){
                        commandSender.sendMessage("Veuillez créer le monde avant de vouloir vous y téléporter");
                        return true;
                    }

                    World toTp = (player.getWorld().equals(world) ? game : world);
                    player.teleport(new Location(toTp, 0, toTp.getHighestBlockYAt(0, 0) + 1, 0));

                    return true;
                case "load":
                    commandSender.sendMessage("Vous venez de lancer la pré-génération de la carte");
                    main.getGameManager().getWorldsController().startWorldGeneration();
                    return true;
                case "fill":
                    if(args.length < 2) {
                        commandSender.sendMessage(Main.MSG_PREFIX + "Veuillez indiquer un rayon.");
                        return true;
                    }
                    if(!Pattern.matches("^[0-9]+$", args[1])) {
                        commandSender.sendMessage(Main.MSG_PREFIX + "Veuillez indiquer un rayon valide.");
                        return true;
                    }
                    int radius = Integer.parseInt(args[1]);

                    if(player.getWorld().equals(game)){
                        for(int rx = -radius; rx <= radius; rx++) for(int rz = -radius; rz <= radius; rz++) for(int ry = -radius; ry <= radius; ry++) {
                            Block block = game.getBlockAt(player.getLocation().add(rx, ry, rz));
                            if(waterTypes.contains(block.getType())) block.setType(Material.GRASS);
                        }
                        return true;
                    } else {
                        commandSender.sendMessage(Main.MSG_PREFIX + "Vous devez être dans le monde de jeu (/w tp)pour utiliser cette commande.");
                    }
                    return true;
                case "center":
                    if(args.length < 2) {
                        commandSender.sendMessage(Main.MSG_PREFIX + "Veuillez indiquer un rayon.");
                        return true;
                    }
                    if(!Pattern.matches("^[0-9]+$", args[1])) {
                        commandSender.sendMessage(Main.MSG_PREFIX + "Veuillez indiquer un rayon valide.");
                        return true;
                    }
                    int treeRadius = Integer.parseInt(args[1]);
                    if(treeRadius > 100){
                        commandSender.sendMessage(Main.MSG_PREFIX + "Le rayon ne peut pas être supérieur à 100.");
                        return true;
                    }

                    if(player.getWorld().equals(game)){
                        for(int rx = -treeRadius; rx <= treeRadius; rx++) for(int rz = -treeRadius; rz <= treeRadius; rz++) {
                            Location loc = player.getLocation();
                            loc.add(rx, 0, rz);
                            Block block = game.getBlockAt(loc);
                            if(woodTypes.contains(block.getType())) {
                                timberNearbyWood(block, 0);
                            }
                        }

                        Random rand = new Random();

                        for(int rx = -treeRadius; rx <= treeRadius; rx++) for(int rz = -treeRadius; rz <= treeRadius; rz++) {
                            Location loc = player.getLocation();
                            loc.add(rx, 0, rz);

                            if(rand.nextInt(100) < 7) game.generateTree(loc, TreeType.DARK_OAK);
                        }

                        commandSender.sendMessage(Main.MSG_PREFIX + "Vous venez de supprimer tous les arbres dans un rayon de " + treeRadius + " blocs autour de vous.");
                        return true;
                    } else {
                        commandSender.sendMessage(Main.MSG_PREFIX + "Vous devez être dans le monde de jeu (/w tp)pour utiliser cette commande.");
                    }
                default:
                    return false;
            }
        }
        return false;
    }

    private void timberNearbyWood(Block block, int recursionDepth){
        if(recursionDepth == 150) return; // Prevent infinite recursion (should never happen)
        if(block == null || !woodTypes.contains(block.getType())) return;

        block.setType(Material.AIR);

        for(BlockFace face : BlockFace.values()){
            timberNearbyWood(block.getRelative(face), recursionDepth + 1);
        }
    }
}
