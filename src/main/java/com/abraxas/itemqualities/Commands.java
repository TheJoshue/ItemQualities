package com.abraxas.itemqualities;

//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.ArgumentSuggestions;
//import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;

import com.abraxas.itemqualities.api.DurabilityManager;
import com.abraxas.itemqualities.inventories.Inventories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.abraxas.itemqualities.QualitiesManager.*;
import static com.abraxas.itemqualities.api.DurabilityManager.damageItem;
import static com.abraxas.itemqualities.api.DurabilityManager.repairItem;
import static com.abraxas.itemqualities.api.Keys.ITEM_QUALITY_REMOVED;
import static com.abraxas.itemqualities.api.Registries.qualitiesRegistry;
import static com.abraxas.itemqualities.inventories.Inventories.QUALITY_MANAGER_INVENTORY;
import static com.abraxas.itemqualities.utils.Permissions.*;
import static com.abraxas.itemqualities.utils.Utils.*;
import static org.bukkit.Material.AIR;
import static org.bukkit.persistence.PersistentDataType.INTEGER;

public class Commands implements CommandExecutor/*, TabCompleter*/ {

    private ItemQualities instance;

    public Commands(ItemQualities plugin) {
        this.instance = plugin;
        //plugin.getCommand("qualities").setExecutor(this);
        //plugin.getCommand("qualities").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Display help or top-level command response
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "resetconfig" -> handleResetConfig(sender);
            case "repairitem" -> {
                if (sender instanceof Player player) handleRepairItem(player);
                else sendMessageWithPrefix(sender, main.getTranslation("message.commands.only_run_ingame"));
            }
            case "setitemquality" -> {
                if (sender instanceof Player player && args.length > 1) handleSetItemQuality(player, args[1]);
                else sendMessageWithPrefix(sender, main.getTranslation("message.commands.enter_quality"));
            }
            case "removeitemquality" -> {
                if (sender instanceof Player player) handleRemoveItemQuality(player);
                else sendMessageWithPrefix(sender, main.getTranslation("message.commands.only_run_ingame"));
            }
            case "managequalities" -> {
                if (sender instanceof Player player) handleManageQualities(player);
                else sendMessageWithPrefix(sender, main.getTranslation("message.commands.only_run_ingame"));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        ItemQualities.getInstance().loadConfig();
        loadAndRegister();
        sendMessageWithPrefix(sender, ItemQualities.getInstance().getTranslation("message.commands.reloaded"));
    }

    private void handleResetConfig(CommandSender sender) {
        ItemQualities.getInstance().resetConfig();
        loadAndRegister();
        sendMessageWithPrefix(sender, ItemQualities.getInstance().getTranslation("message.commands.reloaded_default_config"));
    }

    private void handleRepairItem(Player player) {
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.must_hold_item"));
            return;
        }
        if (!(item.getItemMeta() instanceof Damageable)) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.item_cant_be_repaired"));
            return;
        }
        var itemName = new TranslatableComponent("item.minecraft.%s".formatted(item.getType().toString().toLowerCase())).toPlainText();
        repairItem(item);
        sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.item_repaired").formatted(itemName));
    }

    private void handleSetItemQuality(Player player, String qualityArg) {
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.must_hold_item"));
            return;
        }
        var quality = qualityArg.equalsIgnoreCase("random")
                ? getRandomQuality(item, getQuality(item))
                : getQualityById(qualityArg);

        if (!itemCanHaveQuality(item)) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.item_cant_have_quality"));
            return;
        }
        refreshItem(item, quality);
        damageItem(player, item, 0);
        sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.items_quality_set").formatted(quality.display));
    }

    private void handleRemoveItemQuality(Player player) {
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.must_hold_item"));
            return;
        }
        if (!itemHasQuality(item)) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.item_has_no_quality"));
            return;
        }

        var itemsQuality = getQuality(item);
        removeQualityFromItem(item, true);
        damageItem(player, item, 0);

        sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.commands.quality_removed").formatted(itemsQuality.display));
    }

    private void handleManageQualities(Player player) {
        if (qualitiesRegistry.getRegistry().size() < 1) {
            sendMessageWithPrefix(player, ItemQualities.getInstance().getTranslation("message.plugin.no_qualities_registered"));
            return;
        }
        QUALITY_MANAGER_INVENTORY.open(player, 0);
    }

    private void sendHelp(CommandSender sender) {
        sendMessageWithoutPrefix(sender, ItemQualities.getInstance().getTranslation("message.plugin.help.info"));
    }

    /*@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "resetconfig", "repairitem", "setitemquality", "removeitemquality", "managequalities");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setitemquality")) {
            List<String> qualities = new ArrayList<>(QualitiesManager.qualitiesRegistry.getRegistry().keySet());
            qualities.add("random");
            return qualities;
        }
        return Collections.emptyList();
    }*/

    /*static ItemQualities main = ItemQualities.getInstance();

    public static void register() {
        var subCommands = new ArrayList<CommandAPICommand>() {{
            add(new CommandAPICommand("reload")
                    .withPermission(RELOAD_PERMISSION)
                    .executes((sender, args) -> {
                        main.loadConfig();
                        loadAndRegister();
                        sendMessageWithPrefix(sender, main.getTranslation("message.commands.reloaded"));

                        if (getConfig().newUpdateMessageOnReload) sendNewVersionNotif(sender);
                    }));
            add(new CommandAPICommand("resetconfig")
                    .withPermission(RESET_CONFIG_PERMISSION)
                    .executes((sender, args) -> {
                        main.resetConfig();
                        loadAndRegister();
                        sendMessageWithPrefix(sender, main.getTranslation("message.commands.reloaded_default_config"));
                    }));
            add(new CommandAPICommand("repairitem")
                    .withPermission(REPAIR_ITEM_PERMISSION)
                    .executesPlayer((player, args) -> {
                        var item = player.getInventory().getItemInMainHand();
                        if (item.getType().equals(AIR)) {
                            sendMessageWithPrefix(player, main.getTranslation("message.commands.must_hold_item"));
                            return;
                        }
                        var itemMeta = item.getItemMeta();
                        if (!(itemMeta instanceof Damageable)) {
                            sendMessageWithPrefix(player, main.getTranslation("message.commands.item_cant_be_repaired"));
                            return;
                        }
                        var itemName = new TranslatableComponent("item.minecraft.%s".formatted(item.getType().toString().toLowerCase())).toPlainText();
                        repairItem(item);
                        sendMessageWithPrefix(player, main.getTranslation("message.commands.item_repaired").formatted(itemName));
                    }));
            add(new CommandAPICommand("setitemquality")
                    .withPermission(SET_ITEMS_QUALITY_PERMISSION)
                    .withArguments(new GreedyStringArgument("quality")
                            .replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> {
                                var qualityNamespaces = new String[qualitiesRegistry.getRegistry().size() + 1];
                                var integer = new AtomicInteger();
                                qualitiesRegistry.getRegistry().keySet().forEach(k -> {
                                    qualityNamespaces[integer.getAndIncrement()] = "%s:%s".formatted(k.getNamespace(), k.getKey());
                                });
                                qualityNamespaces[qualitiesRegistry.getRegistry().size()] = "random";
                                return qualityNamespaces;
                            })))
                    .executesPlayer((player, args) -> {
                        var item = player.getInventory().getItemInMainHand();
                        if (item.getType().equals(AIR)) {
                            sendMessageWithPrefix(player, main.getTranslation("message.commands.must_hold_item"));
                            return;
                        }
                        var qualArgString = (String) args[0];
                        var quality = getRandomQuality(getQuality(item));
                        var qualArg = qualArgString.split(":");
                        if (qualArgString != "random" && qualArg.length > 1)
                            quality = getQualityById(qualArg[1]);

                        var meta = item.getItemMeta();

                        if (!itemCanHaveQuality(item) && !meta.getPersistentDataContainer().has(ITEM_QUALITY_REMOVED, INTEGER)) {
                            sendMessageWithPrefix(player, main.getTranslation("message.commands.item_cant_have_quality"));
                            return;
                        }
                        if (meta.getPersistentDataContainer().has(ITEM_QUALITY_REMOVED, INTEGER)) {
                            meta.getPersistentDataContainer().remove(ITEM_QUALITY_REMOVED);
                            item.setItemMeta(meta);
                        }
                        refreshItem(item, quality);
                        damageItem(player, item, 0);
                        sendMessageWithPrefix(player, main.getTranslation("message.commands.items_quality_set").formatted(quality.display));
                    }));
            add(new CommandAPICommand("removeitemquality")
                    .withPermission(REMOVE_ITEMS_QUALITY_PERMISSION)
                    .executesPlayer((player, args) -> {
                        var item = player.getInventory().getItemInMainHand();
                        if (item.getType().equals(AIR)) {
                            sendMessageWithPrefix(player, main.getTranslation("message.commands.must_hold_item"));
                            return;
                        }
                        if (!itemHasQuality(item)) {
                            sendMessageWithPrefix(player, main.getTranslation("message.commands.item_has_no_quality"));
                            return;
                        }

                        var itemsQuality = getQuality(item);
                        removeQualityFromItem(item, true);
                        damageItem(player, item, 0);

                        sendMessageWithPrefix(player, main.getTranslation("message.commands.quality_removed").formatted(itemsQuality.display));
                    }));
            add(new CommandAPICommand("managequalities")
                    .withPermission(MANAGE_QUALITIES_PERMISSION)
                    .executesPlayer((player, args) -> {
                        if (qualitiesRegistry.getRegistry().size() < 1) {
                            sendMessageWithPrefix(player, main.getTranslation("message.plugin.no_qualities_registered"));
                            return;
                        }
                        QUALITY_MANAGER_INVENTORY.open(player, 0);
                    }));
        }};

        var mainCommand = new CommandAPICommand("qualities")
                .withPermission(ADMIN_PERMISSION)
                .executes((sender, args) -> {
                    var usableCommands = subCommands.stream().filter(sc -> sender.hasPermission(sc.getPermission().toString())).toList();
                    if (usableCommands.size() > 0) {
                        sendMessageWithoutPrefix(sender, main.getTranslation("message.plugin.help.info"));
                        usableCommands.forEach(sc -> {
                            sendMessageWithoutPrefix(sender, main.getTranslation("message.plugin.help.command.%s".formatted(sc.getName())));
                        });
                    } else sendMessageWithPrefix(sender, main.getTranslation("message.plugin.help.no_usable_commands"));
                });

        subCommands.forEach(mainCommand::withSubcommand);

        mainCommand.register();
    }*/
}
