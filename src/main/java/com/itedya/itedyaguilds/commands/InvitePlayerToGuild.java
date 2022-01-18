package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

public class InvitePlayerToGuild implements CommandExecutor {
    public static void initialize(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("zaprosdogildii")).setExecutor(new InvitePlayerToGuild());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You have to be in game!");
                return true;
            }

            if (args.length != 1) {
                return false;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                sender.sendMessage(ChatColor.YELLOW + "Nie jestes w gildii!");
                return true;
            }

            List<GuildMember> members = guild.getMembers();

            GuildMember member = members.stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
            assert member != null : "Member is null";

            if (!member.role.equals("OWNER")) {
                sender.sendMessage(ChatColor.YELLOW + "Musisz byc wlasicielem gildii zeby to zrobic!");
                return true;
            }

            Player playerToInvite = Bukkit.getPlayer(args[0]);
            if (playerToInvite == null) {
                sender.sendMessage(ChatColor.YELLOW + "Gracz " + args[0] + " nie istnieje!");
                return true;
            }

            if (GuildsController.getPlayerGuild(playerToInvite) != null) {
                sender.sendMessage(ChatColor.YELLOW + "Gracz " + playerToInvite.getName() + " jest juz w gildii!");
                return true;
            }

            if (InvitesController.getGuildThatInvitesPlayer(playerToInvite) != null) {
                sender.sendMessage(ChatColor.YELLOW + "Gracz " + playerToInvite.getName() + " jest juz zaproszony do jednej gildii! Poczekaj 60s na przeterminowanie zaproszenia.");
                return true;
            }

            InvitesController.addGuildInvite(playerToInvite, guild);
            playerToInvite.sendMessage(ChatColor.GREEN + "Dostales zaproszenie do gildii " +
                    ChatColor.GRAY + "[" + ChatColor.YELLOW + guild.short_name + ChatColor.GRAY + "] " + guild.name +
                    ChatColor.GREEN + " od gracza " + player.getName() + ". " +
                    "Aby zaakceptowac zaproszenie, wpisz /akceptujzaproszenie");

            player.sendMessage(ChatColor.GREEN + "Wyslano zaproszenie!");

            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Wystapil blad po stronie serwera, skontaktuj sie z administartorem.");
            e.printStackTrace();
            return true;
        }
    }
}
