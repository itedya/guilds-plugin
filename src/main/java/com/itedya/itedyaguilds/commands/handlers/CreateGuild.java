package com.itedya.itedyaguilds.commands.handlers;

import com.itedya.itedyaguilds.Database;
import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.dtos.CreateGuildDto;
import com.itedya.itedyaguilds.middlewares.CommandArgumentsAreValid;
import com.itedya.itedyaguilds.middlewares.PlayerHasPermission;
import com.itedya.itedyaguilds.middlewares.PlayerIsNotInGuild;
import org.bukkit.entity.Player;

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


    }
}
