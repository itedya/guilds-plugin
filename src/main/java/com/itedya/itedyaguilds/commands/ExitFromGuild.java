package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class ExitFromGuild implements CommandExecutor {
    private Logger logger = Bukkit.getLogger();

    public static void initialize(JavaPlugin plugin) {
        var command = new ExitFromGuild();
        command.logger = plugin.getLogger();
        Objects.requireNonNull(plugin.getCommand("wyjdzzgildii")).setExecutor(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be a player to execute this command!");
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);

            // Check if player is in guild
            if (guild == null) {
                sender.sendMessage(ChatColor.YELLOW + "Nie jestes w gildii!");
                return true;
            }

            // Check if player isn't owner of guild
            if (GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                player.sendMessage(ChatColor.YELLOW + "Jestes wlascicielem gildii! Mozesz co najwyzej ja usunac.");
                return true;
            }

            GuildsController.removeMember(player);
            Database.connection.commit();
            WorldGuardController.removePlayerFromGuildCuboid(player, guild);

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "left guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            sender.sendMessage(ChatColor.GREEN + "Wyszedles z gildii " + guild.name);
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sender.sendMessage(ChatColor.RED + "Wystapil blad po stronie serwera, skontaktuj sie z administratorem!");
            logger.severe("[ItedyaGuilds] SQLException! " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
