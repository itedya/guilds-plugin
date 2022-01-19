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

    public static void migrateDatabase(JavaPlugin plugin) throws SQLException, IOException {
        InputStream queryFile = plugin.getResource("internal/tables.sql");
        assert queryFile != null;

        Statement statement = Database.connection.createStatement();
        statement.executeUpdate(
                "PRAGMA foreign_keys = ON;\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS guilds\n" +
                        "(\n" +
                        "    uuid       VARCHAR PRIMARY KEY,\n" +
                        "    name       VARCHAR NOT NULL UNIQUE,\n" +
                        "    short_name VARCHAR NOT NULL UNIQUE,\n" +
                        "    guild_home VARCHAR NOT NULL UNIQUE,\n" +
                        "    created_at DATETIME default CURRENT_TIMESTAMP,\n" +
                        "    FOREIGN KEY (guild_home) REFERENCES guild_homes (uuid) ON DELETE CASCADE\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS guild_homes (\n" +
                        "    uuid        VARCHAR PRIMARY KEY,\n" +
                        "    x           INTEGER NOT NULL,\n" +
                        "    y           INTEGER NOT NULL,\n" +
                        "    z           INTEGER NOT NULL,\n" +
                        "    created_at  DATE NOT NULL\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE IF NOT EXISTS guild_members\n" +
                        "(\n" +
                        "    player_uuid VARCHAR NOT NULL UNIQUE,\n" +
                        "    guild_uuid  VARCHAR NOT NULL,\n" +
                        "    role        VARCHAR NOT NULL,\n" +
                        "    created_at  DATETIME default CURRENT_TIMESTAMP,\n" +
                        "    FOREIGN KEY (guild_uuid) REFERENCES guilds (uuid) ON DELETE CASCADE\n" +
                        ");"
        );

        Database.connection.commit();
    }
}
