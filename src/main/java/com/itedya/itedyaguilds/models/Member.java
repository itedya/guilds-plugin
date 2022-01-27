package com.itedya.itedyaguilds.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "members")
public class Member {
    @DatabaseField(foreign = true, columnName = "user_id")
    private String userId;

    @DatabaseField(foreign = true, columnName = "guild_id")
    private Guild guild;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
