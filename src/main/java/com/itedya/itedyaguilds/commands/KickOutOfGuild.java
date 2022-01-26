package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class KickOutOfGuild {
    private final Logger logger;

    public KickOutOfGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.kick-out")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            // Check length of arguments
            if (args.length != 1) {
                return false;
            }

            // Get guild info
            Guild guild = GuildsController.getPlayerGuild(player);
            assert guild != null;

            // Check if user is owner of the guild
            if (!GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                player.sendMessage(MessagesController.getMessage("you_have_to_be_owner_of_a_guild"));
                return true;
            }

            // Check if player is trying to kick himself
            if (player.getName().equalsIgnoreCase(args[0])) {
                player.sendMessage(MessagesController.getMessage("you_cant_kick_yourself"));
                return true;
            }

            Player playerToKick = (Player) Bukkit.getOfflinePlayer(args[0]);

            // Kick player out of guild
            GuildsController.removeMember(playerToKick);
            WorldGuardController.removePlayerFromGuildCuboid(playerToKick, guild);
            Database.connection.commit();

            if (playerToKick.isOnline()) {
                playerToKick.sendMessage(MessagesController.getMessage("you_have_been_kicked_out_of_guild")
                        .replaceAll("\\{GUILD_NAME}", guild.name));
            }

            player.sendMessage(MessagesController.getMessage("you_kicked_out_a_user").replaceAll("\\{PLAYER_NAME}", playerToKick.getName()));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "kicked out user " + playerToKick.getName() + " " + playerToKick.getUniqueId() + " " +
                    "from guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

            var members = guild.getMembers();
            for (var member : members) {
                member.player.sendMessage(MessagesController.getMessage("player_has_been_kicked_out_of_guild")
                        .replaceAll("\\{PLAYER_NAME}", playerToKick.getName()));
            }

            return true;
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            player.sendMessage(MessagesController.getMessage("server_error"));
            e.printStackTrace();
            return true;
        }
    }
}
