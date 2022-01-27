package com.itedya.itedyaguilds;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        this.logger.info("Enabled plugin");
    }

    @Override
    public void onDisable() {
        this.logger.info("Disabled plugin");
    }
}
