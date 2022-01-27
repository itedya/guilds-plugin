package com.itedya.itedyaguilds.utils;

import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.exceptions.NotEnoughPermissionsException;
import com.itedya.itedyaguilds.exceptions.PlayerIsAlreadyInGuildException;
import com.itedya.itedyaguilds.exceptions.PlayerMustBeInGuildException;
import com.itedya.itedyaguilds.exceptions.PlayerMustBeOwnerOfGuildException;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.ParseException;

public class CommandUtil {
    private CommandUtil() throws Exception {
        throw new Exception("Don't create an instance of util class!");
    }

    /**
     * Alternative hasPermission check utility
     * Throws NotEnoughPermissionsException when player does not have permission.
     *
     * @param permission
     * @throws NotEnoughPermissionsException
     */
    public static void playerMustHavePermission(Player player, String permission) throws NotEnoughPermissionsException {
        if (!player.hasPermission(permission)) {
            throw new NotEnoughPermissionsException();
        }
    }

    /**
     * Player can't be in guild check utility
     * If player is in guild, throws PlayerIsAlreadyInGuildException
     *
     * @param player
     * @throws SQLException
     * @throws PlayerIsAlreadyInGuildException
     */
    public static void playerCantBeInGuild(Player player) throws SQLException, PlayerIsAlreadyInGuildException {
        if (GuildsController.isPlayerInGuild(player)) {
            throw new PlayerIsAlreadyInGuildException();
        }
    }

    public static void playerMustBeInGuild(Player player) throws SQLException, PlayerMustBeInGuildException {
        if (!GuildsController.isPlayerInGuild(player)) {
            throw new PlayerMustBeInGuildException();
        }
    }

    public static void playerMustBeOwnerOfGuild(Player player, Guild guild) throws SQLException, ParseException, PlayerMustBeOwnerOfGuildException {
        if (!GuildsController.isPlayerOwnerOfGuild(player, guild)) {
            throw new PlayerMustBeOwnerOfGuildException();
        }
    }
}
