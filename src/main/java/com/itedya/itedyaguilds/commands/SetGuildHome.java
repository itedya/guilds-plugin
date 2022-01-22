package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class SetGuildHome {
    private Logger logger;

    public static SetGuildHome initialize(JavaPlugin plugin) {
        var command = new SetGuildHome();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.set-home")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(ConfigController.getYouAreNotInGuildMessage());
                return true;
            }

            if (! GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                player.sendMessage(ConfigController.getYouHaveToBeOwnerOfGuildMessage());
                return true;
            }



            if (!WorldGuardController.isPlayerInGuildRegion(player, guild)) {
                player.sendMessage(ConfigController.getLocationIsNotInCuboid());
                return true;
            }

            var loc = player.getLocation();
            GuildHome gh = GuildsController.updateGuildHome(guild.getHome(), loc);
            Database.connection.commit();

            player.sendMessage(new StringBuilder()
                    .append(ChatColor.GRAY + "Ustawiono nowy dom gildii na kordynatach ")
                    .append(ChatColor.YELLOW + "X" + ChatColor.GRAY + ": " + gh.x + " ")
                    .append(ChatColor.YELLOW + "Y" + ChatColor.GRAY + ": " + gh.y + " ")
                    .append(ChatColor.YELLOW + "Z" + ChatColor.GRAY + ": " + gh.z)
                    .toString());

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "set up a home guild at coords " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " +
                    "for guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

            return true;
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            this.logger.severe(e.getMessage());
            e.printStackTrace();
            player.sendMessage(ConfigController.getServerErrorMessage());
            return true;
        }
    }
}
