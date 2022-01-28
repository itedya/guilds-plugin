package com.itedya.itedyaguilds.middlewares;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.daos.MemberDao;
import com.itedya.itedyaguilds.daos.MemberDaoImplementation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerIsInGuild extends AbstractHandler {
    private final Database database;
    private final Player player;
    private final ItedyaGuilds plugin;

    public PlayerIsInGuild(ItedyaGuilds plugin, Database database, Player player) {
        this.database = database;
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public String handle() {
        MemberDao memberDao = new MemberDaoImplementation(database);

        try {
            var member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
            if (member != null) {
                return super.handle();
            }

            return ChatColor.translateAlternateColorCodes('&', "&cNie jestes w gildii!");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            return ChatColor.translateAlternateColorCodes('&', "&cWystapil blad serwera. Sprobuj ponownie lub skontaktuj sie z administratorem.");
        }
    }
}
