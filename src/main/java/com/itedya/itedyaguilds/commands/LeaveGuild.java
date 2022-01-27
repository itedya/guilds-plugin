package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.exceptions.NotEnoughPermissionsException;
import com.itedya.itedyaguilds.exceptions.PlayerCantBeOwnerOfGuildException;
import com.itedya.itedyaguilds.exceptions.PlayerMustBeInGuildException;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.utils.CommandUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Logger;

public class LeaveGuild {
    private final Logger logger;

    public LeaveGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player) throws SQLException, PlayerMustBeInGuildException, NotEnoughPermissionsException, PlayerCantBeOwnerOfGuildException, ParseException {
        try {
            CommandUtil.playerMustHavePermission(player, "itedya-guilds.exit");

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) throw new PlayerMustBeInGuildException();

            CommandUtil.playerCantBeOwnerOfGuild(player, guild);

            GuildsController.removeMember(player);
            Database.connection.commit();
            WorldGuardController.removePlayerFromGuildCuboid(player, guild);

            this.logger.info("User ? ? left guild ? [?] ?"
                    .replace("?", player.getName())
                    .replace("?", player.getUniqueId().toString())
                    .replace("?", guild.uuid.toString())
                    .replace("?", guild.short_name)
                    .replace("?", guild.name));

            player.sendMessage(MessagesController.getMessage("you_left_the_guild")
                    .replaceAll("\\{GUILD_NAME}", guild.name));

            var members = guild.getMembers();
            for (var member : members) {
                member.player.sendMessage(MessagesController.getMessage("player_left_the_guild")
                        .replaceAll("\\{PLAYER_NAME}", player.getName()));
            }
        } catch (Exception e) {
            Database.connection.rollback();
            throw e;
        }

        return true;
    }
}
