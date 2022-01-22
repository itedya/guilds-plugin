package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

public class GuildInfo {
    private Logger logger;

    public static GuildInfo intialize(Plugin plugin) {
        var command = new GuildInfo();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.info")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            Guild guildToCheck = null;

            if (args.length == 0) {
                guildToCheck = GuildsController.getPlayerGuild(player);
                if (guildToCheck == null) {
                    player.sendMessage(ConfigController.getYouAreNotInGuildMessage());
                    return true;
                }
            } else {
                guildToCheck = GuildsController.getGuildByShortName(args[0]);

                if (guildToCheck == null) {
                    player.sendMessage(ConfigController.getGuildDoesntExist());
                    return true;
                }
            }

            List<GuildMember> members = guildToCheck.getMembers();

            StringBuilder sb = new StringBuilder()
                    .append("&7---------------- &bGildia &6" + guildToCheck.short_name + " &7----------------\n")
                    .append("Nazwa: " + guildToCheck.name + "\n")
                    .append("Krotka nazwa: " + guildToCheck.short_name + "\n")
                    .append("Czlonkowie\n");

            for (GuildMember member : members) {
                sb.append("&7 - &2" + member.player.getName() + "&7 - ");

                switch (member.role) {
                    case "OWNER" -> sb.append("&6WLASCICIEL");
                    case "MEMBER" -> sb.append("&7CZLONEK");
                    default -> throw new Exception("Unknown enum value " + member.role);
                }

                sb.append("\n");
            }
            sb.append("&7---------------- &bGildia &6" + guildToCheck.short_name + " &7----------------");

            for (String line : sb.toString().split("\n")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        } catch (Exception e) {
            player.sendMessage(ConfigController.getServerErrorMessage());
            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
