package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.*;
import com.itedya.itedyaguilds.exceptions.NotEnoughPermissionsException;
import com.itedya.itedyaguilds.exceptions.PlayerDoesNotHaveInviteToGuildException;
import com.itedya.itedyaguilds.exceptions.PlayerIsAlreadyInGuildException;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.utils.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Logger;

public class AcceptInviteToGuild {
    private final Logger logger;

    public AcceptInviteToGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player) throws SQLException, PlayerIsAlreadyInGuildException, NotEnoughPermissionsException, ParseException, PlayerDoesNotHaveInviteToGuildException {
        CommandUtil.playerMustHavePermission(player, "itedya-guilds.accept-invite");
        CommandUtil.playerCantBeInGuild(player);

        Guild guild = InvitesController.getGuildThatInvitesPlayer(player);

        if (guild == null) throw new PlayerDoesNotHaveInviteToGuildException();

        GuildsController.addPlayerToGuild(player, guild, "MEMBER");
        WorldGuardController.addPlayerToGuildCuboid(player, guild);

        player.sendMessage(MessagesController.getMessage("welcome_to_guild")
                .replaceAll("\\{GUILD_NAME}", guild.name)
                .replaceAll("\\{GUILD_SHORT_NAME}", guild.short_name));

        this.logCommandExecution(player, guild);
        this.sendAnnouncement(player, guild);

        return true;
    }

    public void logCommandExecution(Player player, Guild guild) {
        var message = "User ? ? accepted invite to guild ? [?] ?";

        this.logger.info(message
                .replace("?", player.getName())
                .replace("?", player.getUniqueId().toString())
                .replace("?", guild.uuid.toString())
                .replace("?", guild.short_name)
                .replace("?", guild.name));
    }

    public void sendAnnouncement(Player player, Guild guild) throws SQLException {
        var members = guild.getMembers();

        for (var member : members) {
            member.player.sendMessage(MessagesController.getMessage("player_has_been_added_to_guild")
                    .replaceAll("\\{PLAYER_NAME}", player.getName()));
        }
    }
}
