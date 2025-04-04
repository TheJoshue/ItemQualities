package com.abraxas.itemqualities;

import com.abraxas.itemqualities.api.ItemQualityComparator;
import com.abraxas.itemqualities.api.QualityAttributeModifier;
import com.abraxas.itemqualities.api.quality.ItemQuality;
import com.abraxas.itemqualities.api.quality.ItemQualityBuilder;
import com.abraxas.itemqualities.utils.Utils;
import com.google.common.collect.Multimap;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.abraxas.itemqualities.api.DurabilityManager.getItemDamage;
import static com.abraxas.itemqualities.api.DurabilityManager.getItemMaxDurability;
import static com.abraxas.itemqualities.api.Keys.*;
import static com.abraxas.itemqualities.api.Registries.qualitiesRegistry;
import static com.abraxas.itemqualities.utils.Utils.*;
import static org.bukkit.inventory.EquipmentSlot.*;
import static org.bukkit.persistence.PersistentDataType.INTEGER;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class QualitiesManager {
    static ItemQualities main = ItemQualities.getInstance();

    public static int lowestTier = 0;
    public static int lowTier = 1;
    public static int midTier = 2;
    public static int highTier = 3;
    public static int highestTier = 4;

    static List<ItemQuality> exampleQualities = new ArrayList<>() {{
        add(new ItemQualityBuilder(new NamespacedKey(main, "horrible"), "&cHorrible", 60, lowestTier)
                .withNoDropChance(60)
                .withAdditionalDurabilityLoss(2, 90)
                .withMaxDurabilityMod(-10)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-2,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-3))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-2))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(-0.1))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "bad"), "&cBad", 57, lowTier)
                .withNoDropChance(55)
                .withAdditionalDurabilityLoss(2, 85)
                .withMaxDurabilityMod(-7)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-1.8,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-2.6))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-1.8))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(-0.08))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "rusted"), "&cRusted", 50, lowTier)
                .withNoDropChance(49)
                .withAdditionalDurabilityLoss(2, 75)
                .withMaxDurabilityMod(-5)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-1.4,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-2.4))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-1.6))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(-0.07))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "chipped"), "&cChipped", 48, lowTier)
                .withNoDropChance(55)
                .withAdditionalDurabilityLoss(2, 85)
                .withMaxDurabilityMod(-7)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-1.1,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-2.3))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-1.4))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(-0.05))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "decent"), "&eDecent", 60, midTier)
                .withNoDropChance(45)
                .withAdditionalDurabilityLoss(2, 75)
                .withMaxDurabilityMod(-4)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-1,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-2))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-1.1))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(-0.03))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "good"), "&2Good", 50, midTier)
                .withNoDropChance(40)
                .withAdditionalDurabilityLoss(1, 65)
                .withMaxDurabilityMod(-3)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-0.5,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-1.7))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-1))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(-0.01))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "great"), "&eGreat", 48, midTier)
                .withNoDropChance(10)
                .withAdditionalDurabilityLoss(1, 15)
                .withMaxDurabilityMod(-3)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(-1,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(-0.7))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(-0.009))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "perfect"), "&aPerfect", 45, midTier)
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "legendary"), "&6Legendary", 4, highTier)
                .withNoDurabilityLossChance(60)
                .withMaxDurabilityMod(70)
                .withDoubleDropsChance(7)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(2.3,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(0.8))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(0.7))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(0.05))
                .build());

        add(new ItemQualityBuilder(new NamespacedKey(main, "godly"), "&6Godly", 3, highTier)
                .withNoDurabilityLossChance(70)
                .withMaxDurabilityMod(100)
                .withDoubleDropsChance(10)
                .withSpecificItemWhitelistMode(true)
                .withSpecificItemList(List.of(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE))
                .withAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new QualityAttributeModifier(3,
                        HAND))
                .withAttributeModifier(Attribute.GENERIC_ARMOR, new QualityAttributeModifier(1))
                .withAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new QualityAttributeModifier(1))
                .withAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new QualityAttributeModifier(0.1))
                .build());
    }};

    public static void loadAndRegister() {
        try {
            if (!Files.exists(Path.of("%s/qualities/".formatted(main.getDataFolder()))))
                Files.createDirectory(Path.of("%s/qualities".formatted(main.getDataFolder())));

            if (main.config.exampleItemQualitiesEnabled) {
                exampleQualities.forEach(i -> {
                    var path = Path.of("%s/qualities/%s.json".formatted(main.getDataFolder(), i.key.getKey()));
                    if (!Files.exists(path)) {
                        i.display = colorize(i.display);
                        var itDes = ItemQuality.serialize(i);
                        try {
                            var f = new File(String.valueOf(path));
                            f.createNewFile();
                            Files.writeString(f.toPath(), itDes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                exampleQualities.forEach(i -> {
                    var path = Path.of("%s/qualities/%s.json".formatted(main.getDataFolder(), i.key.getKey()));
                    if (Files.exists(path)) {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    qualitiesRegistry.unregister(i.key);
                });
            }
            qualitiesRegistry.getRegistry().clear();

            Utils.log(main.getTranslation("message.plugin.loading_local_quality_files"));
            var qualities = Files.list(Path.of("%s/qualities".formatted(main.getDataFolder()))).toList();
            Utils.log(main.getTranslation("message.plugin.registering_qualities"));
            qualities.forEach(itemPath -> {
                try {
                    if (!Files.isDirectory(itemPath)) { // If the file is not a directory, register it.
                        if (itemPath.getFileName().toString().endsWith(".json")) {
                            var itemFileName = itemPath.getFileName().toString().replace(".json", "");
                            var json = Files.readString(itemPath, StandardCharsets.UTF_8);
                            var quality = ItemQuality.deserialize(json);
                            if (quality.key == null) quality.key = new NamespacedKey(main, itemFileName);
                            if (quality.tier > highestTier) quality.tier = highestTier;
                            if (quality.tier < lowestTier) quality.tier = lowestTier;
                            register(quality);
                        }
                    } else { // If the file is a directory, get the files within it and register.
                        var otherQualities = Files.list(itemPath);
                        otherQualities.forEach(otherPath -> {
                            try {
                                if (!Files.isDirectory(otherPath)) {
                                    if (otherPath.getFileName().toString().endsWith(".json")) {
                                        var itemFileName = otherPath.getFileName().toString().replace(".json", "");
                                        var json = Files.readString(otherPath, StandardCharsets.UTF_8);
                                        var quality = ItemQuality.deserialize(json);
                                        if (quality.key == null)
                                            quality.key = new NamespacedKey(itemPath.getFileName().toString(), itemFileName);
                                        if (quality.tier > highestTier) quality.tier = highestTier;
                                        if (quality.tier < lowestTier) quality.tier = lowestTier;
                                        register(quality);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Utils.log(main.getTranslation("message.plugin.registration_complete"));
            Utils.log(main.getTranslation("message.plugin.custom_quality_reminder").formatted("https://github.com/Steel-Dev/ItemQualities/wiki/Creating"));
        } catch (IOException e) {
            Utils.log(main.getTranslation("message.plugin.registration_error"));
            e.printStackTrace();
        }
    }

    public static void register(ItemQuality quality) {
        if (getConfig().debugMode)
            Utils.log(main.getTranslation("message.plugin.registering_quality").formatted(quality.key));
        if (qualitiesRegistry.contains(quality.key)) {
            if (getConfig().debugMode)
                Utils.log(main.getTranslation("message.plugin.quality_already_exists").formatted(quality.key));
            return;
        }
        qualitiesRegistry.register(quality.key, quality);
        if (getConfig().debugMode)
            Utils.log(main.getTranslation("message.plugin.quality_registered").formatted(quality.key));
    }

    public static void saveQualityToFile(ItemQuality itemQuality) {
        try {
            var path = Path.of("%s/qualities/%s.json".formatted(main.getDataFolder(),
                    (!itemQuality.key.getNamespace().equals("itemqualities")) ? "%s/%s".formatted(itemQuality.key.getNamespace(), itemQuality.key.getKey()) :
                            itemQuality.key.getKey()));
            File file = new File(path.toString());
            if (!file.exists()) file.getParentFile().mkdir();
            Files.writeString(path, ItemQuality.serialize(itemQuality));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteQuality(ItemQuality itemQuality) {
        try {
            var path = Path.of("%s/qualities/%s.json".formatted(main.getDataFolder(),
                    (!itemQuality.key.getNamespace().equals("itemqualities")) ? "%s/%s".formatted(itemQuality.key.getNamespace(), itemQuality.key.getKey()) :
                            itemQuality.key.getKey()));
            Files.delete(path);
            qualitiesRegistry.unregister(itemQuality.key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack refreshItem(ItemStack itemStack) {
        return refreshItem(itemStack, null);
    }

    public static ItemStack refreshItem(ItemStack itemStack, ItemQuality updatedQuality) {
        var itemsQuality = (updatedQuality != null) ? updatedQuality : getQuality(itemStack);
        removeQualityFromItem(itemStack);
        return addQualityToItem(itemStack, (itemsQuality == null) ? getRandomQuality(itemStack) : itemsQuality);
    }

    public static ItemStack addQualityToItem(ItemStack itemStack, ItemQuality itemQuality) {
        var itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemCanHaveQuality(itemStack) || itemHasQuality(itemStack)) return itemStack;
        if (itemMeta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) return itemStack;
        if (itemQuality == null) return itemStack;

        itemMeta.getPersistentDataContainer().set(ITEM_QUALITY, STRING, itemQuality.key.getKey());

        List<String> newLore = (itemMeta.getLore() != null) ? itemMeta.getLore() : new ArrayList<>();
        if (!getConfig().displayQualityInLore) {
            var customItemName = itemMeta.getPersistentDataContainer().getOrDefault(ITEM_CUSTOM_NAME, STRING, "");
            String itemName = (!customItemName.isEmpty()) ? customItemName : new TranslatableComponent("item.minecraft.%s".formatted(itemStack.getType().toString().toLowerCase())).toPlainText();
            itemMeta.setDisplayName(colorize(getConfig().itemQualityDisplayFormat.replace("{QUALITY}", itemQuality.display).replace("{ITEM}", itemName)));
        } else {
            newLore.add(colorize("&r%s %s".formatted(itemQuality.display, main.getTranslation("lore.stat.quality"))));
            //newLore.add("");
        }

        if (itemQuality.extraDurabilityLoss > 0)
            newLore.add(colorize("&c+%s %s".formatted(itemQuality.extraDurabilityLoss, main.getTranslation("lore.stat.durability_loss").formatted(itemQuality.extraDurabilityLossChance))));
        if (itemQuality.noDurabilityLossChance > 0)
            newLore.add(colorize("&a%s".formatted(main.getTranslation("lore.stat.no_durability_loss").formatted(itemQuality.noDurabilityLossChance))));

        if (itemQuality.itemMaxDurabilityMod != 0)
            newLore.add(colorize("%s%s %s".formatted((itemQuality.itemMaxDurabilityMod < 0) ? "&c" : "&a+", itemQuality.itemMaxDurabilityMod, main.getTranslation("lore.stat.max_durability"))));

        if (isMeleeWeapon(itemStack) || isMiningTool(itemStack) || isProjectileLauncher(itemStack)) {
            if (itemQuality.noDropChance > 0)
                newLore.add(colorize("&c%s".formatted(main.getTranslation("lore.stat.no_drops").formatted(itemQuality.noDropChance))));
            else if (itemQuality.doubleDropsChance > 0)
                newLore.add(colorize("&a%s".formatted(main.getTranslation("lore.stat.double_drops").formatted(itemQuality.doubleDropsChance))));
        }

        if (itemQuality.modifiers.size() > 0) newLore.add("");

        Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> defAttributes = new HashMap<>();
        defAttributes.put(HAND, itemStack.getType().getDefaultAttributeModifiers(HAND));
        defAttributes.put(OFF_HAND, itemStack.getType().getDefaultAttributeModifiers(OFF_HAND));
        defAttributes.put(HEAD, itemStack.getType().getDefaultAttributeModifiers(HEAD));
        defAttributes.put(CHEST, itemStack.getType().getDefaultAttributeModifiers(CHEST));
        defAttributes.put(LEGS, itemStack.getType().getDefaultAttributeModifiers(LEGS));
        defAttributes.put(FEET, itemStack.getType().getDefaultAttributeModifiers(FEET));

        if (itemMeta.hasAttributeModifiers())
            itemMeta.getAttributeModifiers().forEach(itemMeta::removeAttributeModifier);

        itemQuality.modifiers.forEach((attribute, attributeModifier) -> {
            var canAdd = true;

            switch (attribute) {
                case GENERIC_ATTACK_DAMAGE:
                case GENERIC_ATTACK_SPEED:
                case GENERIC_ATTACK_KNOCKBACK:
                    if (!isMeleeWeapon(itemStack) && !isProjectileLauncher(itemStack)) canAdd = false;
                    break;

                case GENERIC_ARMOR:
                case GENERIC_KNOCKBACK_RESISTANCE:
                case GENERIC_ARMOR_TOUGHNESS:
                    if (!isArmor(itemStack)) canAdd = false;
                    break;
            }

            EquipmentSlot slot = attributeModifier.getSlot(itemStack);
            if (slot != Utils.getItemsMainSlot(itemStack)) canAdd = false;
            if (attributeModifier.ignoredSlots != null && attributeModifier.ignoredSlots.contains(slot)) canAdd = false;

            if (canAdd) {
                double initialValue = 0;

                if (defAttributes.containsKey(attributeModifier.getSlot(itemStack))) {
                    var defForSlot = defAttributes.get(attributeModifier.getSlot(itemStack));
                    var curDef = defForSlot.get(attribute).stream().findFirst();
                    if (curDef.isPresent()) {
                        var defaultMod = curDef.get();
                        initialValue = defaultMod.getAmount();
                    }
                }

                var sharpnessLevel = itemStack.getEnchantmentLevel(Enchantment.SHARPNESS);
                var newAmount = initialValue + attributeModifier.getAmount(slot) + ((sharpnessLevel == 1) ? 1 :
                        (sharpnessLevel == 2) ? 1.5d :
                                (sharpnessLevel == 3) ? 2 :
                                        (sharpnessLevel == 4) ? 2.5d :
                                                (sharpnessLevel == 5) ? 3 : 0);
                var newMod = new QualityAttributeModifier(newAmount,
                        slot);
                itemMeta.removeAttributeModifier(attribute);
                if (itemMeta.getAttributeModifiers() == null || !itemMeta.getAttributeModifiers().containsKey(attribute))
                    itemMeta.addAttributeModifier(attribute, newMod.getModifier(itemStack, attribute));
                var attrN = attribute.name().toLowerCase().replace("generic_", "generic.");
                var attrTrans = new TranslatableComponent("attribute.name.%s".formatted(attrN));

                var attrFinal = attrTrans.toPlainText();
                newLore.add(colorize((attributeModifier.getAmount(slot) > 0) ? "&a+" : "&c") + attributeModifier.getAmount(slot) + " " + attrFinal);
            }
        });

        try {
            defAttributes.forEach((equipmentSlot, attributeAttributeModifierMultimap) -> {
                attributeAttributeModifierMultimap.forEach((attribute, attributeModifier) -> {
                    if (itemMeta.getAttributeModifiers() != null &&
                            !itemMeta.getAttributeModifiers().containsKey(attribute) &&
                            !itemMeta.getAttributeModifiers().containsValue(attributeModifier))
                        itemMeta.addAttributeModifier(attribute, attributeModifier);
                });
            });
        } catch (Exception ignored) {
        }
        var maxDur = getItemMaxDurability(itemStack);
        maxDur += itemQuality.itemMaxDurabilityMod;
        itemMeta.getPersistentDataContainer().set(MAX_ITEM_DURABILITY, INTEGER, maxDur);

        if (getConfig().displayDurabilityLineIfMaxDurabilityHasOffset) {
            if (maxDur != itemStack.getType().getMaxDurability()) {
                newLore.add("");
                var itemsCurDam = getItemDamage(itemStack);
                var itemsRemainingDur = maxDur - itemsCurDam;
                newLore.add(colorize("&7&oDurability: %s/%s".formatted(
                        (itemsRemainingDur <= maxDur && itemsRemainingDur > maxDur / 2) ? "&a&o%s".formatted(itemsRemainingDur) :
                                (itemsRemainingDur <= maxDur / 2 && itemsRemainingDur > maxDur / 3) ? "&2&o%s".formatted(itemsRemainingDur) :
                                        (itemsRemainingDur <= maxDur / 3) ? "&c&o%s".formatted(itemsRemainingDur) : "&o%s".formatted(itemsRemainingDur), "&o%s".formatted(maxDur)
                )));
            }
        }

        itemMeta.setLore(newLore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack removeQualityFromItem(ItemStack itemStack) {
        if (!itemCanHaveQuality(itemStack) || !itemHasQuality(itemStack)) return itemStack;

        var itemsQuality = getQuality(itemStack);
        return removeQualityFromItem(itemStack, itemsQuality, false);
    }

    public static ItemStack removeQualityFromItem(ItemStack itemStack, boolean removedByCommand) {
        if (!itemCanHaveQuality(itemStack) || !itemHasQuality(itemStack)) return itemStack;

        var itemsQuality = getQuality(itemStack);
        return removeQualityFromItem(itemStack, itemsQuality, removedByCommand);
    }

    public static ItemStack removeQualityFromItem(ItemStack itemStack, ItemQuality itemQuality, boolean removedByCommand) {
        var itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemHasQuality(itemStack)) return itemStack;

        itemMeta.getPersistentDataContainer().remove(ITEM_QUALITY);
        itemMeta.getPersistentDataContainer().remove(MAX_ITEM_DURABILITY);
        itemMeta.getPersistentDataContainer().remove(ITEM_DURABILITY);

        if (removedByCommand)
            itemMeta.getPersistentDataContainer().set(ITEM_QUALITY_REMOVED, INTEGER, 1);

        var customItemName = itemMeta.getPersistentDataContainer().getOrDefault(ITEM_CUSTOM_NAME, STRING, "");
        String itemName = (!customItemName.isEmpty()) ? customItemName : new TranslatableComponent("item.minecraft.%s".formatted(itemStack.getType().toString().toLowerCase())).toPlainText();
        itemMeta.setDisplayName(colorize("&r" + itemName));

        itemMeta.setLore(new ArrayList<>());

        if (itemMeta.hasAttributeModifiers() && itemQuality != null)
            itemQuality.modifiers.forEach((attribute, qualityAttributeModifier) -> {
                itemMeta.removeAttributeModifier(attribute);
                itemMeta.removeAttributeModifier(attribute, qualityAttributeModifier.getModifier(itemStack, attribute));
            });

        try {
            Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> defAttributes = new HashMap<>();
            defAttributes.put(HAND, itemStack.getType().getDefaultAttributeModifiers(HAND));
            defAttributes.put(OFF_HAND, itemStack.getType().getDefaultAttributeModifiers(OFF_HAND));
            defAttributes.put(HEAD, itemStack.getType().getDefaultAttributeModifiers(HEAD));
            defAttributes.put(CHEST, itemStack.getType().getDefaultAttributeModifiers(CHEST));
            defAttributes.put(LEGS, itemStack.getType().getDefaultAttributeModifiers(LEGS));
            defAttributes.put(FEET, itemStack.getType().getDefaultAttributeModifiers(FEET));
            defAttributes.forEach((equipmentSlot, attributeAttributeModifierMultimap) -> {
                attributeAttributeModifierMultimap.forEach((attribute, attributeModifier) -> {
                    if (itemMeta.getAttributeModifiers() != null &&
                            !itemMeta.getAttributeModifiers().containsKey(attribute) &&
                            !itemMeta.getAttributeModifiers().containsValue(attributeModifier))
                        itemMeta.addAttributeModifier(attribute, attributeModifier);
                });
            });
        } catch (Exception ignored) {
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static boolean itemCanHaveQuality(ItemStack itemStack) {
        return itemStack.getItemMeta() != null &&
                itemStack.getItemMeta() instanceof Damageable &&
                itemStack.getType().getMaxDurability() > 0 &&
                !getConfig().itemBlacklist.contains(itemStack.getType()) &&
                !itemStack.getItemMeta().getPersistentDataContainer().has(ITEM_QUALITY_REMOVED, INTEGER);
    }

    public static ItemQuality getRandomQuality(ItemStack itemStack) {
        return getRandomQuality(itemStack, null);
    }

    public static ItemQuality getRandomQuality(ItemStack itemStack, ItemQuality exclude) {
        List<ItemQuality> itemQualities = new ArrayList<>() {{
            addAll(qualitiesRegistry.getRegistry().values());
        }};
        itemQualities.sort(new ItemQualityComparator());
        Collections.reverse(itemQualities);
        if (exclude != null) itemQualities.remove(exclude);

        // Filter based on whitelist condition
        itemQualities.removeIf(quality -> quality.itemWhitelistMode != null 
            && quality.itemWhitelistMode
            && !quality.itemList.contains(itemStack.getType()));

        for (ItemQuality quality : itemQualities) {
            if (chanceOf(quality.addToItemChance)) return quality;
        }

        return itemQualities.isEmpty() ? null : itemQualities.get(Utils.getRandom().nextInt(itemQualities.size()));
    }

    public static ItemQuality getQualityById(String id) {
        for (NamespacedKey key : qualitiesRegistry.getRegistry().keySet()) {
            if (key.getKey().equals(id)) return qualitiesRegistry.get(key);
        }
        return null;
    }

    public static ItemQuality getQualityById(NamespacedKey id) {
        return qualitiesRegistry.get(id);
    }

    public static ItemQuality getQuality(ItemStack itemStack) {
        if (!itemHasQuality(itemStack)) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return getQualityById(meta.getPersistentDataContainer().get(ITEM_QUALITY, STRING));
    }

    public static boolean itemHasQuality(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(ITEM_QUALITY, STRING);
    }
}
