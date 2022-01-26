package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.*;
import com.itedya.itedyaguilds.exception.IntersectionRegionsException;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.NeededItem;
import com.itedya.itedyaguilds.validators.GuildNameValidator;
import com.itedya.itedyaguilds.validators.GuildShortNameValidator;
import org.apache.logging.log4j.message.Message;
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
    private final Logger logger;

    public CreateGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.create")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            if (args.length != 2) {
                player.sendMessage(MessagesController.getMessage("invalid_usage"));
                for (var line : ConfigController.help) player.sendMessage(line);
                return true;
            }

            if (GuildsController.isPlayerInGuild(player)) {
                player.sendMessage(MessagesController.getMessage("you_are_already_in_guild"));
                return true;
            }

            Guild guild = null;

            List<NeededItem> neededItems = NeededItemsController.getNeededItems(player);

            if (neededItems.size() != 0) {
                NeededItemsController.sendNeededItemsErrors(player, neededItems);
                return true;
            }

            String validateName = new GuildNameValidator(args[0]).validate();
            String validateShortName = new GuildShortNameValidator(args[1]).validate();

            if (validateName != null) {
                player.sendMessage(validateName);
                return true;
            }

            if (validateShortName != null) {
                player.sendMessage(validateShortName);
                return true;
            }

            try {
                GuildHome guildHome = GuildsController.createGuildHome(player.getLocation());
                guild = GuildsController.createGuild(args[0], args[1], guildHome);
                GuildsController.addPlayerToGuild(player, guild, "OWNER");
                WorldGuardController.createGuildRegion(player.getLocation(), guild);
                WorldGuardController.addPlayerToGuildCuboid(player, guild);

                Database.connection.commit();

                this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
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
                        player.sendMessage(MessagesController.getMessage("guild_name_is_not_unique"));
                    } else if (message.contains("short_name")) {
                        player.sendMessage(MessagesController.getMessage("guild_short_name_is_not_unique"));
                    } else {
                        throw e;
                    }

                    return true;
                } else {
                    throw e;
                }
            } catch (IntersectionRegionsException e) {
                player.sendMessage(MessagesController.getMessage("cuboid_intersection"));
                GuildsController.delete(guild);
                return true;
            }

            NeededItemsController.takeGuildNeededItems(player.getInventory());

            player.sendMessage(ChatColor.GRAY + "Stworzyles gildie o nazwie "
                    + ChatColor.YELLOW + args[0] + ChatColor.GRAY +
                    ", a jej prefix to [" + ChatColor.YELLOW + args[1] + ChatColor.GRAY + "]");

            return true;
        } catch (Exception e) {
            player.sendMessage(MessagesController.getMessage("server_error"));

            this.logger.severe("[ItedyaGuilds] SQLException! " + e.getMessage());
            e.printStackTrace();
            return true;
        }

    }
}
