package com.itedya.itedyaguilds.daos;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.models.Guild;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GuildDaoImplementation implements GuildDao {
    private Database database;

    public GuildDaoImplementation(Database database) {
        this.database = database;
    }

    @Override
    public Guild add(Guild guild) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("INSERT INTO guilds (name, short_name, guild_home_id, created_at) VALUES (?, ?, ?, datetime('now', 'localtime'));", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, guild.getName());
        stmt.setString(2, guild.getShortName());
        stmt.setInt(3, guild.getGuildHomeId());

        stmt.executeUpdate();

        guild.setId(stmt.getGeneratedKeys().getInt("id"));

        stmt.close();

        return guild;
    }

    @Override
    public void delete(int id) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("DELETE FROM guilds WHERE id = ?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void update(Guild guild) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("UPDATE guilds SET name = ?, short_name = ?, guild_home_id = ? WHERE id = ?");
        stmt.setString(1, guild.getName());
        stmt.setString(2, guild.getShortName());
        stmt.setInt(3, guild.getGuildHomeId());
        stmt.setInt(4, guild.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public Guild getGuildById(int id) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("SELECT * FROM guilds WHERE id = ?;");
        stmt.setInt(1, id);

        var rs = stmt.executeQuery();

        if (rs.next()) {
            var guild = new Guild();
            guild.setId(rs.getInt("id"));
            guild.setName(rs.getString("name"));
            guild.setShortName(rs.getString("short_name"));
            guild.setGuildHomeId(rs.getInt("guild_home_id"));
            guild.setCreatedAt(rs.getDate("created_at"));

            stmt.close();

            return guild;
        }

        stmt.close();

        return null;
    }

    @Override
    public List<Guild> getGuilds() throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("SELECT * FROM guilds");

        var rs = stmt.executeQuery();

        List<Guild> guilds = new ArrayList<>();

        while (rs.next()) {
            var guild = new Guild();

            guild.setId(rs.getInt("id"));
            guild.setName(rs.getString("name"));
            guild.setShortName(rs.getString("short_name"));
            guild.setGuildHomeId(rs.getInt("guild_home_id"));
            guild.setCreatedAt(rs.getDate("created_at"));

            guilds.add(guild);
        }

        stmt.close();

        return guilds;
    }
}
