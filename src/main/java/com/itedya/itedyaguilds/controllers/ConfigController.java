package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.models.NeededItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigController {
    private static FileConfiguration config;
    private static JavaPlugin plugin;
    private static Logger logger;
    public static List<String> help;

    public static void initialize(JavaPlugin plugin) throws IOException {
        ConfigController.logger = plugin.getLogger();
        ConfigController.config = plugin.getConfig();
        ConfigController.plugin = plugin;

        plugin.saveDefaultConfig();

        File helpFile = new File(plugin.getDataFolder(), "help.txt");
        if (!helpFile.exists()) {
            helpFile.getParentFile().mkdirs();
            plugin.saveResource("help.txt", false);
        }

        ConfigController.help = Files.readAllLines(Paths.get(helpFile.getPath()), StandardCharsets.UTF_8)
                .stream()
                .map(item -> ChatColor.translateAlternateColorCodes('&', item))
                .toList();
    }

    public static void checkForErrors() {
        List<String> neededItems = ConfigController.config.getStringList("needed_items");

        if (neededItems.size() == 0) {
            ConfigController.logger.warning("Needed items is empty!");
        }

        for (String neededItem : neededItems) {
            String itemId = neededItem.split(" ")[0];
            int quantity = Integer.parseInt(neededItem.split(" ")[1]);

            if (Material.matchMaterial(itemId) == null) {
                Bukkit.getPluginManager().disablePlugin(plugin);
                throw new RuntimeException("Item " + itemId + " doesn't exist");
            }

            if (quantity <= 0) {
                throw new RuntimeException("Invalid quantity for item " + itemId);
            }
        }
    }

    public static List<NeededItem> getNeededItems() {
        return ConfigController.config.getStringList("needed_items").stream().map(item -> {
            String[] splitted = item.split(" ");

            NeededItem mappedItem = new NeededItem();
            mappedItem.material = Material.matchMaterial(splitted[0]);
            mappedItem.quantity = Integer.valueOf(splitted[1]);

            return mappedItem;
        }).collect(Collectors.toList());
    }

    public static String getNeededItemError(String name) {
        name = name.toLowerCase();

        return ConfigController.config.getString("messages.errors." + name);
    }

    public static Integer getSecondsToTeleportation() {
        return config.getInt("seconds");
    }
}