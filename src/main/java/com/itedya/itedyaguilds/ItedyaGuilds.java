package com.itedya.itedyaguilds;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    @Inject private CommandExecutor cmd;
    @Inject private Database database;

    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        this.logger.info("Enabled plugin");

        var module = new BindingModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        this.getCommand("g").setExecutor(this.cmd);
    }

    @Override
    public void onDisable() {
        try {
            database.getConnectionSource().close();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Can't close connection with database!", e);
        }

        this.logger.info("Disabled plugin");
    }
}
