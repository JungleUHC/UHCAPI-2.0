package fr.altaks.uhcapi2.core.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

public class HeadBuilder {

    public static ItemBuilder of(String texture){
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        GameProfile profile = new GameProfile(java.util.UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", texture));


        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        item.setItemMeta(meta);
        return new ItemBuilder(item);
    }
}
