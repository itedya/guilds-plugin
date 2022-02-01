package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.commands.handlers.CommandHandler;
import com.itedya.itedyaguilds.commands.handlers.CreateGuild;
import com.itedya.itedyaguilds.commands.handlers.DeleteGuild;
import com.itedya.itedyaguilds.commands.handlers.InviteToGuild;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandsHandler implements CommandExecutor {
    private final ItedyaGuilds plugin;
    private final Database database;

    private Map<String, CommandHandler> commandHandlers;

    public CommandsHandler(ItedyaGuilds plugin, Database database) {
        this.plugin = plugin;
        this.database = database;

        initializeCommandHandlers();
    }

    private void initializeCommandHandlers() {
        this.commandHandlers = new ConcurrentHashMap<>();

        commandHandlers.put("stworz", new CreateGuild(plugin, database));
        commandHandlers.put("zapros", new InviteToGuild(plugin, database));
        commandHandlers.put("usun", new DeleteGuild(plugin, database));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You have to be in game to send commands to this plugin!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Wprowadz poprawnie komende! Zobacz /g help");
            return true;
        }

        var commandHandler = commandHandlers.get(args[0]);

        if (commandHandler == null) {
            sender.sendMessage(ChatColor.RED + "Wprowadz poprawnie komende! Zobacz /g help");
            return true;
        }

        args = Arrays.copyOfRange(args, 1, args.length);

        commandHandler.handle(player, args);

        return true;
    }
}
