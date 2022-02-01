package com.itedya.itedyaguilds.commands.handlers;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.daos.*;
import com.itedya.itedyaguilds.dtos.CreateGuildDto;
import com.itedya.itedyaguilds.enums.MemberRole;
import com.itedya.itedyaguilds.middlewares.CommandArgumentsAreValid;
import com.itedya.itedyaguilds.middlewares.PlayerHasPermission;
import com.itedya.itedyaguilds.middlewares.PlayerIsInWorld;
import com.itedya.itedyaguilds.middlewares.PlayerIsNotInGuild;
import com.itedya.itedyaguilds.models.Guild;
import com.itedya.itedyaguilds.models.GuildHome;
import com.itedya.itedyaguilds.models.Member;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            CreateGuildDto dto = CreateGuildDto.fromCommandArgs(args);
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.create");
            var guildMiddleware = new PlayerIsNotInGuild(plugin, database, player);
            var worldMiddleware = new PlayerIsInWorld(player, Bukkit.getWorld("world"));
            var commandMiddleware = new CommandArgumentsAreValid(dto);

            worldMiddleware.setNext(commandMiddleware);
            guildMiddleware.setNext(worldMiddleware);
            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = permissionMiddleware.handle();

            database.getConnection().rollback();

            if (middlewareResult != null) {
                player.sendMessage(middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> createGuild(player, dto));
        } catch (Exception e) {
            try {
                database.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");
        }
    }

    public void createGuild(Player player, CreateGuildDto dto) {
        try {
            WorldGuardDao worldGuardDao = new WorldGuardDaoImplementation(BukkitAdapter.adapt(player.getWorld()));

            GuildHomeDao guildHomeDao = new GuildHomeDaoImplementation(database);
            GuildDao guildDao = new GuildDaoImplementation(database);
            MemberDao memberDao = new MemberDaoImplementation(database);

            var playerLocation = player.getLocation();

            var gh = new GuildHome();
            gh.setX((int) playerLocation.getX());
            gh.setY((int) playerLocation.getY());
            gh.setZ((int) playerLocation.getZ());

            gh = guildHomeDao.add(gh);

            var guild = new Guild();
            guild.setName(dto.getName());
            guild.setShortName(dto.getShortName());
            guild.setGuildHomeId(gh.getId());

            guild = guildDao.add(guild);

            var member = new Member();
            member.setPlayerUuid(player.getUniqueId());
            member.setGuildId(guild.getId());
            member.setRole(MemberRole.OWNER);

            memberDao.add(member);

            var position = player.getLocation();

            var region = new ProtectedCuboidRegion(
                    "guild_" + guild.getShortName() + "_" + guild.getId().toString(),
                    BlockVector3.at(position.getX() + 50, -500, position.getZ() + 50),
                    BlockVector3.at(position.getX() - 50, 500, position.getZ() - 50)
            );

            if (worldGuardDao.doesCuboidIntersect(region)) {
                database.getConnection().rollback();
                player.sendMessage(ChatColor.YELLOW + "Jakis cuboid juz istnieje na tym terenie (cuboid jest rozmiarow 100x100)");
                return;
            }

            worldGuardDao.add(region);

            worldGuardDao.addPlayerToRegion(region, player);

            database.getConnection().commit();

            Guild finalGuild = guild;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendResult(player, finalGuild));

        } catch (SQLException e) {
            try {
                database.getConnection().rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }

            var msg = e.getMessage().toLowerCase();

            if (msg.contains("unique")) {
                if (msg.contains("short_name")) {
                    player.sendMessage(ChatColor.YELLOW + "Ta krotka nazwa jest juz zajeta!");
                    return;
                } else if (msg.contains("name")) {
                    player.sendMessage(ChatColor.YELLOW + "Ta nazwa jest juz zajeta!");
                    return;
                }
            }

            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");
        } catch (Exception ex) {
            try {
                database.getConnection().rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            plugin.getLogger().log(Level.SEVERE, "Server error!", ex);
            player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");
        }
    }

    public void sendResult(Player player, Guild guild) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7Stworzyles gildie &7[&e?&7] ?!"
                        .replace("?", guild.getShortName())
                        .replace("?", guild.getName())));

        plugin.getLogger().info("Player ? ? created guild ? [?] ?"
                .replace("?", player.getUniqueId().toString())
                .replace("?", player.getName())
                .replace("?", guild.getId().toString())
                .replace("?", guild.getName())
                .replace("?", guild.getShortName()));
    }
}
