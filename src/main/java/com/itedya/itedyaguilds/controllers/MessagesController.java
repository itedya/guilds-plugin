package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.ItedyaGuilds;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Logger;

public class MessagesController {
    private static Logger logger;
    private static ClassLoader classLoader;
    private static YamlConfiguration config = null;
    private static String dataFolder;

    /**
     * Initialize messages controller,
     * gets plugin logger and class loader
     *
     * @param plugin ItedyaGuilds plugin instance
     */
    public static void initialize(ItedyaGuilds plugin) {
        logger = plugin.getLogger();
        classLoader = plugin.getClass().getClassLoader();
        dataFolder = plugin.getDataFolder().getAbsolutePath();

        loadConfiguration(plugin.getConfig().getString("locale"));
    }

    /**
     * Load locale configuration to memory
     *
     * @param localeId Locale file name without extension
     */
    static void loadConfiguration(String localeId) {
        logger.info("Loading messages configuration...");

        try {
            var configFile = new File(dataFolder + File.pathSeparator + "locale" + File.pathSeparator + localeId + ".yml");
            if (!configFile.exists()) {
                logger.warning("Locale file doesn't exist! Using default locale configuration.");
            } else {
                config = YamlConfiguration.loadConfiguration(configFile);
            }
        } catch (Exception e) {
            logger.warning("Can't load locale file, using default locale configuration.");
            logger.warning(e.getMessage());
        }

        if (config == null) {
            try {
                var inputReader = classLoader.getResourceAsStream("locales/?.yml".replace("?", localeId));
                assert inputReader != null : "Default configuration for this locale doesn't exist!";

                // Load default locale configuration
                config = new YamlConfiguration();
                config.loadFromString(new String(inputReader.readAllBytes()));
            } catch (Exception e) {
                logger.severe("Failed to load default locale configuration for locale '" + localeId + "'!");
                logger.severe(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get message
     *
     * @param identifier Message index in locale config file
     * @return string
     */
    public static String getMessage(String identifier) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(identifier, "&cMessage not defined, contact administrator for help."));
    }
}
