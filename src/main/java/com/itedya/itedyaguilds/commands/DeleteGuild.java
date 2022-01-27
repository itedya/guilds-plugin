package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.GuildMember;
import com.itedya.itedyaguilds.utils.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class DeleteGuild {
    private final Logger logger;

    public DeleteGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player) {
        try {
            CommandUtil.playerMustHavePermission(player, "itedya-guilds.delete");
            CommandUtil.playerMustBeInGuild(player);

            Guild guild = GuildsController.getPlayerGuild(player);
            assert guild != null : "Guild is null";

            GuildHome gh = guild.getHome();
            assert gh != null : "Guild home is null";

            GuildMember member = guild.getMembers().stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
            assert member != null : "Member is null";

            if (!member.role.equals("OWNER")) {
                player.sendMessage(MessagesController.getMessage("you_have_to_be_owner_of_guild"));
                return true;
            }

            GuildsController.deleteHome(gh);
            GuildsController.delete(guild);
            Database.connection.commit();

            WorldGuardController.removeGuildRegion(guild);

            player.sendMessage(MessagesController.getMessage("deleted_guild")
                    .replaceAll("\\{GUILD_NAME}", guild.name));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "deleted guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            return true;
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            player.sendMessage(MessagesController.getMessage("server_error"));
            e.printStackTrace();
            return true;
        }
    }
}
