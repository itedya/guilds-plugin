package com.itedya.itedyaguilds.commands;

import com.itedya.itedyaguilds.ItedyaGuilds;
import com.itedya.itedyaguilds.commands.admin.AdminDelete;
import com.itedya.itedyaguilds.commands.admin.AdminKickFromGuild;
import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.controllers.MessagesController;
import com.itedya.itedyaguilds.exceptions.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Logger;

public class CommandHandler implements CommandExecutor {
    private final AcceptInviteToGuild acceptInviteToGuild;
    private final CreateGuild createGuild;
    private final DeleteGuild deleteGuild;
    private final ExitFromGuild exitFromGuild;
    private final InvitePlayerToGuild invitePlayerToGuild;
    private final KickOutOfGuild kickOutOfGuild;
    private final SetGuildHome setGuildHome;
    private final TeleportToGuildHome teleportToGuildHome;
    private final GuildInfo guildInfo;
    private final AdminDelete adminDelete;
    private final AdminKickFromGuild adminKickFromGuild;
    private final Logger logger;

    public CommandHandler(ItedyaGuilds plugin) {
        acceptInviteToGuild = new AcceptInviteToGuild(plugin);
        createGuild = new CreateGuild(plugin);
        deleteGuild = new DeleteGuild(plugin);
        exitFromGuild = new ExitFromGuild(plugin);
        invitePlayerToGuild = new InvitePlayerToGuild(plugin);
        kickOutOfGuild = new KickOutOfGuild(plugin);
        setGuildHome = new SetGuildHome(plugin);
        teleportToGuildHome = new TeleportToGuildHome(plugin);
        guildInfo = new GuildInfo(plugin);
        adminDelete = new AdminDelete(plugin);
        adminKickFromGuild = new AdminKickFromGuild(plugin);
        logger = plugin.getLogger();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You have to be a player to execute this command!");
            return true;
        }

        try {
            if (args.length == 0) throw new InvalidUsageException();

            String commandName = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);

            return switch (commandName) {
                case "akceptuj" -> acceptInviteToGuild.onCommand(player);
                case "stworz" -> createGuild.onCommand(player, args);
                case "usun" -> deleteGuild.onCommand(player, args);
                case "wyjdz" -> exitFromGuild.onCommand(player, command, label, args);
                case "zapros" -> invitePlayerToGuild.onCommand(player, command, label, args);
                case "wyrzuc" -> kickOutOfGuild.onCommand(player, command, label, args);
                case "ustawdom" -> setGuildHome.onCommand(player, command, label, args);
                case "dom" -> teleportToGuildHome.onCommand(player, command, label, args);
                case "info" -> guildInfo.onCommand(player, command, label, args);
                case "admin" -> {
                    if (args.length == 0) yield this.showHelp(player);

                    commandName = args[0];
                    args = Arrays.copyOfRange(args, 1, args.length);

                    yield switch (commandName) {
                        case "usun" -> adminDelete.onCommand(player, args);
                        case "wyrzuc" -> adminKickFromGuild.onCommand(player, args);
                        default -> showHelp(player);
                    };
                }
                default -> showHelp(player);
            };
        } catch (PlayerIsAlreadyInGuildException | ValidationException | NotEnoughPermissionsException | PlayerDoesNotHaveInviteToGuildException e) {
            player.sendMessage(e.getMessage());
        } catch (InvalidUsageException e) {
            player.sendMessage(e.getMessage());
            for (var line : ConfigController.help) player.sendMessage(line);
        } catch (NeededItemsException e) {
            for (var line : e.getMessage().split("\n")) player.sendMessage(line);
        } catch (Exception e) {
            player.sendMessage(MessagesController.getMessage("server_error"));
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private boolean showHelp(Player player) {
        for (String line : ConfigController.help) player.sendMessage(line);
        return true;
    }
}
