package com.itedya.itedyaguilds.daos;

import com.itedya.itedyaguilds.models.Member;

import java.sql.SQLException;

public interface MemberDao {
    public int add(Member member) throws SQLException;

    public void delete(String playerUuid) throws SQLException;

    public void update(Member member) throws SQLException;

    public Member getByPlayerUuid(String playerUuid) throws SQLException;
}
