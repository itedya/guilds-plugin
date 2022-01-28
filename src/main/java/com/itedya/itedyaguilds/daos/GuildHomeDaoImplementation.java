package com.itedya.itedyaguilds.daos;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.models.GuildHome;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class GuildHomeDaoImplementation implements GuildHomeDao {
    private final Database database;

    public GuildHomeDaoImplementation(Database database) {
        this.database = database;
    }

    @Override
    public GuildHome add(GuildHome gh) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("INSERT INTO guild_homes (x, y, z, created_at) VALUES (?, ?, ?, datetime('now', 'localtime'));", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, gh.getX());
        stmt.setInt(2, gh.getY());
        stmt.setInt(3, gh.getZ());

        stmt.executeUpdate();

        gh.setId(stmt.getGeneratedKeys().getInt("id"));

        stmt.close();

        return gh;
    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public GuildHome getGuildHomeById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(GuildHome guild) throws SQLException {

    }
}
