package fr.altaks.uhcapi2.views.world.submenus;

import fr.altaks.uhcapi2.controllers.WorldsController;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.views.world.WorldMainMenu;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorldCaveBoostSubMenu extends FastInv {

    private WorldMainMenu upperMenu;

    private final String
        REDSTONE_BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY5YTFmMTE0MTUxYjQ1MjEzNzNmMzRiYzE0YzI5NjNhNTAxMWNkYzI1YTY1NTRjNDhjNzA4Y2Q5NmViZmMifX19",
        LAPIS_BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTEwMDFiNDI1MTExYmZlMGFjZmY3MTBhOGI0MWVhOTVlM2I5MzZhODVlNWJiNjUxNzE2MGJhYjU4N2U4ODcwZiJ9fX0=",
        COAL_BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYxYzU3OTc0ZjEwMmQzZGViM2M1M2Q0MmZkZTkwOWU5YjM5Y2NiYzdmNzc2ZTI3NzU3NWEwMmQ1MWExOTk5ZSJ9fX0=",
        GOLD_BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzNiYzk2NWQ1NzljM2M2MDM5ZjBhMTdlYjdjMmU2ZmFmNTM4YzdhNWRlOGU2MGVjN2E3MTkzNjBkMGE4NTdhOSJ9fX0=",
        DIAMOND_BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTFlZDlhYmY1MWZlNGVhODRjZmNiMjcyOTdmMWJjNTRjZDM4MmVkZjg1ZTdiZDZlNzVlY2NhMmI4MDY2MTEifX19",
        IRON_BOOST_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAxODQzZWM0M2YwODhjOTYzZmZjM2UyZjcxYzY2ZTMxNTU5NDNiMTc3YTFhMzU5ODJiMTIwZjZmNjQ4MjJiYyJ9fX0=";

    private final String CAVE_BOOST_VALUE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NkYjczYjQzMzZkM2ExMzM4MzQ0MjljMGVkY2RlMzM3M2RkNjRlZTIyMWUwY2Y3N2JlODE0OTQxMTYwYTQ0YSJ9fX0=";

    private ItemStack redstoneBoost = HeadBuilder.of(REDSTONE_BOOST_VALUE)
            .name("Boost de la redstone")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1"
            )
            .build();

    private ItemStack lapisBoost = HeadBuilder.of(LAPIS_BOOST_VALUE)
            .name("Boost du Lapis-Lazuli")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1"
            )
            .build();

    private ItemStack coalBoost = HeadBuilder.of(COAL_BOOST_VALUE)
            .name("Boost du charbon")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1"
            )
            .build();

    private ItemStack goldBoost = HeadBuilder.of(GOLD_BOOST_VALUE)
            .name("Boost de l'or")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1"
            )
            .build();

    private ItemStack diamondBoost = HeadBuilder.of(DIAMOND_BOOST_VALUE)
            .name("Boost du diamant")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1"
            )
            .build();

    private ItemStack ironBoost = HeadBuilder.of(IRON_BOOST_VALUE)
            .name("Boost du fer")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1"
            )
            .build();

    private ItemStack caveBoost = HeadBuilder.of(CAVE_BOOST_VALUE)
            .name("Boost des caves")
            .lore(
                    "",
                    ChatColor.YELLOW + "Multiplicateur : " + 1 + "x",
                    "",
                    ChatColor.GRAY + "Clic gauche : +0.1",
                    ChatColor.GRAY + "Clic droit : -0.1",
                    ChatColor.GRAY + "Permet de modifier la fréquence d'apparition des caves"
            )
            .build();

    private GameManager manager;
    private HashMap<Integer, WorldsController.BoostType> slotsToBoostType = new HashMap<>();

    public WorldCaveBoostSubMenu(GameManager manager, WorldMainMenu upperMenu) {
        super(5*9, "Boost des caves");
        this.upperMenu = upperMenu;
        this.manager = manager;

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        // 11, 13, 15,
        //     22
        // 29, 31, 33

        setItem(11, redstoneBoost);
        setItem(13, lapisBoost);
        setItem(15, coalBoost);

        setItem(22, caveBoost);

        setItem(29, goldBoost);
        setItem(31, diamondBoost);
        setItem(33, ironBoost);

        slotsToBoostType.put(11, WorldsController.BoostType.REDSTONE);
        slotsToBoostType.put(13, WorldsController.BoostType.LAPIS);
        slotsToBoostType.put(15, WorldsController.BoostType.COAL);
        slotsToBoostType.put(22, WorldsController.BoostType.CAVE);
        slotsToBoostType.put(29, WorldsController.BoostType.GOLD);
        slotsToBoostType.put(31, WorldsController.BoostType.DIAMOND);
        slotsToBoostType.put(33, WorldsController.BoostType.IRON);

        // Set the return arrow
        setItem(40, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // Check interaction validity
        if(event.getClickedInventory() == null || event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if(slotsToBoostType.containsKey(event.getSlot())){
            if (event.isLeftClick()) {
                // Increase boost
                manager.getWorldsController().increaseBoost(slotsToBoostType.get(event.getSlot()), .1f);
            } else if (event.isRightClick()) {
                // Decrease boost
                manager.getWorldsController().decreaseBoost(slotsToBoostType.get(event.getSlot()), .1f);
            }

            // Update item lore

            updateItemLoreAccordingToNewBoostValue(event, manager.getWorldsController().getBoosts().get(slotsToBoostType.get(event.getSlot())));
        }
    }

    private static void updateItemLoreAccordingToNewBoostValue(InventoryClickEvent event, float newValue) {
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(1, ChatColor.YELLOW + "Multiplicateur : " + NumberFormat.getNumberInstance(Locale.FRANCE).format((double) newValue) + "x");
        meta.setLore(lore);
        event.getCurrentItem().setItemMeta(meta);
    }
}
