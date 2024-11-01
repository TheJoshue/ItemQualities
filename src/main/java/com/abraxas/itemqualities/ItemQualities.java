package com.abraxas.itemqualities;

import com.abraxas.itemqualities.listeners.BlockListeners;
import com.abraxas.itemqualities.listeners.ItemListeners;
import com.abraxas.itemqualities.listeners.ServerListeners;
//import com.abraxas.itemqualities.utils.UpdateChecker;
import com.abraxas.itemqualities.utils.Utils;
import com.google.gson.JsonParser;
//import dev.jorel.commandapi.CommandAPI;
//import dev.jorel.commandapi.CommandAPIConfig;
import fr.minuskube.inv.InventoryManager;
//import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import static com.abraxas.itemqualities.utils.Utils.log;
import static com.abraxas.itemqualities.utils.Utils.registerEvents;

public final class ItemQualities extends JavaPlugin {
    private static ItemQualities instance;

    ResourceBundle langBundle;
    InventoryManager inventoryManager;

    Config config;
    String configPath = "%s/config.json".formatted(getDataFolder());

    public static ItemQualities getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        //CommandAPI.onLoad(new CommandAPIConfig().silentLogs(true));
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        getCommand("itemqualities").setExecutor(new Commands(this));
        //CommandAPI.onEnable(instance);

        loadConfig();
        //startMetrics();
        QualitiesManager.loadAndRegister();

        //Commands.register();

        registerEvents(new ItemListeners());
        registerEvents(new BlockListeners());
        registerEvents(new ServerListeners());

        //UpdateChecker.checkForNewVersion();

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        log(getLangBundle().getString("message.plugin.enabled").formatted((float) (System.currentTimeMillis() - start) / 1000));
    }

    @Override
    public void onDisable() {
        log(getLangBundle().getString("message.plugin.disabled"));
    }

    /*void startMetrics() {
        Metrics metrics = new Metrics(this, 15451);
    }*/

    void loadConfig() {
        try {
            if (!Files.exists(Path.of(configPath))) {
                saveResource("config.json", true);
                resetConfig();
                return;
            }
            var file = new File(configPath);
            var contents = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            var json = JsonParser.parseString(contents);

            config = Config.deserialize(String.valueOf(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
        langBundle = ResourceBundle.getBundle("language", config.locale);
    }

    void saveDefConfig() throws IOException {
        var newConfig = new Config();
        newConfig.prefix = Utils.colorize(newConfig.prefix);
        newConfig.itemQualityDisplayFormat = Utils.colorize(newConfig.itemQualityDisplayFormat);
        var defaultConfig = Config.serialize(newConfig);
        var file = new File(configPath);
        Files.writeString(file.toPath(), defaultConfig);
    }

    public void resetConfig() {
        try {
            saveDefConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadConfig();
    }

    public ResourceBundle getLangBundle() {
        return langBundle;
    }

    public String getTranslation(String key) {
        return new String(getLangBundle().getString(key).getBytes(StandardCharsets.UTF_8));
    }

    public Config getConfiguration() {
        return config;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
