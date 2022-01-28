package com.itedya.itedyaguilds;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;

public class Database {
    private final ItedyaGuilds plugin;
    private final String DATABASE_URI;
    private Connection connection;

    public Database(ItedyaGuilds _plugin) {
        plugin = _plugin;
        DATABASE_URI = "jdbc:sqlite:" + Paths.get(this.plugin.getDataFolder().toString(), "database.db");

        try {
            this.connection = DriverManager.getConnection(DATABASE_URI);
            this.connection.setAutoCommit(false);
            this.migrate();

            plugin.getLogger().log(Level.INFO, "Initialized connection with database");

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Can't initialize connection with database!", e);
        }
    }

    public void migrate() {
        try {
            var is = plugin.getResource("migrations.sql");
            assert is != null : "migrations.sql input stream is null";

            var migrations = new String(is.readAllBytes()).replace("\r\n", "");
            var stmt = this.connection.createStatement();

            stmt.executeUpdate(migrations);

            this.connection.commit();
            stmt.close();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Can't migrate database!", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
