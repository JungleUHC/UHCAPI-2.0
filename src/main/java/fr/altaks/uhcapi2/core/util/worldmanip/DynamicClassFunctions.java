package fr.altaks.uhcapi2.core.util.worldmanip;

import fr.altaks.uhcapi2.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicClassFunctions {

    public static final HashMap<String, Class<?>> classes = new HashMap<>();
    public static final HashMap<String, Method> methods = new HashMap<>();
    public static final HashMap<String, Field> fields = new HashMap<>();
    public static String nmsPackage = "net.minecraft.server.v1_8_R3";
    public static String obcPackage = "org.bukkit.craftbukkit.v1_8_R3";
    //methods
    @SuppressWarnings("rawtypes")
    private static HashMap regionfiles;
    private static Field rafField;

    public static boolean setPackages() {
        Server craftServer = Bukkit.getServer();
        if(craftServer != null) {
            try {
                Class<?> craftClass = craftServer.getClass();
                Method getHandle = craftClass.getMethod("getHandle");
                Class<?> returnType = getHandle.getReturnType();

                obcPackage = craftClass.getPackage().getName();
                nmsPackage = returnType.getPackage().getName();
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static boolean setClasses() {
        try {
            // org.bukkit.craftbukkit
            classes.put("CraftServer", Class.forName(obcPackage + ".CraftServer"));
            classes.put("CraftWorld", Class.forName(obcPackage + ".CraftWorld"));
            classes.put("CraftFallingSand", Class.forName(obcPackage + ".entity.CraftFallingSand"));

            // net.minecraft.server
            classes.put("MinecraftServer", Class.forName(nmsPackage + ".MinecraftServer"));
            classes.put("RegionFile", Class.forName(nmsPackage + ".RegionFile"));
            classes.put("RegionFileCache", Class.forName(nmsPackage + ".RegionFileCache"));
            classes.put("WorldData", Class.forName(nmsPackage + ".WorldData"));
            classes.put("WorldServer", Class.forName(nmsPackage + ".WorldServer"));
            classes.put("EntityFallingBlock", Class.forName(nmsPackage + ".EntityFallingBlock"));

            return true;
        } catch (Exception e) {
            Main.logDebug("Could not acquire a required class");
            return false;
        }
    }

    public static boolean setMethods() {
        try {
            // org.bukkit.craftbukkit
            methods.put("CraftWorld.getHandle()", classes.get("CraftWorld").getDeclaredMethod("getHandle"));
            methods.put("CraftServer.getServer()", classes.get("CraftServer").getDeclaredMethod("getServer"));
            methods.put("CraftFallingSand.getHandle()", classes.get("CraftFallingSand").getDeclaredMethod("getHandle"));

            // net.minecraft.server

            return true;
        } catch (Exception e) {
            Main.logDebug("Could not find a required method");
            return false;
        }
    }

    public static boolean setFields() {
        try {
            fields.put("RegionFileCache.regionsByFilename", classes.get("RegionFileCache").getDeclaredField("a"));        // obfuscated - regionsByFilename in RegionFileCache
            fields.put("RegionFile.dataFile", classes.get("RegionFile").getDeclaredField("c"));                            // obfuscated - dataFile in RegionFile

            fields.put("EntityFallingBlock.hurtEntities", classes.get("EntityFallingBlock").getDeclaredField("hurtEntities"));
            fields.put("EntityFallingBlock.fallHurtAmount", classes.get("EntityFallingBlock").getDeclaredField("fallHurtAmount"));
            fields.put("EntityFallingBlock.fallHurtMax", classes.get("EntityFallingBlock").getDeclaredField("fallHurtMax"));

            fields.put("MinecraftServer.worlds", classes.get("MinecraftServer").getDeclaredField("worlds"));

            fields.put("CraftServer.worlds", classes.get("CraftServer").getDeclaredField("worlds"));
            //fields.put("RegionFile.dataFile", classes.get("RegionFile").getDeclaredField("c"));
            return true;
        } catch (Exception e) {
            Main.logDebug(" Could not find a field class");
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    public static void bindRegionFiles() {
        try {
            fields.get("RegionFileCache.regionsByFilename").setAccessible(true);
            regionfiles = (HashMap) fields.get("RegionFileCache.regionsByFilename").get(null);
            rafField = fields.get("RegionFile.dataFile");
            rafField.setAccessible(true);
            Main.logDebug("Successfully bound to region file cache.");
        } catch (Throwable t) {
            Main.logDebug("Error binding to region file cache.");
            t.printStackTrace();
        }
    }

    public static void unbindRegionFiles() {
        regionfiles = null;
        rafField = null;
    }

    @SuppressWarnings("rawtypes")
    public static synchronized boolean clearWorldReference(String worldName) {
        if(regionfiles == null) return false;
        if(rafField == null) return false;

        ArrayList<Object> removedKeys = new ArrayList<>();
        try {
            for(Object o : regionfiles.entrySet()){
                Map.Entry e = (Map.Entry) o;
                File f = (File) e.getKey();

                if(f.toString().startsWith("." + File.separator + worldName)) {
                    try {
                        RandomAccessFile raf = (RandomAccessFile) rafField.get(e.getValue());
                        raf.close();
                        removedKeys.add(f);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            Main.logDebug("Exception while removing world reference for '" + worldName + "'!");
            ex.printStackTrace();
        }
        for(Object key : removedKeys)
            regionfiles.remove(key);

        return true;
    }

    @SuppressWarnings("unchecked")
    public static void forceUnloadWorld(World world, Location backupLocationForPlayers) {
        world.setAutoSave(false);
        for(Player player : world.getPlayers())
            player.teleport(backupLocationForPlayers);

        // formerly used server.unloadWorld at this point. But it was sometimes failing, even when I force-cleared the player list

        try {
            Field f = fields.get("CraftServer.worlds");
            f.setAccessible(true);
            Map<String, World> worlds = (Map<String, World>) f.get(Bukkit.getServer());
            worlds.remove(world.getName().toLowerCase());
            f.setAccessible(false);
        } catch (IllegalAccessException ignored) {
        }

        Object ms = getMinecraftServer();

        List<Object> worldList;
        try {
            worldList = (List<Object>) fields.get("MinecraftServer.worlds").get(ms);

            int wid = worldList.indexOf(methods.get("CraftWorld.getHandle()").invoke(world));
            if(wid > -1) {
                worldList.remove(wid);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    protected static Object getMinecraftServer() {
        try {
            return methods.get("CraftServer.getServer()").invoke(Bukkit.getServer());
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return null;
    }

    public static void setFallingBlockHurtEntities(FallingBlock block, float damage, int max) {
        try {
            Object efb = methods.get("CraftFallingSand.getHandle()").invoke(block);

            Field field = fields.get("EntityFallingBlock.hurtEntities");
            field.setAccessible(true);
            field.setBoolean(efb, true);

            field = fields.get("EntityFallingBlock.fallHurtAmount");
            field.setAccessible(true);
            field.setFloat(efb, damage);

            field = fields.get("EntityFallingBlock.fallHurtMax");
            field.setAccessible(true);
            field.setInt(efb, max);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}