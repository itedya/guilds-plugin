package com.itedya.itedyaguilds.commands;

import com.itedya.anticombatlog.controllers.PlayerCountdownController;
import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Logger;

public class TeleportToGuildHome {
    private Logger logger;
    private ItedyaGuilds plugin;

    public static TeleportToGuildHome initialize(ItedyaGuilds plugin) {
        var command = new TeleportToGuildHome();
        command.plugin = plugin;
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.home")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(ConfigController.getYouAreNotInGuildMessage());
                return true;
            }

            if (PlayerCountdownController.isPlayerInBattle(player)) {
                player.sendMessage(ConfigController.getYouCantBeInBattle());
                return true;
            }

            GuildHome gh = guild.getHome();
            assert gh != null;

            player.sendMessage(ConfigController.getTeleportationIn(5));

            var loc = player.getLocation();

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                private Logger logger = plugin.getLogger();

                @Override
                public void run() {
                    var locNow = player.getLocation();

                    if (loc.getX() != locNow.getX() ||
                            loc.getY() != locNow.getY() ||
                            loc.getZ() != locNow.getZ()) {
                        player.sendMessage(ConfigController.getMovedWhileTeleporting());
                    } else {
                        player.teleport(new Location(Bukkit.getWorld("world"), gh.x, gh.y, gh.z));

                        this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                                "teleported to guild home at coords " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " +
                                "of guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);
                    }
                }
            }, 20 * 5);

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "requested a teleport to guild home at coords " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " +
                    "of guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            player.sendMessage(ConfigController.getServerErrorMessage());
            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
