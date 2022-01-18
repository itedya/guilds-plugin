package com.itedya.itedyaguilds.commands;

import com.earth2me.essentials.OfflinePlayer;
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

public class KickOutOfGuild implements CommandExecutor {
    public static void initialize(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("wyrzuczgildii")).setExecutor(new KickOutOfGuild());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            // Check if sender is player, not a console
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be in game!");
                return true;
            }

            // Check length of arguments
            if (args.length != 1) {
                return false;
            }

            // Get guild info
            Guild guild = GuildsController.getPlayerGuild(player);
            assert guild != null;

            // Check if user is owner of the guild
            if (!GuildsController.isPlayerOwnerOfGuild(player, guild)) {
                sender.sendMessage(ChatColor.YELLOW + "Nie jestes wlascicielem gildii!");
                return true;
            }

            // Check if player is trying to kick himself
            if (player.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatColor.YELLOW + "Nie mozesz wyrzucic sam siebie!");
                return true;
            }

            Player playerToKick = (Player) Bukkit.getOfflinePlayer(args[0]);

            // Kick player out of guild
            GuildsController.removeMember(playerToKick);
            WorldGuardController.removePlayerFromGuildCuboid(playerToKick, guild);
            Database.connection.commit();

            if (playerToKick.isOnline()) {
                playerToKick.sendMessage(ChatColor.YELLOW + "Zostales wyrzucony z gildii " + guild.name + "!");
            }

            player.sendMessage(ChatColor.YELLOW + "Wyrzuciles uzytkownika " + playerToKick.getName());

            return true;
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sender.sendMessage(ChatColor.RED + "Wystapil blad po stronie serwera, skontaktuj sie z administartorem.");
            e.printStackTrace();
            return true;
        }
    }
}
