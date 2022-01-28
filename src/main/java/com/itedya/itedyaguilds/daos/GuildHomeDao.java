package com.itedya.itedyaguilds.daos;

import com.itedya.itedyaguilds.models.GuildHome;

import java.sql.SQLException;

public interface GuildHomeDao {
    public GuildHome add(GuildHome guild) throws SQLException;

    public void delete(int id) throws SQLException;

    public GuildHome getGuildHomeById(int id) throws SQLException;

    public void update(GuildHome guild) throws SQLException;
}
