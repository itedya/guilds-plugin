package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.controllers.MessagesController;
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
    private final Logger logger;

    public InvitePlayerToGuild(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.invite")) {
                player.sendMessage(MessagesController.getMessage("not_enough_permissions"));
                return true;
            }

            if (args.length != 1) {
                return false;
            }

            Guild guild = GuildsController.getPlayerGuild(player);
            if (guild == null) {
                player.sendMessage(MessagesController.getMessage("you_are_not_in_guild"));
                return true;
            }

            List<GuildMember> members = guild.getMembers();

            GuildMember member = members.stream().filter(item -> item.player.getUniqueId() == player.getUniqueId()).findFirst().orElse(null);
            assert member != null : "Member is null";

            if (!member.role.equals("OWNER")) {
                player.sendMessage(MessagesController.getMessage("you_have_to_be_owner_of_a_guild"));
                return true;
            }

            Player playerToInvite = Bukkit.getPlayer(args[0]);
            if (playerToInvite == null) {
                player.sendMessage(MessagesController.getMessage("player_does_not_exist")
                        .replaceAll("\\{PLAYER_NAME}", args[0]));

                return true;
            }

            if (GuildsController.getPlayerGuild(playerToInvite) != null) {
                player.sendMessage(MessagesController.getMessage("player_is_already_in_guild")
                        .replaceAll("\\{PLAYER_NAME}", playerToInvite.getName()));
                return true;
            }

            if (InvitesController.getGuildThatInvitesPlayer(playerToInvite) != null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz " + playerToInvite.getName() + " jest juz zaproszony do jednej gildii! Poczekaj 60s na przeterminowanie zaproszenia.");
                return true;
            }

            InvitesController.addGuildInvite(playerToInvite, guild);
            playerToInvite.sendMessage(MessagesController.getMessage("you_got_invite")
                    .replaceAll("\\{PLAYER_NAME}", playerToInvite.getName())
                    .replaceAll("\\{GUILD_NAME}", guild.name)
                    .replaceAll("\\{GUILD_SHORT_NAME}", guild.short_name));

            player.sendMessage(MessagesController.getMessage("sent_invite"));

            this.logger.info("User " + player.getName() + " " + player.getUniqueId() + " " +
                    "invited user " + playerToInvite.getName() + " " + playerToInvite.getUniqueId() + " " +
                    "to guild " + guild.uuid.toString() + " [" + guild.short_name + "]" + guild.name);

            return true;
        } catch (Exception e) {
            player.sendMessage(MessagesController.getMessage("server_error"));
            e.printStackTrace();
            return true;
        }
    }
}
