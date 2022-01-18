package com.itedya.itedyaguilds.builders;

import com.itedya.itedyaguilds.models.Guild;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GuildBuilder {
    private UUID _uuid = UUID.randomUUID();
    private String _name;
    private String _short_name;
    private Date _created_at = new Date();

    public GuildBuilder() {}

    public GuildBuilder setUUID(String uuid) {
        this._uuid = UUID.fromString(uuid);
        return this;
    }

    public GuildBuilder setName(String name) {
        this._name = name;
        return this;
    }

    public GuildBuilder setShortName(String short_name) {
        this._short_name = short_name;
        return this;
    }

    public GuildBuilder setCreatedAt(String createdAt) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this._created_at = df.parse(createdAt);
        return this;
    }

    public Guild build() {
        return new Guild(_uuid, _name, _short_name, _created_at);
    }
}
