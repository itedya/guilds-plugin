package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class AcceptInviteToGuild implements CommandExecutor {
    private Logger logger;

    public static void initialize(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("akceptujzaproszenie")).setExecutor(new AcceptInviteToGuild());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be in game to use this command!");
                return true;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild != null) {
                player.sendMessage(ChatColor.YELLOW + "Jestes juz w gildii!");
                return true;
            }

            guild = InvitesController.getGuildThatInvitesPlayer(player);

            if (guild == null) {
                player.sendMessage(ChatColor.YELLOW + "Nie masz zaproszenia do gildii");
                return true;
            }

            GuildsController.addPlayerToGuild(player, guild, "MEMBER");
            WorldGuardController.addPlayerToGuildCuboid(player, guild);
            player.sendMessage(ChatColor.GREEN + "Witamy w gildii " + ChatColor.GRAY + "[" +
                    ChatColor.YELLOW + guild.short_name + ChatColor.GRAY + "] " + ChatColor.YELLOW + guild.name + ChatColor.GRAY + "!");

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "accepted invite to guild " + guild.uuid.toString() + " [" + guild.short_name + "] " + guild.name);

        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Wystapil blad po stronie serwera, skontaktuj sie z administratorem!");
            e.printStackTrace();
        }

        return true;
    }
}
