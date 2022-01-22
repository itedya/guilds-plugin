package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.models.Guild;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "itedya";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "guild";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.3.1";
    }

    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("short_name_formatted")) {
            try {
                Guild guild = GuildsController.getPlayerGuild(player);
                if (guild == null) return "";

                return ChatColor.GRAY + "[" + ChatColor.YELLOW + guild.short_name + ChatColor.GRAY + "]";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (params.equals("short_name_formatted_with_space")) {
            try {
                Guild guild = GuildsController.getPlayerGuild(player);
                if (guild == null) return "";

                return ChatColor.GRAY + "[" + ChatColor.YELLOW + guild.short_name + ChatColor.GRAY + "] ";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}