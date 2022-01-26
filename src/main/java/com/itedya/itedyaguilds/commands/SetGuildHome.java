package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
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
    private final Logger logger;

    public SetGuildHome(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.set-home")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(MessagesController.getMessage("you_are_not_in_guild"));
                return true;
            }

            if (!GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                player.sendMessage(MessagesController.getMessage("you_have_to_be_owner_of_guild"));
                return true;
            }


            if (!WorldGuardController.isPlayerInGuildRegion(player, guild)) {
                player.sendMessage(MessagesController.getMessage("location_is_not_in_cuboid"));
                return true;
            }

            var loc = player.getLocation();
            GuildHome gh = GuildsController.updateGuildHome(guild.getHome(), loc);
            Database.connection.commit();

            player.sendMessage(ChatColor.GRAY + "Ustawiono nowy dom gildii na kordynatach " +
                            ChatColor.YELLOW + "X" + ChatColor.GRAY + ": " + gh.x + " " +
                            ChatColor.YELLOW + "Y" + ChatColor.GRAY + ": " + gh.y + " " +
                            ChatColor.YELLOW + "Z" + ChatColor.GRAY + ": " + gh.z);

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "set up a home guild at coords " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " +
                    "for guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            this.logger.severe(e.getMessage());
            e.printStackTrace();
            player.sendMessage(MessagesController.getMessage("server_error"));
        }
        return true;
    }
}
