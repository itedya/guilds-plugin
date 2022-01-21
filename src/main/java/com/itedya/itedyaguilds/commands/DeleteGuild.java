package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
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

public class DeleteGuild implements CommandExecutor {
    private Logger logger;

    public static void initialize(JavaPlugin plugin) {
        var command = new DeleteGuild();
        command.logger = plugin.getLogger();
        Objects.requireNonNull(plugin.getCommand("usungildie")).setExecutor(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be in game to execute this command!");
                return true;
            }

            if (!GuildsController.isPlayerInGuild(player)) {
                player.sendMessage(ChatColor.YELLOW + "Nie jestes w gildii!");
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            assert guild != null : "Guild is null";

            GuildMember member = guild.getMembers().stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
            assert member != null : "Member is null";

            if (!member.role.equals("OWNER")) {
                sender.sendMessage(ChatColor.YELLOW + "Musisz byc wlasicielem gildii zeby to zrobic!");
                return true;
            }

            try {
                GuildsController.delete(guild);
                Database.connection.commit();
                WorldGuardController.removeGuildRegion(guild);
            } catch (SQLException e) {
                Database.connection.rollback();
            }

            sender.sendMessage(ChatColor.GRAY + "Usunales gildie " + ChatColor.YELLOW + guild.name + ChatColor.GRAY + "!");

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "deleted guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Wystapil blad po stronie serwera, skontaktuj sie z administartorem.");
            e.printStackTrace();
            return true;
        }
    }
}
