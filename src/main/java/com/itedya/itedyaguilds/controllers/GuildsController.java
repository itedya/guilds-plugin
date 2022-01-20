package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.builders.GuildBuilder;
import com.itedya.itedyaguilds.builders.GuildHomeBuilder;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

public class GuildsController {
    public static Guild getGuildByUUID(String uuid) throws SQLException, ParseException {
        PreparedStatement stmt = Database.connection.prepareStatement("SELECT * FROM guilds WHERE uuid = ?;");
        stmt.setString(1, uuid);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new GuildBuilder()
                    .setUUID(uuid)
                    .setName(rs.getString("name"))
                    .setShortName(rs.getString("short_name"))
                    .setCreatedAt(rs.getString("created_at"))
                    .build();
        }

        return null;
    }

    public static boolean isPlayerInGuild(Player player) throws SQLException {
        String uuid = player.getUniqueId().toString();

        Statement stmt = Database.connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM guild_members WHERE player_uuid = '" + uuid + "'");

        int count = 0;
        while (rs.next()) {
            count = rs.getInt("COUNT(*)");
        }

        return count > 0;
    }

    public static Guild getPlayerGuild(Player player) throws SQLException, ParseException {
        PreparedStatement stmt = Database.connection.prepareStatement(
                "SELECT guilds.* FROM guilds " +
                        "INNER JOIN guild_members pig on guilds.uuid = pig.guild_uuid " +
                        "WHERE pig.player_uuid = ?;");

        stmt.setString(1, player.getUniqueId().toString());

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new GuildBuilder()
                    .setUUID(rs.getString("uuid"))
                    .setName(rs.getString("name"))
                    .setShortName(rs.getString("short_name"))
                    .setCreatedAt(rs.getString("created_at"))
                    .build();
        }

        return null;
    }

    public static Guild createGuild(String name, String shortName, GuildHome guildHome) throws SQLException {
        UUID uuid = UUID.randomUUID();
        Statement stmt = Database.connection.createStatement();

        PreparedStatement prepStmt = Database.connection.prepareStatement("INSERT INTO guilds (uuid, name, short_name, guild_home) VALUES (?, ?, ?, ?);");
        prepStmt.setString(1, uuid.toString());
        prepStmt.setString(2, name);
        prepStmt.setString(3, shortName);
        prepStmt.setString(4, guildHome.uuid.toString());

        prepStmt.executeUpdate();

        Guild guild = new GuildBuilder()
                .setUUID(uuid.toString())
                .setName(name)
                .setShortName(shortName)
                .build();

        stmt.close();
        prepStmt.close();

        return guild;
    }

    public static void addPlayerToGuild(Player player, Guild guild, String role) throws SQLException {
        PreparedStatement stmt = Database.connection.prepareStatement("INSERT INTO guild_members (player_uuid, guild_uuid, role) VALUES (?, ?, ?);");
        stmt.setString(1, player.getUniqueId().toString());
        stmt.setString(2, guild.uuid.toString());
        stmt.setString(3, role);
        stmt.executeUpdate();
        stmt.close();
    }

    public static void removeMember(Player player) throws SQLException {
        PreparedStatement stmt = Database.connection.prepareStatement("DELETE FROM guild_members WHERE player_uuid = ?;");
        stmt.setString(1, player.getUniqueId().toString());
        stmt.executeUpdate();
        stmt.close();
    }

    public static void delete(Guild guild) throws SQLException {
        PreparedStatement stmt = Database.connection.prepareStatement("DELETE FROM guilds WHERE uuid = ?;");
        stmt.setString(1, guild.uuid.toString());
        stmt.executeUpdate();
        stmt.close();

        stmt = Database.connection.prepareStatement("DELETE FROM guild_members WHERE guild_uuid = ?;");
        stmt.setString(1, guild.uuid.toString());
        stmt.executeUpdate();
        stmt.close();
    }

    public static boolean isPlayerOwnerOfGuild(Player player, Guild guild) throws SQLException, ParseException {
        if (guild == null) {
            guild = GuildsController.getPlayerGuild(player);
        }

        assert guild != null : "Guild is null";

        guild.getMembers();

        List<GuildMember> members = guild.getMembers();

        GuildMember member = members.stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
        assert member != null : "Member cant be null";

        return member.role.equals("OWNER");
    }

    public static GuildHome createGuildHome(Location location) throws SQLException {
        PreparedStatement stmt = Database.connection.prepareStatement("INSERT INTO guild_homes (uuid, x, y, z, created_at) VALUES (?, ?, ?, ?, datetime('now', 'localtime'))");
        UUID uuid = UUID.randomUUID();

        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();

        stmt.setString(1, uuid.toString());
        stmt.setInt(2, x);
        stmt.setInt(3, y);
        stmt.setInt(4, z);

        stmt.executeUpdate();

        stmt.close();

        return new GuildHomeBuilder()
                .setUUID(uuid)
                .setX(x)
                .setY(y)
                .setX(x)
                .build();
    }

    public static GuildHome updateGuildHome(GuildHome guildHome, Location location) throws SQLException {
        PreparedStatement stmt = Database.connection.prepareStatement("UPDATE guild_homes SET x = ?, y = ?, z = ? WHERE uuid = ?;");

        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();

        stmt.setInt(1, x);
        stmt.setInt(2, y);
        stmt.setInt(3, z);
        stmt.setString(4, guildHome.uuid.toString());

        stmt.executeUpdate();

        stmt.close();

        return new GuildHomeBuilder()
                .setUUID(guildHome.uuid)
                .setX(x)
                .setY(y)
                .setZ(z)
                .build();
    }
}
