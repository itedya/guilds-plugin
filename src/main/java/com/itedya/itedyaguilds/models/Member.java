package com.itedya.itedyaguilds.models;

import java.util.UUID;

public class Member {
    private UUID uuid;
    private Guild guild;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
