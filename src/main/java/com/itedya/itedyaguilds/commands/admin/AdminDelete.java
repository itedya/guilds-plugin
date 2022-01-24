package com.itedya.itedyaguilds.commands.admin;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.util.logging.Logger;

public class AdminDelete {
    private Logger logger;

    public AdminDelete(ItedyaGuilds plugin) {
        this.logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull Player player, String[] args) {
        try {
            if (!player.hasPermission("itedya-guilds.admin.delete")) {
                player.sendMessage(ConfigController.getNotEnoughPermissionsMessage());
                return true;
            }

            if (args.length != 1) {
                player.sendMessage("Musisz podac krotka nazwe gildii zeby uzyc tej komendy!");
                return true;
            }

            Guild guild = GuildsController.getGuildByShortName(args[0]);
            if (guild == null) {
                player.sendMessage(ConfigController.getGuildDoesntExist());
                return true;
            }

            GuildsController.deleteHome(guild.getHome());
            GuildsController.delete(guild);
            Database.connection.commit();
            WorldGuardController.removeGuildRegion(guild);

            player.sendMessage(ConfigController.getDeletedGuildMessage(guild.name));

            String loggerSb = "User " +
                    player.getName() + " " +
                    player.getUniqueId().toString() + " " +
                    "deleted guild " +
                    "[" + guild.short_name + "] " +
                    guild.name + " " +
                    guild.uuid.toString() + " " +
                    "using admin command";

            this.logger.info(loggerSb);
        } catch (Exception e) {
            try {
                Database.connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            player.sendMessage(ConfigController.getServerErrorMessage());
            this.logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}