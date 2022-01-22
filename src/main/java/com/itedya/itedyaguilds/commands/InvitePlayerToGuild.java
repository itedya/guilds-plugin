package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildMember;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

public class InvitePlayerToGuild {
    private Logger logger;

    public static InvitePlayerToGuild initialize(JavaPlugin plugin) {
        var command = new InvitePlayerToGuild();
        command.logger = plugin.getLogger();
        return command;
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.invite")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            if (args.length != 1) {
                return false;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(ConfigController.getYouAreNotInGuildMessage());
                return true;
            }

            List<GuildMember> members = guild.getMembers();

            GuildMember member = members.stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
            assert member != null : "Member is null";

            if (!member.role.equals("OWNER")) {
                player.sendMessage(ConfigController.getYouHaveToBeOwnerOfGuildMessage());
                return true;
            }

            Player playerToInvite = Bukkit.getPlayer(args[0]);
            if (playerToInvite == null) {
                player.sendMessage(ConfigController.getPlayerDoesntExist(args[0]));
                return true;
            }

            if (GuildsController.getPlayerGuild(playerToInvite) != null) {
                player.sendMessage(ConfigController.getPlayerIsAlreadyInGuild(playerToInvite.getName()));
                return true;
            }

            if (InvitesController.getGuildThatInvitesPlayer(playerToInvite) != null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz " + playerToInvite.getName() + " jest juz zaproszony do jednej gildii! Poczekaj 60s na przeterminowanie zaproszenia.");
                return true;
            }

            InvitesController.addGuildInvite(playerToInvite, guild);
            playerToInvite.sendMessage(ConfigController.getYouGotInviteMessage(playerToInvite.getName(), guild.name, guild.short_name));

            player.sendMessage(ConfigController.getSentInviteMessage());

            this.logger.info("User " + player.getName() + " " + player.getUniqueId().toString() + " " +
                    "invited user " + playerToInvite.getName() + " " + playerToInvite.getUniqueId().toString() + " " +
                    "to guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

            return true;
        } catch (Exception e) {
            player.sendMessage(ConfigController.getServerErrorMessage());
            e.printStackTrace();
            return true;
        }
    }
}
