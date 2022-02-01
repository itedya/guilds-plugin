package com.itedya.itedyaguilds.daos;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.enums.MemberRole;
import com.itedya.itedyaguilds.models.Member;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MemberDaoImplementation implements MemberDao {
    private final Database database;

    public MemberDaoImplementation(Database database) {
        this.database = database;
    }

    @Override
    public int add(Member member) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("INSERT INTO guild_members (player_uuid, guild_id, role) VALUES (?, ?, ?);");
        stmt.setString(1, member.getPlayerUuid().toString());
        stmt.setInt(2, member.getGuildId());
        stmt.setString(3, member.getRole().toString());

        var rs = stmt.executeUpdate();
        stmt.close();

        return rs;
    }

    @Override
    public void delete(String playerUuid) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("DELETE FROM guild_members WHERE player_uuid = ?;");
        stmt.setString(1, playerUuid);
        stmt.executeUpdate();
        stmt.close();
    }

    public Member getByPlayerUuid(String playerUuid) throws SQLException {
        PreparedStatement stmt = database.getConnection().prepareStatement("SELECT * FROM guild_members WHERE player_uuid = ?;");
        stmt.setString(1, playerUuid);

        var rs = stmt.executeQuery();
        if (rs.next()) {
            var member = new Member();
            member.setPlayerUuid(UUID.fromString(rs.getString("player_uuid")));
            member.setGuildId(rs.getInt("guild_id"));
            member.setRole(MemberRole.fromString(rs.getString("role")));
            member.setCreatedAt(rs.getDate("created_at"));

            rs.close();
            stmt.close();

            return member;
        }

        rs.close();
        stmt.close();

        return null;
    }

    @Override
    public void update(Member member) throws SQLException {

    }
}
