package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
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
    private final Logger logger;

    public GuildInfo(Plugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.info")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            Guild guildToCheck;

            if (args.length == 0) {
                guildToCheck = GuildsController.getPlayerGuild(player);
                if (guildToCheck == null) {
                    player.sendMessage(MessagesController.getMessage("you_are_not_in_guild"));
                    return true;
                }
            } else {
                guildToCheck = GuildsController.getGuildByShortName(args[0]);

                if (guildToCheck == null) {
                    player.sendMessage(MessagesController.getMessage("guild_does_not_exist"));
                    return true;
                }
            }

            List<GuildMember> members = guildToCheck.getMembers();

            String sb = "&7---------------- &bGildia &6" + guildToCheck.short_name + " &7----------------\n" +
                    "Nazwa: " + guildToCheck.name + "\n" +
                    "Krotka nazwa: " + guildToCheck.short_name + "\n" +
                    "Czlonkowie\n";

            for (GuildMember member : members) {
                sb += "&7 - &2" + member.player.getName() + "&7 - ";

                switch (member.role) {
                    case "OWNER" -> sb += "&6WLASCICIEL";
                    case "MEMBER" -> sb += "&7CZLONEK";
                    default -> throw new Exception("Unknown enum value " + member.role);
                }

                sb += "\n";
            }
            sb += "&7---------------- &bGildia &6" + guildToCheck.short_name + " &7----------------";

            for (String line : sb.split("\n")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        } catch (Exception e) {
            player.sendMessage(MessagesController.getMessage("server_error"));
            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
