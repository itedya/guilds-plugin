package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.NeededItemsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.exception.IntersectionRegionsException;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.NeededItem;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
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
import java.util.Objects;
import java.util.logging.Logger;

public class CreateGuild implements CommandExecutor {
    private final Logger logger = Bukkit.getLogger();

    public static void initialize(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("stworzgildie")).setExecutor(new CreateGuild());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (args.length != 2) {
                return false;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be a player to execute this command!");
                return true;
            }

            if (GuildsController.isPlayerInGuild(player)) {
                player.sendMessage(ChatColor.YELLOW + "Jestes juz w gildii! Nie mozesz zalozyc kolejnej!");
                return true;
            }

            Guild guild = null;

            List<NeededItem> neededItems = NeededItemsController.getNeededItems(player);

            if (neededItems.size() == 0) {
                NeededItemsController.takeGuildNeededItems(player.getInventory());
            } else {
                NeededItemsController.sendNeededItemsErrors(player, neededItems);
                return true;
            }

            
            try {
                guild = GuildsController.createGuild(args[0], args[1]);
                GuildsController.addPlayerToGuild(player, guild, "OWNER");
                WorldGuardController.createGuildRegion(player.getLocation(), guild);
                WorldGuardController.addPlayerToGuildCuboid(player, guild);

                Database.connection.commit();
            } catch (SQLException e) {
                try {
                    Database.connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                String message = e.getMessage();

                if (message.contains("UNIQUE constraint failed")) {
                    if (message.contains("name")) {
                        sender.sendMessage(ChatColor.RED + "Gildia o takiej nazwie juz istnieje! Wybierz inny.");
                    } else if (message.contains("short_name")) {
                        sender.sendMessage(ChatColor.RED + "Gildia z takim skrotem juz istnieje! Wybierz inna.");
                    } else {
                        throw e;
                    }

                    return true;
                }
            } catch (IntersectionRegionsException e) {
                player.sendMessage(ChatColor.RED + "Jakis cuboid juz istnieje na tym terenie (cuboid jest rozmiarow 150x150)");
                GuildsController.delete(guild);
                return true;
            }

            player.sendMessage(ChatColor.GRAY + "Stworzyles gildie o nazwie "
                    + ChatColor.YELLOW + args[0] + ChatColor.GRAY +
                    ", a jej prefix to [" + ChatColor.YELLOW + args[1] + ChatColor.GRAY + "]");

            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Wystapil blad po stronie serwera, skontaktuj sie z administratorem!");

            this.logger.severe("[ItedyaGuilds] SQLException! " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
