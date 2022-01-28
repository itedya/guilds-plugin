package com.itedya.itedyaguilds.daos;

import com.itedya.itedyaguilds.models.Guild;

import java.sql.SQLException;
import java.util.List;

public interface GuildDao {
    public int add(Guild guild) throws SQLException;

    public void delete(int id) throws SQLException;

    public Guild getGuildById(int id) throws SQLException;

    public List<Guild> getGuilds() throws SQLException;

    public void update(Guild guild) throws SQLException;
}
