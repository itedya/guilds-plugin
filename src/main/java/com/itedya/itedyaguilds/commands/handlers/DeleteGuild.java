package com.itedya.itedyaguilds.commands.handlers;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.daos.*;
import com.itedya.itedyaguilds.middlewares.PlayerHasPermission;
import com.itedya.itedyaguilds.middlewares.PlayerIsInGuild;
import com.itedya.itedyaguilds.models.Guild;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class DeleteGuild implements CommandHandler {
    private final ItedyaGuilds plugin;
    private final Database database;

    public DeleteGuild(ItedyaGuilds plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player));
    }

    public void validate(Player player) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.delete");
            var guildMiddleware = new PlayerIsInGuild(plugin, database, player);
            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = permissionMiddleware.handle();

            database.getConnection().rollback();

            if (middlewareResult != null) {
                player.sendMessage(middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> deleteGuild(player));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage("Wystapil blad serwera, skontaktuj sie z administratorem.");
        }
    }

    public void deleteGuild(Player player) {
        try {
            MemberDao memberDao = new MemberDaoImplementation(database);
            GuildDao guildDao = new GuildDaoImplementation(database);
            WorldGuardDao worldGuardDao = new WorldGuardDaoImplementation(BukkitAdapter.adapt(player.getWorld()));

            memberDao.delete(player.getUniqueId().toString());
            var member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
            var guild = guildDao.getGuildById(member.getGuildId());

            guildDao.delete(member.getGuildId());

            database.getConnection().commit();

            worldGuardDao.delete("guild_" + guild.getShortName() + "_" + guild.getId().toString());

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendResult(player, guild));
        } catch (Exception e) {
            try {
                database.getConnection().rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage("Wystapil blad serwera, skontaktuj sie z administratorem.");
        }
    }

    public void sendResult(Player player, Guild guild) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7Usunales gildie [&e?&7] ?!"
                        .replace("?", guild.getShortName())
                        .replace("?", guild.getName())));

        plugin.getLogger().info("Player ? ? deleted guild ? [?] ?"
                .replace("?", player.getUniqueId().toString())
                .replace("?", player.getName())
                .replace("?", guild.getId().toString())
                .replace("?", guild.getShortName())
                .replace("?", guild.getName()));
    }

}
