package com.itedya.itedyaguilds.builders;

import com.itedya.itedyaguilds.models.GuildHome;

import java.util.UUID;

public class GuildHomeBuilder {
    private UUID uuid;
    private Integer x;
    private Integer y;
    private Integer z;

    public GuildHomeBuilder() { }

    public GuildHomeBuilder setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public GuildHomeBuilder setUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
        return this;
    }

    public GuildHomeBuilder setX(Integer x) {
        this.x = x;
        return this;
    }

    public GuildHomeBuilder setY(Integer y) {
        this.y = y;
        return this;
    }

    public GuildHomeBuilder setZ(Integer z) {
        this.z = x;
        return this;
    }

    public GuildHome build() {
        return new GuildHome(this.uuid, this.x, this.y, this.z);
    }
}
