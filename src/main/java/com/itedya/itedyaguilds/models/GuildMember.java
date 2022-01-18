package com.itedya.itedyaguilds.models;

import org.bukkit.entity.Player;

public class GuildMember {
    public Player player;
    public Guild guild;
    public String role;

    public GuildMember(Player player, Guild guild, String role) {
        this.player = player;
        this.guild = guild;
        this.role = role;
    }
}
