/*     */ package fr.ariloxe.mumble.minecraft;
/*     */ 
/*     */ import fr.ariloxe.mumble.Main;
/*     */ import fr.ariloxe.mumble.murmur.api.MumbleLinkAPI;
/*     */ import fr.ariloxe.mumble.murmur.api.mumble.IUser;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Base64;
/*     */ import net.md_5.bungee.api.chat.BaseComponent;
/*     */ import net.md_5.bungee.api.chat.ClickEvent;
/*     */ import net.md_5.bungee.api.chat.ComponentBuilder;
/*     */ import net.md_5.bungee.api.chat.HoverEvent;
/*     */ import net.md_5.bungee.api.chat.TextComponent;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class MumbleCommand
/*     */   implements CommandExecutor
/*     */ {
/*     */   private final MumbleService mumbleService;
/*     */   
/*     */   public MumbleCommand(MumbleService mumbleService) {
/*  27 */     this.mumbleService = mumbleService;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
/*  32 */     if (!(commandSender instanceof Player)) {
/*  33 */       return true;
/*     */     }
/*  35 */     Player player = (Player)commandSender;
/*     */     
/*  37 */     if (strings.length == 0) {
/*  38 */       String token; if (MumbleLinkAPI.getApi().getMumbleManager().getServer() == null) {
/*  39 */         player.sendMessage("§3§lMumbleLink §8» §fDésolé, mais il semblerait que notre §cMumbleLink§f ne soit pas activé pour cette partie..");
/*  40 */         return true;
/*     */       } 
/*     */       
/*  43 */       String decoder = player.getName() + "-" + MumbleLinkAPI.getApi().getMumbleManager().getServer().getPort();
/*     */       
/*     */       try {
/*  46 */         token = Base64.getEncoder().encodeToString(decoder.getBytes(StandardCharsets.UTF_8.toString()));
/*  47 */       } catch (UnsupportedEncodingException e) {
/*  48 */         player.sendMessage("§4⚠ Une erreur est survenue, veuillez réessayer plus tard. Si ce problème persiste, créez un ticket sur notre discord. ⚠");
/*  49 */         return true;
/*     */       } 
/*     */       
/*  52 */       if (token == null) {
/*  53 */         player.sendMessage("§4⚠ Une erreur est survenue, veuillez réessayer plus tard. Si ce problème persiste, créez un ticket sur notre discord. ⚠");
/*  54 */         return true;
/*     */       } 
/*     */       
/*  57 */       player.sendMessage("");
/*  58 */       player.sendMessage("§3§lMumbleLink §8§l» §fCliquez sur le message suivant afin d'obtenir votre lien de connexion au Mumble de la partie.");
/*     */       
/*  60 */       TextComponent eventText = new TextComponent("§e§l[CLIQUEZ]");
/*  61 */       eventText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://jungleuhc.fr/askMumble.php?token=" + token.replaceAll("==", "")));
/*  62 */       eventText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("     §3§lMumbleLink \n\n §fCliquez sur ce lien afin de rejoindre le serveur mumble.")).create()));
/*  63 */       player.spigot().sendMessage((BaseComponent)eventText);
/*  64 */       player.sendMessage("");
/*     */     }
/*  66 */     else if (strings[0].equalsIgnoreCase("enable")) {
/*  67 */       if (MumbleLinkAPI.getApi().getMumbleManager().getServer() != null) {
/*  68 */         player.sendMessage("§3§lMumbleLink §8» §fDésolé, mais il semblerait que notre §cMumbleLink§f soit déjà activé pour cette partie..");
/*  69 */         return true;
/*     */       } 
/*     */       
/*  72 */       Bukkit.broadcastMessage("");
/*  73 */       Bukkit.broadcastMessage("§3§lMumbleLink §8§l» §f" + player.getName() + "§a a crée le serveur MumbleLink.");
/*  74 */       Bukkit.broadcastMessage("     §8» §fEffectuez la commande /mumble.");
/*  75 */       Bukkit.broadcastMessage("");
/*     */       
/*  77 */       Bukkit.getScheduler().runTaskAsynchronously((Plugin)Main.getInstance(), () -> MumbleLinkAPI.getApi().getMumbleManager().createServer());
/*  78 */       Bukkit.getScheduler().runTaskLater((Plugin)Main.getInstance(), () -> { this.mumbleService.getSpeakers().add(player.getName()); this.mumbleService.getSpeakers().add("Ariloxe"); this.mumbleService.waitingTask(); }20L);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  83 */     else if (strings[0].equalsIgnoreCase("list")) {
/*  84 */       if (MumbleLinkAPI.getApi().getMumbleManager().getServer() == null) {
/*  85 */         player.sendMessage("§3§lMumbleLink §8» §fDésolé, mais il semblerait que notre §cMumbleLink§f ne soit pas activé pour cette partie..");
/*  86 */         return true;
/*     */       } 
/*     */       
/*  89 */       MumbleLinkAPI.getApi().getMumbleManager().getServer().getUsers().forEach(iUser -> {
/*     */             if (Bukkit.getPlayer(iUser.getName()) != null) {
/*     */               player.sendMessage(iUser.getName() + " - §aConnecté");
/*     */             } else {
/*     */               player.sendMessage(iUser.getName() + " - §cDéconnecté");
/*     */             } 
/*     */           });
/*  96 */     } else if (strings[0].equalsIgnoreCase("disable")) {
/*  97 */       if (MumbleLinkAPI.getApi().getMumbleManager().getServer() == null) {
/*  98 */         player.sendMessage("§3§lMumbleLink §8» §fDésolé, mais il semblerait que notre §cMumbleLink§f ne soit pas activé pour cette partie..");
/*  99 */         return true;
/*     */       } 
/*     */       
/* 102 */       Bukkit.broadcastMessage("");
/* 103 */       Bukkit.broadcastMessage("§3§lMumbleLink §8§l» §f" + player.getName() + "§c a supprimé le serveur MumbleLink.");
/* 104 */       Bukkit.broadcastMessage("");
/*     */       
/* 106 */       MumbleLinkAPI.getApi().getMumbleManager().getServer().stop();
/* 107 */       MumbleLinkAPI.getApi().getMumbleManager().getServer().delete();
/* 108 */       MumbleLinkAPI.getApi().getMumbleManager().setServer(null);
/*     */     }
/* 110 */     else if (strings[0].equalsIgnoreCase("gamestart")) {
/* 111 */       if (MumbleLinkAPI.getApi().getMumbleManager().getServer() == null) {
/* 112 */         player.sendMessage("§3§lMumbleLink §8» §fDésolé, mais il semblerait que notre §cMumbleLink§f ne soit pas activé pour cette partie..");
/* 113 */         return true;
/*     */       } 
/*     */       
/* 116 */       player.sendMessage("§3§lMumbleLink §8§l» §fParamètre du Mumble défini sur : §6en cours de partie...");
/* 117 */       this.mumbleService.start();
/* 118 */     } else if (strings[0].equalsIgnoreCase("addspeaker")) {
/* 119 */       if (strings.length == 1) {
/* 120 */         player.sendMessage("");
/* 121 */         player.sendMessage("§3§lMumbleLink §8§l» §fMerci de rajouter le §cpseudonyme§f du joueur souhaité.");
/* 122 */         player.sendMessage("");
/* 123 */         return true;
/*     */       } 
/*     */       
/* 126 */       Player target = Bukkit.getPlayer(strings[1]);
/* 127 */       if (target == null) {
/* 128 */         player.sendMessage("");
/* 129 */         player.sendMessage("§3§lMumbleLink §8§l» §fLe joueur '§c" + strings[1] + "§f' n'est pas connecté.");
/* 130 */         player.sendMessage("");
/* 131 */         return true;
/*     */       } 
/*     */       
/* 134 */       this.mumbleService.getSpeakers().add(target.getName());
/*     */       
/* 136 */       target.sendMessage("§3§lMumbleLink §8§l» §fVous avez été §aajouté§f à la liste des Speakers sur le Mumble.");
/* 137 */       player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §aajouté§f le joueur §e" + target.getName() + "§f en tant que Speaker sur Mumble.");
/*     */     }
/* 139 */     else if (strings[0].equalsIgnoreCase("removespeaker")) {
/* 140 */       if (strings.length == 1) {
/* 141 */         player.sendMessage("");
/* 142 */         player.sendMessage("§3§lMumbleLink §8§l» §fMerci de rajouter le §cpseudonyme§f du joueur souhaité.");
/* 143 */         player.sendMessage("");
/* 144 */         return true;
/*     */       } 
/*     */       
/* 147 */       Player target = Bukkit.getPlayer(strings[1]);
/* 148 */       if (target == null) {
/* 149 */         player.sendMessage("");
/* 150 */         player.sendMessage("§3§lMumbleLink §8§l» §fLe joueur '§c" + strings[1] + "§f' n'est pas connecté.");
/* 151 */         player.sendMessage("");
/* 152 */         return true;
/*     */       } 
/*     */       
/* 155 */       if (target.getName().equals("Ariloxe")) {
/* 156 */         return true;
/*     */       }
/* 158 */       this.mumbleService.getSpeakers().remove(target.getName());
/*     */       
/* 160 */       target.sendMessage("§3§lMumbleLink §8§l» §fVous avez été §cretiré§f à la liste des Speakers sur le Mumble.");
/* 161 */       player.sendMessage("§3§lMumbleLink §8§l» §fVous avez §cretiré§f le joueur §e" + target.getName() + "§f en tant que Speaker sur Mumble.");
/*     */     } 
/*     */     
/* 164 */     return false;
/*     */   }
/*     */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/fr/ariloxe/mumble/minecraft/MumbleCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */