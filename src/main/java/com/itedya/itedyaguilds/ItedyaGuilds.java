package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.commands.*;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.listeners.DisplayCuboidInfoListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    private final Logger logger = Bukkit.getLogger();

    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();

        // initializing database
        try {
            Database.connectToDatabase(this);
            Database.migrateDatabase(this);
        } catch (SQLException e) {
            this.logger.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        InvitesController.initialize(this);
        try {
            ConfigController.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize commands
        AcceptInviteToGuild.initialize(this);
        CreateGuild.initialize(this);
        DeleteGuild.initialize(this);
        ExitFromGuild.initialize(this);
        InvitePlayerToGuild.initialize(this);
        KickOutOfGuild.initialize(this);
        SetGuildHome.initialize(this);
        TeleportToGuildHome.initialize(this);


        // Initializing cuboid boss bar
        Bukkit.getServer().getPluginManager().registerEvents(new DisplayCuboidInfoListener(), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExpansion().register();
        }

        this.logger.info("Enabled plugin");
    }

    @Override
    public void onDisable() {
        this.logger.info("Disabled plugin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You have to be a player to execute this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ConfigController.getInvalidCommandMessage());
            for (String line : ConfigController.help) player.sendMessage(line);

            return true;
        }

        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        return switch (commandName) {
            case "akceptuj" -> AcceptInviteToGuild.initialize(this).onCommand(player, command, label, args);
            case "stworz" -> CreateGuild.initialize(this).onCommand(player, command, label, args);
            case "usun" -> DeleteGuild.initialize(this).onCommand(player, command, label, args);
            case "wyjdz" -> ExitFromGuild.initialize(this).onCommand(player, command, label, args);
            case "zapros" -> InvitePlayerToGuild.initialize(this).onCommand(player, command, label, args);
            case "wyrzuc" -> KickOutOfGuild.initialize(this).onCommand(player, command, label, args);
            case "ustawdom" -> SetGuildHome.initialize(this).onCommand(player, command, label, args);
            case "dom" -> TeleportToGuildHome.initialize(this).onCommand(player, command, label, args);
            case "info" -> GuildInfo.intialize(this).onCommand(player, command, label, args);
            default -> {
                for (String line : ConfigController.help) player.sendMessage(line);

                yield true;
            }
        };
    }
}
