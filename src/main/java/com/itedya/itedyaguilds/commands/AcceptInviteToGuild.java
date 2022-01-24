package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class AcceptInviteToGuild {
    private Logger logger;

    public static AcceptInviteToGuild initialize(JavaPlugin plugin) {
        var command = new AcceptInviteToGuild();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.accept-invite")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild != null) {
                player.sendMessage(ConfigController.getYouAreAlreadyInGuildMessage());
                return true;
            }

            guild = InvitesController.getGuildThatInvitesPlayer(player);

            if (guild == null) {
                player.sendMessage(ConfigController.getYouDontHaveInviteToGuild());
                return true;
            }

            GuildsController.addPlayerToGuild(player, guild, "MEMBER");
            WorldGuardController.addPlayerToGuildCuboid(player, guild);
            player.sendMessage(ConfigController.getWelcomeToGuildMessage(guild.name, guild.short_name));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "accepted invite to guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            var members = guild.getMembers();
            for (var member : members) {
                member.player.sendMessage(ConfigController.getPlayerHasBeenAddedToGuildMessage(player.getName()));
            }
        } catch (Exception e) {
            player.sendMessage(ConfigController.getServerErrorMessage());
            e.printStackTrace();
        }

        return true;
    }
}
