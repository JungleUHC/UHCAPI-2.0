package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.controllers.game.GameBorderController;
import fr.altaks.uhcapi2.controllers.game.GameInvsController;
import fr.altaks.uhcapi2.controllers.game.GameMobsController;
import fr.altaks.uhcapi2.controllers.game.GameStuffController;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.IController;
import org.bukkit.enchantments.Enchantment;

public class GameController implements IController {

    private Main main;
    private GameManager manager;

    private GameBorderController gameBorderController;
    private GameInvsController gameInvsController;
    private GameMobsController gameMobsController;
    private GameStuffController gameStuffController;

    public GameController(GameManager manager, Main main){
        this.main = main;
        this.manager = manager;

        this.gameBorderController = new GameBorderController(main);
        this.gameInvsController = new GameInvsController(main);
        this.gameMobsController = new GameMobsController(main);
        this.gameStuffController = new GameStuffController(main);
    }

    @Override
    public void onGameStart() {

    }

    public String getEnchantmentName(Enchantment enchantment){
        switch (enchantment.getName()){
            case "ARROW_DAMAGE":
                return "Power";
            case "ARROW_FIRE":
                return "Flame";
            case "ARROW_INFINITE":
                return "Infinity";
            case "ARROW_KNOCKBACK":
                return "Punch";
            case "BINDING_CURSE":
                return "Curse of Binding";
            case "DAMAGE_ALL":
                return "Sharpness";
            case "DAMAGE_ARTHROPODS":
                return "Bane of Arthropods";
            case "DAMAGE_UNDEAD":
                return "Smite";
            case "DEPTH_STRIDER":
                return "Depth Strider";
            case "DIG_SPEED":
                return "Efficiency";
            case "DURABILITY":
                return "Unbreaking";
            case "FIRE_ASPECT":
                return "Fire Aspect";
            case "FROST_WALKER":
                return "Frost Walker";
            case "KNOCKBACK":
                return "Knockback";
            case "LOOT_BONUS_BLOCKS":
                return "Fortune";
            case "LOOT_BONUS_MOBS":
                return "Looting";
            case "LUCK":
                return "Luck of the Sea";
            case "LURE":
                return "Lure";
            case "MENDING":
                return "Mending";
            case "OXYGEN":
                return "Respiration";
            case "PROTECTION_ENVIRONMENTAL":
                return "Protection";
            case "PROTECTION_EXPLOSIONS":
                return "Blast Protection";
            case "PROTECTION_FALL":
                return "Feather Falling";
            case "PROTECTION_FIRE":
                return "Fire Protection";
            case "PROTECTION_PROJECTILE":
                return "Projectile Protection";
            case "SILK_TOUCH":
                return "Silk Touch";
            case "SWEEPING_EDGE":
                return "Sweeping Edge";
            case "THORNS":
                return "Thorns";
            case "VANISHING_CURSE":
                return "Cure of Vanishing";
            case "WATER_WORKER":
                return "Aqua Affinity";
            default:
                return "Unknown";
        }

    };

    public GameBorderController getGameBorderController() {
        return gameBorderController;
    }

    public GameInvsController getGameInvsController() {
        return gameInvsController;
    }

    public GameMobsController getGameMobsController() {
        return gameMobsController;
    }

    public GameStuffController getGameStuffController() {
        return gameStuffController;
    }
}
