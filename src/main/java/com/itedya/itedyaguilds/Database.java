package com.itedya.itedyaguilds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Database {
    private static final Logger logger = Bukkit.getLogger();
    public static Connection connection;

    public static void connectToDatabase(JavaPlugin plugin) throws SQLException {
        try {
            String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "database.db";
            Database.connection = DriverManager.getConnection(url);
            Database.connection.setAutoCommit(false);

            Database.logger.info("[ItedyaGuilds] Connection to SQLite has been established.");
        } catch (SQLException e) {
            Database.logger.severe(ChatColor.RED + "Exception occured while connecting to sqlite.");
            Database.logger.severe(ChatColor.RED + e.getMessage());
            throw e;
        }
    }

    public static void migrateDatabase(JavaPlugin plugin) throws SQLException {
        InputStream queryFile = plugin.getResource("migrations.sql");
        assert queryFile != null;

        Statement statement = Database.connection.createStatement();
        statement.executeUpdate(
                "PRAGMA foreign_keys = ON;" +
                        "CREATE TABLE IF NOT EXISTS guilds" +
                        "(" +
                        "    uuid       VARCHAR PRIMARY KEY," +
                        "    name       VARCHAR NOT NULL UNIQUE," +
                        "    short_name VARCHAR NOT NULL UNIQUE," +
                        "    guild_home VARCHAR NOT NULL UNIQUE," +
                        "    created_at DATETIME default CURRENT_TIMESTAMP," +
                        "    FOREIGN KEY (guild_home) REFERENCES guild_homes (uuid) ON DELETE CASCADE" +
                        ");" +
                        "" +
                        "CREATE TABLE IF NOT EXISTS guild_homes (" +
                        "    uuid        VARCHAR PRIMARY KEY," +
                        "    x           INTEGER NOT NULL," +
                        "    y           INTEGER NOT NULL," +
                        "    z           INTEGER NOT NULL," +
                        "    created_at  DATE NOT NULL" +
                        ");" +
                        "" +
                        "CREATE TABLE IF NOT EXISTS guild_members" +
                        "(" +
                        "    player_uuid VARCHAR NOT NULL UNIQUE," +
                        "    guild_uuid  VARCHAR NOT NULL," +
                        "    role        VARCHAR NOT NULL," +
                        "    created_at  DATETIME default CURRENT_TIMESTAMP," +
                        "    FOREIGN KEY (guild_uuid) REFERENCES guilds (uuid) ON DELETE CASCADE" +
                        ");"
        );

        Database.connection.commit();
    }
}
