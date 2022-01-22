package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
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
    private Logger logger = Bukkit.getLogger();

    public static ExitFromGuild initialize(JavaPlugin plugin) {
        var command = new ExitFromGuild();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.exit")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);

            // Check if player is in guild
            if (guild == null) {
                player.sendMessage(ConfigController.getYouAreNotInGuildMessage());
                return true;
            }

            // Check if player is owner of guild
            if (GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                player.sendMessage(ConfigController.getOwnerCanOnlyDeleteGuildMessage());
                return true;
            }

            GuildsController.removeMember(player);
            Database.connection.commit();
            WorldGuardController.removePlayerFromGuildCuboid(player, guild);

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "left guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            player.sendMessage(ConfigController.getExitFromGuildMessge(guild.name));
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            player.sendMessage(ConfigController.getServerErrorMessage());
            logger.severe("[ItedyaGuilds] Exception! " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
