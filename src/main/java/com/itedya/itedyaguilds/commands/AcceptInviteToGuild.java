package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.*;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class AcceptInviteToGuild {
    private final Logger logger;

    public AcceptInviteToGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.accept-invite")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild != null) {
                player.sendMessage(MessagesController.getMessage("you_are_already_in_guild"));
                return true;
            }

            guild = InvitesController.getGuildThatInvitesPlayer(player);

            if (guild == null) {
                player.sendMessage(MessagesController.getMessage("you_dont_have_invite_to_guild"));
                return true;
            }

            GuildsController.addPlayerToGuild(player, guild, "MEMBER");
            WorldGuardController.addPlayerToGuildCuboid(player, guild);
            player.sendMessage(MessagesController.getMessage("welcome_to_guild")
                    .replaceAll("\\{GUILD_NAME}", guild.name)
                    .replaceAll("\\{GUILD_SHORT_NAME}", guild.short_name));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "accepted invite to guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            var members = guild.getMembers();
            for (var member : members) {
                member.player.sendMessage(MessagesController.getMessage("player_has_been_added_to_guild")
                        .replaceAll("\\{PLAYER_NAME}", player.getName()));
            }
        } catch (Exception e) {
            player.sendMessage(MessagesController.getMessage("server_error"));
            e.printStackTrace();
        }

        return true;
    }
}
