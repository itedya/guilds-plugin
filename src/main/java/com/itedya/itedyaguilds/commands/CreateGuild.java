package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.*;
import com.itedya.itedyaguilds.exceptions.*;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.NeededItem;
import com.itedya.itedyaguilds.utils.CommandUtil;
import com.itedya.itedyaguilds.validators.GuildNameValidator;
import com.itedya.itedyaguilds.validators.GuildShortNameValidator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
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

    public boolean onCommand(@NotNull Player player, @NotNull String[] args) throws NotEnoughPermissionsException, SQLException, PlayerIsAlreadyInGuildException, InvalidUsageException, ValidationException, NeededItemsException {
        CommandUtil.playerMustHavePermission(player, "itedya-guilds.create");
        if (args.length != 2) throw new InvalidUsageException();
        CommandUtil.playerCantBeInGuild(player);

        // check items for guild
        List<NeededItem> neededItems = NeededItemsController.getNeededItems(player);
        if (neededItems.size() != 0) throw new NeededItemsException(neededItems);

        this.validateArguments(args);

        Guild guild = null;

        try {
            GuildHome guildHome = GuildsController.createGuildHome(player.getLocation());
            guild = GuildsController.createGuild(args[0], args[1], guildHome);
            GuildsController.addPlayerToGuild(player, guild, "OWNER");
            WorldGuardController.createGuildRegion(player.getLocation(), guild);
            WorldGuardController.addPlayerToGuildCuboid(player, guild);

            Database.connection.commit();

            this.logger.info("User ? ? created guild ? [?] ?"
                    .replace("?", player.getUniqueId().toString())
                    .replace("?", player.getName())
                    .replace("?", guild.uuid.toString())
                    .replace("?", guild.short_name)
                    .replace("?", guild.name));
        } catch (SQLException e) {
            Database.connection.rollback();
            String message = e.getMessage();

            if (message.contains("UNIQUE constraint failed")) {
                if (message.contains("name")) {
                    throw new ValidationException(MessagesController.getMessage("guild_name_is_not_unique"));
                } else if (message.contains("short_name")) {
                    throw new ValidationException(MessagesController.getMessage("guild_short_name_is_not_unique"));
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        } catch (IntersectionRegionsException e) {
            Database.connection.rollback();

            player.sendMessage(MessagesController.getMessage("cuboid_intersection"));
            return true;
        }

        NeededItemsController.takeGuildNeededItems(player.getInventory());

        player.sendMessage("&7Stworzyles gildie [?] ?"
                .replace("?", guild.short_name)
                .replace("?", guild.name));

        return true;
    }

    public void validateArguments(String[] args) throws ValidationException {
        String validateName = new GuildNameValidator(args[0]).validate();
        String validateShortName = new GuildShortNameValidator(args[1]).validate();

        if (validateName != null) throw new ValidationException(validateName);
        if (validateShortName != null) throw new ValidationException(validateShortName);
    }
}
