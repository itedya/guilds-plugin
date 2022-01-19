package com.itedya.itedyaguilds.models;

import java.util.UUID;

public class GuildHome {
    public UUID uuid;
    public Integer x;
    public Integer y;
    public Integer z;

    public GuildHome(UUID uuid, Integer x, Integer y, Integer z) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
