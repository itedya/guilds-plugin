package com.itedya.itedyaguilds.commands;

import com.itedya.anticombatlog.controllers.PlayerCountdownController;
import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class TeleportToGuildHome implements CommandExecutor {
    private Logger logger;
    private ItedyaGuilds plugin;

    public static void initialize(ItedyaGuilds plugin) {
        var command = new TeleportToGuildHome();
        command.plugin = plugin;
        command.logger = plugin.getLogger();
        Objects.requireNonNull(plugin.getCommand("domgildii")).setExecutor(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be in game to use this command!");
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(ChatColor.YELLOW + "Musisz byc w gildii zeby wykonac ta koemnde!");
                return true;
            }

            if (PlayerCountdownController.isPlayerInBattle(player)) {
                player.sendMessage(ChatColor.YELLOW + "Nie mozesz uzyc tej komendy podczas walki!");
                return true;
            }

            GuildHome gh = guild.getHome();
            assert gh != null;

            player.sendMessage(ChatColor.GOLD + "Teleportacja za 5 sekund...");

            var loc = player.getLocation();

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                private Logger logger = plugin.getLogger();

                @Override
                public void run() {
                    var locNow = player.getLocation();

                    if (loc.getX() != locNow.getX() ||
                            loc.getY() != locNow.getY() ||
                            loc.getZ() != locNow.getZ()) {
                        player.sendMessage(ChatColor.YELLOW + "Ruszyles sie! Teleportacja anulowana!");
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
            sender.sendMessage("Wystapil blad po stronie serwera, skontaktuj sie z administratorem lub sprobuj jeszcze raz.");
            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
