package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.NeededItemsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.exception.IntersectionRegionsException;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.NeededItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class CreateGuild {
    private Logger logger = Bukkit.getLogger();

    public static CreateGuild initialize(JavaPlugin plugin) {
        var command = new CreateGuild();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.create")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            if (args.length != 2) {
                player.sendMessage(ConfigController.getInvalidUsageMessage());
                player.sendMessage(ConfigController.help.toString());
                return true;
            }

            if (GuildsController.isPlayerInGuild(player)) {
                player.sendMessage(ConfigController.getYouAreAlreadyInGuildMessage());
                return true;
            }

            Guild guild = null;

            List<NeededItem> neededItems = NeededItemsController.getNeededItems(player);

            if (neededItems.size() != 0) {
                NeededItemsController.sendNeededItemsErrors(player, neededItems);
                return true;
            }

            try {
                GuildHome guildHome = GuildsController.createGuildHome(player.getLocation());
                guild = GuildsController.createGuild(args[0], args[1], guildHome);
                GuildsController.addPlayerToGuild(player, guild, "OWNER");
                WorldGuardController.createGuildRegion(player.getLocation(), guild);
                WorldGuardController.addPlayerToGuildCuboid(player, guild);

                Database.connection.commit();

                this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                        "created guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);
            } catch (SQLException e) {
                try {
                    Database.connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                String message = e.getMessage();

                if (message.contains("UNIQUE constraint failed")) {
                    if (message.contains("name")) {
                        player.sendMessage(ConfigController.getGuildNameIsNotUniqueMessage());
                    } else if (message.contains("short_name")) {
                        player.sendMessage(ConfigController.getGuildShortNameIsNotUniqueMessage());
                    } else {
                        throw e;
                    }

                    return true;
                } else {
                    throw e;
                }
            } catch (IntersectionRegionsException e) {
                player.sendMessage(ConfigController.getCuboidIntersectionMessage());
                GuildsController.delete(guild);
                return true;
            }

            NeededItemsController.takeGuildNeededItems(player.getInventory());

            player.sendMessage(ChatColor.GRAY + "Stworzyles gildie o nazwie "
                    + ChatColor.YELLOW + args[0] + ChatColor.GRAY +
                    ", a jej prefix to [" + ChatColor.YELLOW + args[1] + ChatColor.GRAY + "]");

            return true;
        } catch (Exception e) {
            player.sendMessage(ConfigController.getServerErrorMessage());

            this.logger.severe("[ItedyaGuilds] SQLException! " + e.getMessage());
            e.printStackTrace();
            return true;
        }

    }
}
