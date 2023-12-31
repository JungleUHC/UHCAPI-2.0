package fr.altaks.uhcapi2.services;

import fr.altaks.uhcapi2.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;

public class VoteService {

    private final Main main;

    private int lastvoteId = 0;
    private final HashMap<Integer, HashMap<Player, Boolean>> votes = new HashMap<>();

    public VoteService(Main main){
        this.main = main;
    }

    public int startVote(String voteMsg){
        lastvoteId++;

        TextComponent msg = new TextComponent(voteMsg);
        TextComponent oui = new TextComponent(ChatColor.GREEN + "[\u2714 Oui]" + ChatColor.RESET);
        oui.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.GREEN + "Cliquez pour voter oui !")}));
        oui.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote " + lastvoteId + " true"));
        TextComponent non = new TextComponent(ChatColor.RED + "[\u2716 Non]" + ChatColor.RESET);
        non.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.RED + "Cliquez pour voter non !")}));
        non.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote " + lastvoteId + " false"));

        msg.addExtra("\n");
        msg.addExtra(oui);
        msg.addExtra(" ");
        msg.addExtra(non);

        for(Player player : main.getServer().getOnlinePlayers()){
            player.spigot().sendMessage(msg);
        }

        new BukkitRunnable(){

            private final int voteId = lastvoteId;

            @Override
            public void run() {
                if (votes.containsKey(voteId)){
                    boolean result = getVoteResult(voteId);
                    for(Player player : Bukkit.getOnlinePlayers()){
                        player.sendMessage(ChatColor.GREEN + "Le vote est terminé ! \"" + voteMsg + "\""+
                                "Résultat : " + (result ? ChatColor.GREEN + "Oui" : ChatColor.RED + "Non"));
                    }
                } else {
                    for(Player player : Bukkit.getOnlinePlayers()){
                        player.sendMessage(ChatColor.GREEN + "Le vote est terminé pour \""+voteMsg+"\"" +
                                "Résultat : " + ChatColor.RED + "Aucun vote n'a été enregistré");
                    }
                }
            }

        }.runTaskLater(main, 30 * 20L);

        return lastvoteId;
    }

    public void addVote(int voteId, Player player, boolean vote) {
        if (!votes.containsKey(voteId) && voteId <= lastvoteId){
            votes.put(voteId, new HashMap<>());
        }

        if (voteId <= lastvoteId) {
            if(!votes.get(voteId).containsKey(player)) { // if player has not already voted
                votes.get(voteId).put(player, vote);

                player.sendMessage(ChatColor.GREEN + "Votre vote a été pris en compte !");
            } else {
                player.sendMessage(ChatColor.RED + "Vous avez déjà voté !");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Ce vote n'existe pas !");
        }
    }

    public boolean getVoteResult(int voteId){
        if (!votes.containsKey(voteId)){
            return false;
        }

        int yes = 0;
        int no = 0;

        for (Player player : votes.get(voteId).keySet()){
            if (votes.get(voteId).get(player)){
                yes++;
            } else {
                no++;
            }
        }

        return yes > no;
    }

}
