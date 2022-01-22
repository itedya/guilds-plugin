package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class DeleteGuild {
    private Logger logger;

    public static DeleteGuild initialize(JavaPlugin plugin) {
        var command = new DeleteGuild();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.delete")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            if (!GuildsController.isPlayerInGuild(player)) {
                player.sendMessage(ConfigController.getYouAreNotInGuildMessage());
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            assert guild != null : "Guild is null";

            GuildMember member = guild.getMembers().stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
            assert member != null : "Member is null";

            if (!member.role.equals("OWNER")) {
                player.sendMessage(ConfigController.getYouHaveToBeOwnerOfGuildMessage());
                return true;
            }

            try {
                GuildsController.delete(guild);
                Database.connection.commit();
                WorldGuardController.removeGuildRegion(guild);
            } catch (SQLException e) {
                Database.connection.rollback();
            }

            player.sendMessage(ConfigController.getDeletedGuildMessage(guild.name));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "deleted guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            return true;
        } catch (Exception e) {
            player.sendMessage(ConfigController.getServerErrorMessage());
            e.printStackTrace();
            return true;
        }
    }
}
