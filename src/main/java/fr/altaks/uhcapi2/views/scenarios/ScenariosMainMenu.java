package fr.altaks.uhcapi2.views.scenarios;

import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage.*;
import fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage.*;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScenariosMainMenu extends FastInv {

    private HostMainMenu upperMenu;

    private ScenariosMainSecondPageMenu scenariosMainSecondPageMenu;

    private String SECOND_PAGE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjYzMTRkMzFiMDk1ZTRkNDIxNzYwNDk3YmU2YTE1NmY0NTlkOGM5OTU3YjdlNmIxYzEyZGViNGU0Nzg2MGQ3MSJ9fX0=";

    private ItemStack secondPage = HeadBuilder.of(SECOND_PAGE_VALUE)
            .name("Page suivante")
            .build();

    private List<Scenario> scenarios = Arrays.asList(
            new CatEyes(), new CutClean(), new GigaDrill(), new HasteyBabies(), new HasteyBoys(), new StarterTools(), new TimberPvP(),
            new SafeMiner(), new FastSmelter(), new DiamondLimit(), new GoldLimit(), new SpeedyMiner(), new NoFall(), new BetaZombies(),
            new AllStone(), new DirectToInventory(), new VeinMiner(), new DoubleOres(), new TripleOres(), new NoNametag(), new IronMan(),

            new Unbreakable(), new GoldenHead(), new MasterLevel(), new NoFire(), new NoNether(), new NoRod(), new UltraApple(), new MinHP()
    );

    public HashMap<ItemStack, Scenario> scenarioItems = new HashMap<>();

    public ScenariosMainMenu(HostMainMenu upperMenu) {
        super(6*9, "Configuration des scenarios");

        scenariosMainSecondPageMenu = new ScenariosMainSecondPageMenu(upperMenu, this);

        setItems(getCorners(), ItemBuilder.FILLING_PANE);

        setItem(41, secondPage, e -> scenariosMainSecondPageMenu.open((Player) e.getWhoClicked()));

        for(Scenario scenario : scenarios){

            // Make the lore string wrap at 30 characters
            String[] wrappedLore = wrapLore(scenario.getDescription(), 30);

            // Generate the scenario item
            ItemBuilder itemBuilder = scenario.getIcon()
                    .name(ChatColor.YELLOW + scenario.getName())
                    .lore(wrappedLore)
                    .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

            if(scenario.isConfigurable()){
                itemBuilder.addLore("", ChatColor.GREEN + "Cliquez pour configurer ce scenario");
            }

            // place the scenario item in the menu
            if(scenario.getSlot() < 6*9){
                setItem(scenario.getSlot(), itemBuilder.build());
            } else {
                scenariosMainSecondPageMenu.setItem(scenario.getSlot() - 6*9, itemBuilder.build());
            }
        }

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    private String[] wrapLore(String loreToWrap, int charsAmountPerLine) {
        String[] description = loreToWrap.split(" ");
        StringBuilder lore = new StringBuilder();
        lore.append("\n" + ChatColor.GRAY);
        int lineLength = 0;
        for(String word : description){
            if(lineLength + word.length() > charsAmountPerLine+2){
                lore.append("\n" + ChatColor.GRAY);
                lineLength = 0;
            }
            lore.append(word).append(" ");
            lineLength += word.length();
        }
        return lore.toString().split("\n");
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

}
