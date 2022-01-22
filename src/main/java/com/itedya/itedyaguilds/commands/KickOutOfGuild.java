package com.itedya.itedyaguilds.commands;

import com.earth2me.essentials.OfflinePlayer;
import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class KickOutOfGuild {
    private Logger logger;

    public static KickOutOfGuild initialize(JavaPlugin plugin) {
        var command = new KickOutOfGuild();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.kick-out")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
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
                player.sendMessage(ConfigController.getYouHaveToBeOwnerOfGuildMessage());
                return true;
            }

            // Check if player is trying to kick himself
            if (player.getName().equalsIgnoreCase(args[0])) {
                player.sendMessage(ConfigController.getYouCantKickYourselfMessage());
                return true;
            }

            Player playerToKick = (Player) Bukkit.getOfflinePlayer(args[0]);

            // Kick player out of guild
            GuildsController.removeMember(playerToKick);
            WorldGuardController.removePlayerFromGuildCuboid(playerToKick, guild);
            Database.connection.commit();

            if (playerToKick.isOnline()) {
                playerToKick.sendMessage(ConfigController.getYouHaveBeenKickedOutOfGuildMessage(guild.name));
            }

            player.sendMessage(ConfigController.getYouKickedOutAUser(playerToKick.getName()));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "kicked out user " + playerToKick.getName() + " " + playerToKick.getUniqueId().toString() + " " +
                    "from guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

            return true;
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            player.sendMessage(ConfigController.getServerErrorMessage());
            e.printStackTrace();
            return true;
        }
    }
}
