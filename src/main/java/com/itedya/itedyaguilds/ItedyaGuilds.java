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
        this.getDataFolder().mkdir();

        // initializing database
        try {
            Database.connectToDatabase(this);
            Database.migrateDatabase(this);
        } catch (SQLException e) {
            this.logger.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        InvitesController.initialize(this);
        ConfigController.initialize(this);

        // Initialize commands
        AcceptInviteToGuild.initialize(this);
        CreateGuild.initialize(this);
        DeleteGuild.initialize(this);
        ExitFromGuild.initialize(this);
        InvitePlayerToGuild.initialize(this);
        KickOutOfGuild.initialize(this);
        SetGuildHome.initialize(this);
        TeleportToGuildHome.initialize(this);


        // Initializing cuboid boss bar
        Bukkit.getServer().getPluginManager().registerEvents(new DisplayCuboidInfoListener(), this);

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
