package com.itedya.itedyaguilds.models;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.builders.GuildMemberBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Guild {
    public UUID uuid;
    public String name;
    public String short_name;
    public Date created_at;

    public Guild(UUID uuid, String name, String short_name, Date created_at) {
        this.uuid = uuid;
        this.name = name;
        this.short_name = short_name;
        this.created_at = created_at;
    }

    public List<GuildMember> getMembers() throws SQLException {
        PreparedStatement stmt = Database.connection.prepareStatement("SELECT * FROM guild_members WHERE guild_uuid = ?;");
        stmt.setString(1, this.uuid.toString());

        ResultSet rs = stmt.executeQuery();

        List<GuildMember> members = new ArrayList<>();

        while (rs.next()) {
            Player player = Bukkit.getPlayer(UUID.fromString(rs.getString("player_uuid")));

            GuildMember gm = new GuildMemberBuilder()
                    .setGuild(this)
                    .setPlayer(player)
                    .setRole(rs.getString("role"))
                    .build();

            members.add(gm);
        }

        return members;
    }
}
