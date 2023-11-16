package fr.altaks.uhcapi2.views.scenarios;

import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.GameManager;
import fr.altaks.uhcapi2.core.util.HeadBuilder;
import fr.altaks.uhcapi2.views.HostMainMenu;
import fr.altaks.uhcapi2.views.scenarios.scenarios.firstpage.*;
import fr.altaks.uhcapi2.views.scenarios.scenarios.secondpage.*;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScenariosMainMenu extends FastInv {

    private HostMainMenu upperMenu;

    private final ScenariosMainSecondPageMenu scenariosMainSecondPageMenu;

    private final String SECOND_PAGE_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjYzMTRkMzFiMDk1ZTRkNDIxNzYwNDk3YmU2YTE1NmY0NTlkOGM5OTU3YjdlNmIxYzEyZGViNGU0Nzg2MGQ3MSJ9fX0=";

    private final ItemStack secondPage = HeadBuilder.of(SECOND_PAGE_VALUE)
            .name("Page suivante")
            .build();

    private final List<Scenario> scenarios = Arrays.asList(
            new CatEyes(), new CutClean(), new GigaDrill(), new HasteyBabies(), new HasteyBoys(), new StarterTools(), new TimberPvP(),
            new SafeMiner(), new FastSmelter(), new DiamondLimit(), new GoldLimit(), new SpeedyMiner(), new NoFall(), new BetaZombies(),
            new AllStone(), new DirectToInventory(), new VeinMiner(), new DoubleOres(), new TripleOres(), new NoNametag(), new IronMan(),

            new Unbreakable(), new GoldenHead(), new MasterLevel(), new NoFire(), new NoNether(), new NoRod(), new UltraApple(), new MinHP()
    );

    private final HashMap<Integer, Scenario> scenariosSlots = new HashMap<>();

    public GameManager getManager() {
        return manager;
    }

    private final GameManager manager;

    public ScenariosMainMenu(GameManager manager, HostMainMenu upperMenu) {
        super(6*9, "Configuration des scenarios");
        this.manager = manager;
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

            ItemStack finalItem = itemBuilder.build();
            changeItemVisualActivationState(scenario, finalItem, false);

            // place the scenario item in the menu
            if(scenario.getSlot() < 6*9){
                setItem(scenario.getSlot(), finalItem);
            } else {
                scenariosMainSecondPageMenu.setItem(scenario.getSlot() - 6*9, finalItem);
            }
            scenariosSlots.put(scenario.getSlot(), scenario);
        }

        // Set the return arrow
        setItem(49, new ItemBuilder(Material.ARROW).name("Retour").build(),
                e -> upperMenu.open((Player) e.getWhoClicked())
        );
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // checks to avoid NPEs
        if(event.getClickedInventory() == null || event.getView().getBottomInventory() == event.getClickedInventory()) return;
        if(!scenariosSlots.containsKey(event.getSlot())) return;
        if(Arrays.asList(49, 41).contains(event.getSlot())) return; // return arrow or second page

        // get the scenario from the clicked item
        Scenario scenario = scenariosSlots.get(event.getSlot());
        if(scenario == null) throw new RuntimeException("Scenario not found");

        // add glowing effect to the item if it was in the selected scenarios list
        ItemStack item = event.getCurrentItem();
        manager.getScenariosController().switchScenarioActivationState(scenario);
        changeItemVisualActivationState(scenario, item, manager.getScenariosController().getScenariosToEnable().contains(scenario));
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

    public HashMap<Integer, Scenario> getScenariosSlots() {
        return scenariosSlots;
    }

    public void changeItemVisualActivationState(Scenario scenario, ItemStack item, boolean isEnabled){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + scenario.getName() + " " + (isEnabled ? ChatColor.GREEN + "[Activé]" : ChatColor.RED + "[Désactivé]"));
        item.setItemMeta(meta);
        if(isEnabled){
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        } else {
            item.removeEnchantment(Enchantment.DURABILITY);
        }
    }

}
