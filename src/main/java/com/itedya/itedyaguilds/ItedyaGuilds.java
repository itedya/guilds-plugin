package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.commands.CommandsHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    private Database database;

    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();

        this.database = new Database(this);

        this.getCommand("g").setExecutor(new CommandsHandler(this, database));

        this.logger.info("Enabled plugin");
    }

    @Override
    public void onDisable() {
        if (database != null && database.getConnection() != null) {
            try {
                database.getConnection().close();
            } catch (Exception e) {
                this.getLogger().log(Level.SEVERE, "Can't close connection with database!", e);
            }
        }

        this.logger.info("Disabled plugin");
    }
}
