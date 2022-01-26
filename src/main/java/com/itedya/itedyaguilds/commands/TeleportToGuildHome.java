package com.itedya.itedyaguilds.commands;

import com.itedya.anticombatlog.controllers.PlayerCountdownController;
import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.MessagesController;
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
    private final Logger logger;
    private final ItedyaGuilds plugin;

    public TeleportToGuildHome(ItedyaGuilds itedyaGuildsplugin) {
        plugin = itedyaGuildsplugin;
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.home")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(MessagesController.getMessage("you_are_not_in_guild"));
                return true;
            }

            if (PlayerCountdownController.isPlayerInBattle(player)) {
                player.sendMessage(MessagesController.getMessage("you_cant_be_in_battle"));
                return true;
            }

            GuildHome gh = guild.getHome();
            assert gh != null;

            var seconds = ConfigController.getSecondsToTeleportation();

            player.sendMessage(MessagesController.getMessage("teleportation_in")
                    .replaceAll("\\{SECONDS}", String.valueOf(seconds)));

            var loc = player.getLocation();

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                private final Logger logger = plugin.getLogger();

                @Override
                public void run() {
                    var locNow = player.getLocation();

                    if (loc.getX() != locNow.getX() ||
                            loc.getY() != locNow.getY() ||
                            loc.getZ() != locNow.getZ()) {
                        player.sendMessage(MessagesController.getMessage("moved_while_teleporting"));
                    } else {
                        player.teleport(new Location(Bukkit.getWorld("world"), gh.x, gh.y, gh.z));

                        this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                                "teleported to guild home at coords " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " +
                                "of guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);
                    }
                }
            }, 20L * seconds);

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "requested a teleport to guild home at coords " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " +
                    "of guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            player.sendMessage(MessagesController.getMessage("server_error"));
            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
