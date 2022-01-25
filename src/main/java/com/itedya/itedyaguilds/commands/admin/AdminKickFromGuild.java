package com.itedya.itedyaguilds.commands.admin;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class AdminKickFromGuild {
    private Logger logger;

    public AdminKickFromGuild(ItedyaGuilds plugin) {
        this.logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, String[] args) {
        try {
            if (! player.hasPermission("itedya-guilds.admin.kick-out")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            if (args.length != 2) {
                player.sendMessage("Musisz podac krotka nazwe gildii oraz nazwe uzytkownika!");
                return true;
            }

            Guild guild = GuildsController.getGuildByShortName(args[0]);
            if (guild == null) {
                player.sendMessage("Gildia nie istnieje!");
                return true;
            }

            var members = guild.getMembers();
            var member = members.stream().filter(item -> item.player.getName().equals(args[1])).findFirst().orElse(null);

            if (member == null) {
                player.sendMessage("Ten gracz nie jest w gildii!");
                return true;
            }

            if (member.role.equals("OWNER")) {
                player.sendMessage("Nie mozesz wyrzucic wlasciciela gildii!");
                return true;
            }

            GuildsController.removeMember(member.player);
            Database.connection.commit();

            player.sendMessage("Wyrzuciles czlonka " + args[1] + " z gildii [" + guild.short_name + "] " + guild.name);
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
