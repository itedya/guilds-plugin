package com.itedya.itedyaguilds;

import com.google.inject.Inject;
import com.itedya.itedyaguilds.models.Guild;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.nio.file.Paths;
import java.util.logging.Level;

public class Database {
    private ItedyaGuilds plugin;
    private final String DATABASE_URI = "jdbc:sqlite:" + Paths.get(this.plugin.getDataFolder().toString(), "database.db");
    private JdbcConnectionSource cs;

    @Inject
    public Database(ItedyaGuilds plugin) {
        this.plugin = plugin;

        try {
            this.cs = new JdbcConnectionSource(DATABASE_URI);

            TableUtils.createTableIfNotExists(cs, Guild.class);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Can't initialize connection with database!", e);
        }
    }

    public JdbcConnectionSource getConnectionSource() {
        return cs;
    }
}
