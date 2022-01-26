package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class ExitFromGuild {
    private final Logger logger;

    public ExitFromGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.exit")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);

            // Check if player is in guild
            if (guild == null) {
                player.sendMessage(MessagesController.getMessage("you_are_not_in_guild"));
                return true;
            }

            // Check if player is owner of guild
            if (GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                player.sendMessage(MessagesController.getMessage("owner_can_only_delete_guild"));
                return true;
            }

            GuildsController.removeMember(player);
            Database.connection.commit();
            WorldGuardController.removePlayerFromGuildCuboid(player, guild);

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "left guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            player.sendMessage(MessagesController.getMessage("exit_from_guild")
                    .replaceAll("\\{GUILD_NAME}", guild.name));

            var members = guild.getMembers();
            for (var member : members) {
                member.player.sendMessage(MessagesController.getMessage("player_exited_from_guild")
                        .replaceAll("\\{PLAYER_NAME}", player.getName()));
            }
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            player.sendMessage(MessagesController.getMessage("server_error"));
            logger.severe("[ItedyaGuilds] Exception! " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
