package fr.altaks.uhcapi2.commands;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IPluginCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DocCommand implements IPluginCommand {

    private Main main;

    public DocCommand(Main main) {
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "doc";
    }

    @Override
    public boolean listensEvents() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase(getCommandName())){
            if(commandSender instanceof Player){
                TextComponent message = new TextComponent("§6[§eUHC§6] §eDocumentation: §fhttps://app.gitbook.com/o/sH649lnekvnOScWB5oQf/s/aHdgXR3DmrgiLp1owDmj/");
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
                        new TextComponent("§e§oCliquez pour ouvrir la documentation")
                }));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://app.gitbook.com/o/sH649lnekvnOScWB5oQf/s/aHdgXR3DmrgiLp1owDmj/"));
                ((Player)commandSender).spigot().sendMessage(message);
                return true;
            } else {
                commandSender.sendMessage("§cVous devez être un joueur pour exécuter cette commande !");
                return false;
            }
        }
        return false;
    }
}
