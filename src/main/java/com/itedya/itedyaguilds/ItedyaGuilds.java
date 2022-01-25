package com.itedya.itedyaguilds;

import com.itedya.itedyaguilds.commands.*;
import com.itedya.itedyaguilds.commands.admin.AdminDelete;
import com.itedya.itedyaguilds.commands.admin.AdminKickFromGuild;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.InvitesController;
import com.itedya.itedyaguilds.listeners.DisplayCuboidInfoListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Logger;

public final class ItedyaGuilds extends JavaPlugin {
    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // initializing database
        try {
            this.getDataFolder().mkdir();

            this.saveDefaultConfig();

            Database.connectToDatabase(this);
            Database.migrateDatabase(this);

            InvitesController.initialize(this);
            ConfigController.initialize(this);

            // Initializing cuboid boss bar
            Bukkit.getServer().getPluginManager().registerEvents(new DisplayCuboidInfoListener(), this);

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PlaceholderAPIExpansion().register();
            }

            this.logger.info("Enabled plugin");
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
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
            case "admin" -> {
                if (args.length == 0) {
                    for (String line : ConfigController.help) player.sendMessage(line);
                    yield true;
                }

                commandName = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);

                yield switch (commandName) {
                    case "usun" -> new AdminDelete(this).onCommand(player, args);
                    case "wyrzuc" -> new AdminKickFromGuild(this).onCommand(player, args);
                    default -> {
                        for (String line : ConfigController.help) player.sendMessage(line);

                        yield true;
                    }
                };
            }
            default -> {
                for (String line : ConfigController.help) player.sendMessage(line);

                yield true;
            }
        };
    }
}
