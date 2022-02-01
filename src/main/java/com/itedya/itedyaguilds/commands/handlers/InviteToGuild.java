package com.itedya.itedyaguilds.commands.handlers;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.middlewares.PlayerHasPermission;
import com.itedya.itedyaguilds.middlewares.PlayerIsInGuild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class InviteToGuild implements CommandHandler {
    private final ItedyaGuilds plugin;
    private final Database database;

    public InviteToGuild(ItedyaGuilds plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.invite");
            var playerIsInGuild = new PlayerIsInGuild(plugin, database, player);
            permissionMiddleware.setNext(playerIsInGuild);

            var middlewareResult = permissionMiddleware.handle();

            database.getConnection().commit();

            if (middlewareResult != null) {
                player.sendMessage(middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> invite(player));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage("Wystapil blad serwera, skontaktuj sie z administratorem.");
        }
    }

    public void invite(Player player) {

    }
}
