package fr.altaks.uhcapi2.controllers;

import fr.altaks.uhcapi2.core.IController;

import java.util.HashMap;

public class WorldsController implements IController {

    /**
     * The default world generation parameters.
     */
    private final String defaultWorldGenerationParameters =
            "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";

    /**
     * The world generation boosts that will be used to generate the world.
     * @return
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

    public WorldsController(){
        for(BoostType type : BoostType.values()){
            boosts.put(type, 1f);
        }
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
        REDSTONE("redstoneSize"),
        LAPIS("lapisSize"),
        COAL("coalSize"),
        GOLD("goldSize"),
        DIAMOND("diamondSize"),
        IRON("ironSize"),
        CAVE("");

        /**
         * The name of the property in the world generation JSON parameters.
         */
        String jsonPropertyName;

        /**
         * Defines a boost type.
         * @param jsonPropertyName
         */
        private BoostType(String... jsonPropertyName){

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
