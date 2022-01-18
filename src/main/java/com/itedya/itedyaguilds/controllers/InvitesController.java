package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.models.Guild;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvitesController {
    private static JavaPlugin plugin;
    private static Map<UUID, UUID> invites = new HashMap<>();

    public static void initialize(JavaPlugin plugin) {
        InvitesController.plugin = plugin;
    }

    public static Guild getGuildThatInvitesPlayer(Player player) throws SQLException, ParseException {
        UUID guildUUID = InvitesController.invites.get(player.getUniqueId());
        if (guildUUID == null) return null;

        return GuildsController.getGuildByUUID(guildUUID.toString());
    }

    public static void addGuildInvite(Player player, Guild guild) {
        InvitesController.invites.put(player.getUniqueId(), guild.uuid);

        new BukkitRunnable() {
            @Override
            public void run() {
                InvitesController.invites.remove(player.getUniqueId());
            }
        }.runTaskLater(InvitesController.plugin, 20 * 60);
    }
}
