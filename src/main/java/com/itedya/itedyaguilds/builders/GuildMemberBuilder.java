package com.itedya.itedyaguilds.builders;

import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.entity.Player;

public class GuildMemberBuilder {
    private Guild _guild;
    private Player _player;
    private String _role = "OWNER";

    public GuildMemberBuilder() {}

    public GuildMemberBuilder setGuild(Guild guild) {
        this._guild = guild;
        return this;
    }

    public GuildMemberBuilder setPlayer(Player player) {
        this._player = player;
        return this;
    }

    public GuildMemberBuilder setRole(String role) {
        this._role = role;
        return this;
    }

    public GuildMember build() {
        return new GuildMember(this._player, this._guild, this._role);
    }
}
