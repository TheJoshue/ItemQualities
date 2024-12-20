package com.abraxas.itemqualities;

import org.bukkit.Material;

import java.util.*;

import static com.abraxas.itemqualities.api.APIUtils.getGson;

public class Config {
    public boolean debugMode = true;
    public String prefix = "&bItemQualities &7» ";
    public String itemQualityDisplayFormat = "{QUALITY} &r{ITEM}";
    public boolean exampleItemQualitiesEnabled = true;
    public boolean displayQualityInLore = false;
    public boolean displayDurabilityLineIfMaxDurabilityHasOffset = true;
    public boolean applyQualityOnCraft = true;
    public boolean rerollQualityOnSmith = false;
    public boolean reforgeStationEnabled = true;
    public boolean damageAnvilOnReforge = true;
    public boolean reforgeTierDependsOnAnvilDamage = true;
    public Locale locale = Locale.ENGLISH;
    public List<Material> itemBlacklist = new ArrayList<>() {{
        add(Material.CARROT_ON_A_STICK);
        add(Material.WARPED_FUNGUS_ON_A_STICK);
    }};
    public Map<Material, Integer> reforgeEXPLevelCosts = new LinkedHashMap<>() {{
        put(Material.WOODEN_SWORD, 1);
        put(Material.WOODEN_AXE, 1);
        put(Material.WOODEN_PICKAXE, 1);
        put(Material.WOODEN_SHOVEL, 1);
        put(Material.WOODEN_HOE, 1);
        put(Material.LEATHER_HELMET, 1);
        put(Material.LEATHER_CHESTPLATE, 1);
        put(Material.LEATHER_LEGGINGS, 1);
        put(Material.LEATHER_BOOTS, 1);

        put(Material.FLINT_AND_STEEL, 1);
        put(Material.FIRE_CHARGE, 1);
        put(Material.SHEARS, 1);
        put(Material.BOW, 4);
        put(Material.CROSSBOW, 6);
        put(Material.TRIDENT, 13);
        put(Material.ELYTRA, 20);
        put(Material.FISHING_ROD, 2);

        put(Material.STONE_SWORD, 3);
        put(Material.STONE_AXE, 3);
        put(Material.STONE_PICKAXE, 3);
        put(Material.STONE_SHOVEL, 3);
        put(Material.STONE_HOE, 3);

        put(Material.GOLDEN_SWORD, 2);
        put(Material.GOLDEN_AXE, 2);
        put(Material.GOLDEN_PICKAXE, 2);
        put(Material.GOLDEN_SHOVEL, 2);
        put(Material.GOLDEN_HOE, 2);
        put(Material.GOLDEN_HELMET, 2);
        put(Material.GOLDEN_CHESTPLATE, 2);
        put(Material.GOLDEN_LEGGINGS, 2);
        put(Material.GOLDEN_BOOTS, 2);

        put(Material.IRON_SWORD, 4);
        put(Material.IRON_AXE, 4);
        put(Material.IRON_PICKAXE, 4);
        put(Material.IRON_SHOVEL, 4);
        put(Material.IRON_HOE, 4);
        put(Material.IRON_HELMET, 4);
        put(Material.IRON_CHESTPLATE, 4);
        put(Material.IRON_LEGGINGS, 4);
        put(Material.IRON_BOOTS, 4);

        put(Material.DIAMOND_SWORD, 7);
        put(Material.DIAMOND_AXE, 7);
        put(Material.DIAMOND_PICKAXE, 7);
        put(Material.DIAMOND_SHOVEL, 7);
        put(Material.DIAMOND_HOE, 7);
        put(Material.DIAMOND_HELMET, 7);
        put(Material.DIAMOND_CHESTPLATE, 7);
        put(Material.DIAMOND_LEGGINGS, 7);
        put(Material.DIAMOND_BOOTS, 7);

        put(Material.NETHERITE_SWORD, 10);
        put(Material.NETHERITE_AXE, 10);
        put(Material.NETHERITE_PICKAXE, 10);
        put(Material.NETHERITE_SHOVEL, 10);
        put(Material.NETHERITE_HOE, 10);
        put(Material.NETHERITE_HELMET, 10);
        put(Material.NETHERITE_CHESTPLATE, 10);
        put(Material.NETHERITE_LEGGINGS, 10);
        put(Material.NETHERITE_BOOTS, 10);
    }};

    public static Config deserialize(String json) {
        return getGson().fromJson(json, Config.class);
    }

    public static String serialize(Config item) {
        return getGson().toJson(item, Config.class);
    }
}
