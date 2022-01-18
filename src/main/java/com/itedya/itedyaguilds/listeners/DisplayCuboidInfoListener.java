package com.itedya.itedyaguilds.listeners;

import com.itedya.itedyaguilds.controllers.GuildsController;
import com.itedya.itedyaguilds.controllers.WorldGuardController;
import com.itedya.itedyaguilds.models.Guild;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DisplayCuboidInfoListener implements Listener {
    private static final Logger logger = Bukkit.getLogger();

    private final Map<String, BossBar> bossBars = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) throws SQLException, ParseException {
        Player player = e.getPlayer();

        BossBar bossBar = this.bossBars.get(player.getUniqueId().toString());

        ProtectedRegion rg = WorldGuardController.getGuildRegionThatPlayerIsIn(player);

        if (rg == null) {
            if (bossBar != null) {
                bossBar.removeAll();
                this.bossBars.remove(player.getUniqueId().toString());
            }

            return;
        }

        String uuid = rg.getId().split("_")[2];

        Guild guild = GuildsController.getGuildByUUID(uuid);
        if (guild == null) {
            logger.warning("[ItedyaGuilds] Weird thing? World guard controller returns guild UUID " + rg.getId() +
                    ", but Guilds controller reports that it doesn't exist.");
            return;
        }

        if (bossBar == null) {
            String title = ChatColor.DARK_PURPLE + "Cuboid gildii " + ChatColor.YELLOW + guild.short_name;

            bossBar = Bukkit.getServer().createBossBar(title, BarColor.PURPLE, BarStyle.SOLID);

            bossBar.addPlayer(player);
            bossBar.setVisible(true);
            bossBars.put(player.getUniqueId().toString(), bossBar);
        }
    }
}
