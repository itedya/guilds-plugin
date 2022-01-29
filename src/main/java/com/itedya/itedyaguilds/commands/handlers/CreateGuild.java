package com.itedya.itedyaguilds.commands.handlers;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.daos.*;
import com.itedya.itedyaguilds.dtos.CreateGuildDto;
import com.itedya.itedyaguilds.enums.MemberRole;
import com.itedya.itedyaguilds.middlewares.CommandArgumentsAreValid;
import com.itedya.itedyaguilds.middlewares.PlayerHasPermission;
import com.itedya.itedyaguilds.middlewares.PlayerIsNotInGuild;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.logging.Level;

public class CreateGuild implements CommandHandler {
    private final ItedyaGuilds plugin;
    private final Database database;

    public CreateGuild(ItedyaGuilds plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void handle(Player player, String[] args) {
        CreateGuildDto dto = CreateGuildDto.fromCommandArgs(args);

        String middlewareResult = new PlayerHasPermission(player, "itedya-guilds.create")
                .setNext(new PlayerIsNotInGuild(plugin, database, player))
                .setNext(new CommandArgumentsAreValid(dto))
                .handle();

        if (middlewareResult != null) {
            player.sendMessage(middlewareResult);
            return;
        }

        GuildHomeDao guildHomeDao = new GuildHomeDaoImplementation(database);
        GuildDao guildDao = new GuildDaoImplementation(database);
        MemberDao memberDao = new MemberDaoImplementation(database);

        var playerLocation = player.getLocation();

        var gh = new GuildHome();
        gh.setX((int) playerLocation.getX());
        gh.setY((int) playerLocation.getY());
        gh.setZ((int) playerLocation.getZ());

        try {
            gh = guildHomeDao.add(gh);

            var guild = new Guild();
            guild.setName(dto.getName());
            guild.setShortName(dto.getShortName());
            guild.setGuildHomeId(gh.getId());

            try {
                guild = guildDao.add(guild);
            } catch (SQLException e) {
                var msg = e.getMessage().toLowerCase();

                if (msg.contains("unique")) {
                    if (msg.contains("short_name")) {
                        player.sendMessage(ChatColor.YELLOW + "Ta krotka nazwa jest juz zajeta!");
                    } else if (msg.contains("name")) {
                        player.sendMessage(ChatColor.YELLOW + "Ta nazwa jest juz zajeta!");
                    }
                }

                plugin.getLogger().log(Level.SEVERE, "Server error!", e);
                player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");
                return;
            }

            var member = new Member();
            member.setPlayerUuid(player.getUniqueId());
            member.setGuildId(guild.getId());
            member.setRole(MemberRole.OWNER);

            memberDao.add(member);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "Stworzyles gildie &7[&e?&7] ?!"
                            .replace("?", guild.getShortName())
                            .replace("?", guild.getName())));

            plugin.getLogger().info("Player ? ? created guild ? [?] ?"
                    .replace("?", player.getUniqueId().toString())
                    .replace("?", player.getName())
                    .replace("?", guild.getId().toString())
                    .replace("?", guild.getName())
                    .replace("?", guild.getShortName()));
        } catch (SQLException ex) {
            try {
                database.getConnection().rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            plugin.getLogger().log(Level.SEVERE, "Server error!", ex);
            player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");
        }
    }
}
