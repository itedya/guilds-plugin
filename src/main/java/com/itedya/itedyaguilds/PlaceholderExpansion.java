package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "itedya";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "itedya-guilds";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("guild_short_name")) {
            try {
                Guild guild = GuildsController.getPlayerGuild((Player) player);
                if (guild == null) return "BEZ GILDII";

                return guild.short_name;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}