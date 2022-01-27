package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.commands.CommandHandler;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.controllers.MessagesController;
import com.itedya.itedyaguilds.listeners.DisplayCuboidInfoListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // initializing database
        try {
            this.getDataFolder().mkdir();

            this.saveDefaultConfig();

            Database.connectToDatabase(this);
            Database.migrateDatabase(this);

            InvitesController.initialize(this);
            ConfigController.initialize(this);
            MessagesController.initialize(this);

            // Initializing cuboid boss bar
            Bukkit.getServer().getPluginManager().registerEvents(new DisplayCuboidInfoListener(), this);

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PlaceholderAPIExpansion().register();
            }

            Objects.requireNonNull(Bukkit.getPluginCommand("g")).setExecutor(new CommandHandler(this));

            this.logger.info("Enabled plugin");
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        this.logger.info("Disabled plugin");
    }
}
