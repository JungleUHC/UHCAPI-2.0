package fr.altaks.uhcapi2.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.altaks.uhcapi2.Main;
import fr.altaks.uhcapi2.core.IController;
import fr.altaks.uhcapi2.core.util.worldmanip.DynamicClassFunctions;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorldsController implements IController {

    /**
     * The default world generation parameters.
     */
    private final String defaultWorldGenerationParameters =
            "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";

    /**
     * The world generation boosts that will be used to generate the world.
     * @return the world generation boosts that will be used to generate the world.
     */
    public HashMap<BoostType, Float> getBoosts() {
        return boosts;
    }


    /**
     * The world generation boosts that will be used to generate the world.
     */
    private final HashMap<BoostType, Float> boosts = new HashMap<>();

    private boolean enableVillages = true;
    private boolean enableStrongholds = true;
    private boolean enableMineshafts = true;

    public void switchStructureActivationStatus(StructureType type){
        switch (type) {
            case VILLAGE:
                this.enableVillages  = !enableVillages;
            break;
            case STRONGHOLD:
                this.enableStrongholds  = !enableStrongholds;
            break;
            case MINESHAFT:
                this.enableMineshafts  = !enableMineshafts;
            break;
            default:
                throw new IllegalArgumentException("Unknown structure type " + type.name());

        }
    }

    public boolean isStructureEnabled(StructureType type){
        switch (type) {
            case VILLAGE:
                return enableVillages;
            case STRONGHOLD:
                return enableStrongholds;
            case MINESHAFT:
                return enableMineshafts;
            default:
                throw new IllegalArgumentException("Unknown structure type " + type.name());

        }
    }

    private BukkitTask generationTask;
    private long generationStartTime;

    /**
     * Indicates if the world generation is currently running (it has a 10 sec delay before starting).
     * @return
     */
    private boolean isGenerationCancellable(){
        return System.currentTimeMillis() < generationStartTime + 10000;
    }

    public void startWorldGeneration(){

        if(this.generationTask != null && !isGenerationCancellable()) return;

        this.main.getGameManager().getHost().sendMessage(Main.MSG_PREFIX + ChatColor.GREEN + "Démarrage de la génération du monde dans 10 secondes...");
        for(Player coHost : this.main.getGameManager().getCoHosts()){
            coHost.sendMessage(Main.MSG_PREFIX + ChatColor.GREEN + "Démarrage de la génération du monde dans 10 secondes...");
        }

        // Create the world generation JSON parameters
        JsonObject worldGenerationParameters = new JsonParser().parse(defaultWorldGenerationParameters).getAsJsonObject();

        Main.logDebug("Creating new generation parameters...");
        for(Map.Entry<BoostType, Float> entry : this.boosts.entrySet()){
            worldGenerationParameters.addProperty(entry.getKey().getJsonPropertyName(), entry.getValue() * entry.getKey().getDefaultValue());
            Main.logDev(" | " + entry.getKey().getJsonPropertyName() + " : " + entry.getValue());
        }

        worldGenerationParameters.addProperty("useVillages", this.enableVillages);
        worldGenerationParameters.addProperty("useStrongholds", this.enableStrongholds);
        worldGenerationParameters.addProperty("useMineShafts", this.enableMineshafts);

        Main.logDebug("New world generation parameters : " + worldGenerationParameters);

        this.generationStartTime = System.currentTimeMillis();
        Main.logDev("Starting world generation in 10 seconds... [Timestamp : " + this.generationStartTime + "]");
        Main.logDev("Awaiting for world regeneration...");
        this.generationTask = new BukkitRunnable(){


            @Override
            public void run() {

                Main.logDev("Awaiting for world unloading...");

                World gameWorld = Bukkit.getWorld("game");
                World safeWorld = Bukkit.getWorld("world");

                if(gameWorld != null){
                    // Prepare the world unload
                    gameWorld.setKeepSpawnInMemory(false);
                    // Unload the game worlds
                    Bukkit.unloadWorld("game", false);

                    DynamicClassFunctions.bindRegionFiles();
                    DynamicClassFunctions.forceUnloadWorld(gameWorld, safeWorld.getSpawnLocation());
                    DynamicClassFunctions.clearWorldReference(gameWorld.getName());

                    try {
                        FileUtils.deleteDirectory(gameWorld.getWorldFolder());
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    Main.logDev("Unloaded the game world.");
                }


                Main.logDev("Awaiting for world creation...");
                // Prepare the new world creation

                WorldCreator creator = new WorldCreator("game");
                creator.environment(World.Environment.NORMAL);
                creator.type(WorldType.CUSTOMIZED);
                creator.generatorSettings(worldGenerationParameters.toString());

                // determine a random seed among the seeds list in the config
                ArrayList<Long> seeds = new ArrayList<>();
                for(String seed : main.getConfig().getStringList("seeds")){
                    seeds.add(Long.parseLong(seed));
                }

                long randomSeed = seeds.get((int) (Math.random() * seeds.size()));
                creator.seed(randomSeed);
                creator.createWorld();

                Main.logDebug("Created the game world.");

                // Preload chunks around spawn
                World newGameWorld = Bukkit.getWorld("game");
                newGameWorld.setKeepSpawnInMemory(true);
                for(int x = -8; x < 8; x++){
                    for(int z = -8; z < 8; z++){
                        newGameWorld.unloadChunk(x, z, false);
                        newGameWorld.regenerateChunk(x, z);
                    }
                }

                // set the generation task to null
                generationTask = null;
                generationStartTime = 0;
                Main.logDev("Generation task values cleared");

            }

        }.runTaskLater(this.main, 10 * 20);


    }

    /**
     * Cancels the world generation if it is still cancellable.
     * @return true if the cancellation was successful, false otherwise.
     */
    public boolean cancelWorldGeneration(){
        // If start time is less than 10 sec, cancel the task.
        if(generationTask != null && isGenerationCancellable()){
            this.generationTask.cancel();
            this.generationTask = null;
            this.generationStartTime = 0;
            this.main.getGameManager().getHost().sendMessage(Main.MSG_PREFIX + ChatColor.RED + "La génération du monde a été annulée.");
            for(Player coHost : this.main.getGameManager().getCoHosts()){
                coHost.sendMessage(Main.MSG_PREFIX + ChatColor.RED + "La génération du monde a été annulée.");
            }
            Main.logDebug("World generation cancelled.");
            return true;
        }
        return false;
    }

    private Main main;

    public WorldsController(Main main){
        for(BoostType type : BoostType.values()){
            boosts.put(type, 1f);
        }
        this.main = main;
    }

    public void onGameStart() {

    }

    /**
     * Allows to increase the boost of a specific ore or the cave generation system.
     * @param type the type of boost to increase
     * @param boost the amount of boost to increase
     */
    public void increaseBoost(BoostType type, float boost) {

        float newValue = boosts.get(type) + boost;
        if(newValue > 4f) newValue = 4f;

        boosts.put(type, newValue);
    }

    /**
     * Allows to decrease the boost of a specific ore or the cave generation system.
     * @param type the type of boost to decrease
     * @param boost the amount of boost to decrease
     */
    public void decreaseBoost(BoostType type, float boost) {

        float newValue = boosts.get(type) - boost;
        if(newValue < 0f) newValue = 0f;

        boosts.put(type, newValue);
    }

    /**
     * Defines all the boosts that can be applied to the world generation.
     */
    public enum BoostType {
        REDSTONE("redstoneSize", 19.0f),
        LAPIS("lapisSize", 25.0f),
        COAL("coalSize", 17.0f),
        GOLD("goldSize", 22.0f),
        DIAMOND("diamondSize", 20.0f),
        IRON("ironSize", 27.0f),
        CAVE("depthNoiseScaleExponent", 0.5f);

        /**
         * The name of the property in the world generation JSON parameters.
         */
        private String jsonPropertyName;
        private float defaultValue;

        /**
         * Defines a boost type.
         * @param jsonPropertyName
         */
        BoostType(String jsonPropertyName, float defaultValue){
            this.jsonPropertyName = jsonPropertyName;
            this.defaultValue = defaultValue;
        }

        /**
         * Returns the name of the property in the world generation JSON parameters.
         * @return the name of the property in the world generation JSON parameters.
         */
        public String getJsonPropertyName() {
            return jsonPropertyName;
        }

        public float getDefaultValue() {
            return defaultValue;
        }
    }

    /**
     * Defines all the structures that can be enabled or disabled in the world generation.
     */
    public enum StructureType {
        VILLAGE("useVillages"),
        STRONGHOLD("useStrongholds"),
        MINESHAFT("useMineShafts");

        /**
         * The name of the property in the world generation JSON parameters.
         */
        String jsonPropertyName;

        /**
         * Defines a structure type.
         * @param jsonPropertyName the name of the property in the world generation JSON parameters.
         */
        private StructureType(String... jsonPropertyName){

        }
    }
}
