package com.itedya.itedyaguilds.models;

import com.itedya.itedyaguilds.enums.MemberRole;

import java.util.Date;
import java.util.UUID;

public class Member {
    private UUID playerUuid;
    private Integer guildId;
    private MemberRole role;
    private Date createdAt;

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public void setGuildId(Integer guildId) {
        this.guildId = guildId;
    }

    public MemberRole getRole() {
        return role;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
