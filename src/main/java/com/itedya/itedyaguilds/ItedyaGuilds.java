package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.commands.*;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.listeners.DisplayCuboidInfoListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    private final Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        this.logger.info("[ItedyaGuilds] Creating data folder");
        this.getDataFolder().mkdir();

        // initializing database
        try {
            this.logger.info("[ItedyaGuilds] Connecting to database");
            Database.connectToDatabase(this);
            this.logger.info("[ItedyaGuilds] Migrating database");
            Database.migrateDatabase(this);
        } catch (SQLException e) {
            this.logger.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.logger.info("[ItedyaGuilds] Initializing invites controller");
        InvitesController.initialize(this);
        this.logger.info("[ItedyaGuilds] Initializing config controller");
        ConfigController.initialize(this);

        // Initialize commands
        this.logger.info("[ItedyaGuilds] Initializing /akceptujzaproszenie command");
        AcceptInviteToGuild.initialize(this);
        this.logger.info("[ItedyaGuilds] Initializing /stworzgildie command");
        CreateGuild.initialize(this);
        this.logger.info("[ItedyaGuilds] Initializing /usungildie command");
        DeleteGuild.initialize(this);
        this.logger.info("[ItedyaGuilds] Initializing /wyjdzzgildii command");
        ExitFromGuild.initialize(this);
        this.logger.info("[ItedyaGuilds] Initializing /zaproszgildii command");
        InvitePlayerToGuild.initialize(this);
        this.logger.info("[ItedyaGuilds] Initializing /wyrzuczgildii command");
        KickOutOfGuild.initialize(this);

        // Initializing cuboid boss bar
        this.logger.info("[ItedyaGuilds] Initializing cuboid boss bar");
        Bukkit.getServer().getPluginManager().registerEvents(new DisplayCuboidInfoListener(), this);

        this.logger.info("[ItedyaGuilds] Initializing papi expander");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExpansion().register();
        }

        this.logger.info("Enabled plugin");
    }

    @Override
    public void onDisable() {
        this.logger.info("Disabled plugin");
    }
}
