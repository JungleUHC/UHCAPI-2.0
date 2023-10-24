package fr.altaks.uhcapi2.views.scenarios;

import fr.altaks.uhcapi2.Main;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface Scenario extends Listener {

    String getName();

    String getDescription();

    ItemBuilder getIcon();

    int getSlot();

    void setup(Main main);

    default boolean isConfigurable() {
        return false;
    }

    default void processClick(InventoryClickEvent event){}

    /*
    Cat Eyes (non configurable) : Accorde l'effet de vision nocturne à tous les joueurs, ce qui rend la nuit moins obscure.
    Cut Clean (non configurable) : Simplifie le processus de collecte en cuisant instantanément tous les minerais, le sable et la nourriture extraits.
    TimberPVP (non configurable) : Permet de casser instantanément les troncs d'arbres jusqu'à l'activation du PvP, facilitant la collecte de bois.
    Giga Drill (non configurable) : Tous les outils fabriqués sont dotés d'Efficiency 10 et Unbreaking 10, améliorant considérablement l'efficacité et la durabilité.
    Hastey Boys (non configurable) : Les outils fabriqués reçoivent l'enchantement Efficiency 3 et Unbreaking 3, accélérant ainsi la collecte.
    Hastey Babies (non configurable) : Les outils fabriqués reçoivent l'enchantement Efficiency 1 et Unbreaking 1, offrant un léger avantage en vitesse.
    Starter Tools (non configurable) : Ajoute des outils en fer enchantés avec Efficiency 2 et Unbreaking 2 dans l'inventaire de départ, simplifiant le début de la partie.
    Safe Miner (non configurable) : Les joueurs ne subissent pas de dégâts de feu sous la couche 32.
    Fast Smelter (non configurable) : Les minerais placés dans les fours sont cuits trois fois plus rapidement, accélérant la production de ressources.
    Diamond Limit (Configurable) : Permet de définir une limite pour la quantité de diamants que les joueurs peuvent collecter.
    Gold Limit (Configurable) : Permet de définir une limite pour la quantité d'or que les joueurs peuvent collecter.
    Speedy Miner (non configurable) : Accorde l'effet Speed I aux joueurs sous la couche 32, améliorant leur vitesse de déplacement.
    NoFall (non configurable) : Empêche les joueurs de subir des dégâts dus aux chutes.
    Beta Zombies (Configurable % de drop) : Les zombies ont une chance de laisser tomber des plumes, avec un taux de drop configurable.
    All Stone = Cobblestone (non configurable) : La diorite, l'andésite, etc., ainsi que la pierre, sont transformées en cobblestone lorsqu'elles sont minés.
    Direct To Inventory (non configurable) : Les minerais miner vont directement dans l'inventaire des joueurs.
    Vein Miner (non configurable) : Lorsqu'un minerai est miner, tous les blocs de minerai connectés sont également casser simultanément.
    Double Ores (non configurable) : Chaque minerai miner en donne deux.
    Triple Ores (non configurable) : Chaque minerai miner en donne trois.
    No Nametag (non configurable) : Masque les pseudonymes des joueurs, ajoutant un élément de mystère au jeu.
    Ironman (non configurable) : Récompense le dernier joueur à ne pas prendre de dégâts avec 2 pommes d'or.
    Unbreakable (non configurable) : Rend tous les outils et armures incassables, éliminant la nécessité de réparation.
    No Fire (non configurable) : Annule les dégâts causés par le feu, garantissant la sécurité des joueurs.
    Ultra Apple (Configurable) : Les joueurs reçoivent périodiquement une pomme d'or comme récompense.
    Golden Head (non configurable) : Permet la fabrication d'une pomme d'or qui régénère 4 cœurs en utilisant la tête d'un joueur éliminé.
    Master Level (non configurable) : Accorde à tous les joueurs 10 000 niveaux d'EXP, facilitant l'enchantement des objets.
    No Nether (non configurable) : Empêche l'accès au Nether.
    MinHP (non configurable) : Empêche les joueurs de descendre en dessous de 3 cœurs avant l'activation du PvP.
    No Rod (non configurable) : Interdit le craft/utilisation de la canne à pêche
    Final Heal (Configurable) (item = pepite d'or): configurer le soin de tout les joueurs de la partie. (modifiable de 1min par clic par defaut a 20)
     */
}
