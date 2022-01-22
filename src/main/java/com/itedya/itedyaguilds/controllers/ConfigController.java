package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.models.NeededItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigController {
    private static FileConfiguration config;
    private static JavaPlugin plugin;
    private static Logger logger;
    public static List<String> help;

    public static void initialize(JavaPlugin plugin) throws IOException, URISyntaxException {
        ConfigController.logger = plugin.getLogger();
        ConfigController.config = plugin.getConfig();
        ConfigController.plugin = plugin;

        plugin.saveDefaultConfig();

        File helpFile = new File(plugin.getDataFolder(), "help.txt");
        if (!helpFile.exists()) {
            helpFile.getParentFile().mkdirs();
            plugin.saveResource("help.txt", false);
        }

        ConfigController.help = Files.readAllLines(Paths.get(helpFile.getPath()), StandardCharsets.UTF_8)
                .stream()
                .map(item -> ChatColor.translateAlternateColorCodes('&', item)).toList();
    }

    public static void checkForErrors() {
        List<String> neededItems = ConfigController.config.getStringList("needed_items");

        if (neededItems.size() == 0) {
            ConfigController.logger.warning("Needed items is empty!");
        }

        for (String neededItem : neededItems) {
            String itemId = neededItem.split(" ")[0];
            int quantity = Integer.parseInt(neededItem.split(" ")[1]);

            if (Material.matchMaterial(itemId) == null) {
                Bukkit.getPluginManager().disablePlugin(plugin);
                throw new RuntimeException("Item " + itemId + " doesn't exist");
            }

            if (quantity <= 0) {
                throw new RuntimeException("Invalid quantity for item " + itemId);
            }
        }
    }

    public static List<NeededItem> getNeededItems() {
        return ConfigController.config.getStringList("needed_items").stream().map(item -> {
            String[] splitted = item.split(" ");

            NeededItem mappedItem = new NeededItem();
            mappedItem.material = Material.matchMaterial(splitted[0]);
            mappedItem.quantity = Integer.valueOf(splitted[1]);

            return mappedItem;
        }).collect(Collectors.toList());
    }

    public static String getNotEnoughItemsForGuild() {
        return ConfigController.config.getString("messages.not_enough_items_for_guild");
    }

    public static String getNeededItemError(String name) {
        name = name.toLowerCase();

        return ConfigController.config.getString("messages.errors." + name);
    }

    public static String getInvalidCommandMessage() {
        return ConfigController.config.getString("messages.invalid_command", ChatColor.translateAlternateColorCodes('&', "&cNiepoprawna komenda"));
    }

    public static String getNotEnoughPermissionsMessage() {
        return ConfigController.config.getString("messages.not_enough_permissions", ChatColor.translateAlternateColorCodes('&', "&cBrak permisji!"));
    }

    public static String getServerErrorMessage() {
        return ConfigController.config.getString("messages.server_error", ChatColor.translateAlternateColorCodes('&', "&cWystapil blad serwera! Skontaktuj sie z itedya, yuuki lub maksio lub sprobuj ponownie."));
    }

    public static String getInvalidUsageMessage() {
        return ConfigController.config.getString("messages.invalid_usage", ChatColor.translateAlternateColorCodes('&', "&cZle uzycie komendy, zobacz pomoc."));
    }

    public static String getYouAreAlreadyInGuildMessage() {
        return ConfigController.config.getString("messages.you_are_already_in_guild", ChatColor.translateAlternateColorCodes('&', "&eJestes juz w gildii!"));
    }

    public static String getGuildNameIsNotUniqueMessage() {
        return ConfigController.config.getString("messages.guild_name_is_not_unique", ChatColor.translateAlternateColorCodes('&', "&eGildia o takiej nazwie juz istnieje! Wybierz inna."));
    }

    public static String getGuildShortNameIsNotUniqueMessage() {
        return ConfigController.config.getString("messages.guild_short_name_is_not_unique", ChatColor.translateAlternateColorCodes('&', "&eGildia z takim skrotem juz istnieje! Wybierz inny."));
    }

    public static String getCuboidIntersectionMessage() {
        return ConfigController.config.getString("messages.cuboid_intersection", ChatColor.translateAlternateColorCodes('&', "&eJakis cuboid juz istnieje na tym terenie (cuboid jest rozmiarow 150x150)!"));
    }

    public static String getYouDontHaveInviteToGuild() {
        return ConfigController.config.getString("messages.you_dont_have_invite_to_guild", ChatColor.translateAlternateColorCodes('&', "&eNie masz zaproszenia do gildii!"));
    }

    public static String getWelcomeToGuildMessage(String name, String shortName) {
        return ConfigController.config.getString("messages.you_dont_have_invite_to_guild", ChatColor.translateAlternateColorCodes('&', "&aWitamy w gildii &7[&e{GUILD_SHORT_NAME}&7] &e{GUILD_NAME}&a!"))
                .replaceAll("\\{GUILD_NAME}", name)
                .replaceAll("\\{GUILD_SHORT_NAME}", shortName);
    }

    public static String getYouAreNotInGuildMessage() {
        return ConfigController.config.getString("messages.you_are_not_in_guild", ChatColor.translateAlternateColorCodes('&', "&eNie jestes w gildii!"));
    }

    public static String getYouHaveToBeOwnerOfGuildMessage() {
        return ConfigController.config.getString("messages.you_have_to_be_owner_of_guild", ChatColor.translateAlternateColorCodes('&', "&eMusisz byc wlasicielem gildii zeby to zrobic!"));
    }

    public static String getDeletedGuildMessage(String name) {
        return ConfigController.config.getString("messages.deleted_guild",  ChatColor.translateAlternateColorCodes('&', "&7Usunales gildie &e{GUILD_NAME}&7!"))
                .replaceAll("\\{GUILD_NAME}", name);
    }

    public static String getOwnerCanOnlyDeleteGuildMessage() {
        return ConfigController.config.getString("messages.owner_can_only_delete_guild", ChatColor.translateAlternateColorCodes('&', "&aJestes wlascicielem gildii! Mozesz co najwyzej ja usunac."));
    }

    public static String getExitFromGuildMessge(String name) {
        return ConfigController.config.getString("messages.exit_from_guild", ChatColor.translateAlternateColorCodes('&', "&7Wyszedles z gildii &a{GUILD_NAME}&7!"))
                .replaceAll("\\{GUILD_NAME}", name);
    }

    public static String getPlayerDoesntExist(String name) {
        return ConfigController.config.getString("messages.player_doesnt_exist", ChatColor.translateAlternateColorCodes('&', "&eGracz {PLAYER_NAME} nie istnieje!"))
                .replaceAll("\\{PLAYER_NAME}", name);
    }

    public static String getPlayerIsAlreadyInGuild(String name) {
        return ConfigController.config.getString("messages.player_is_already_in_guild", ChatColor.translateAlternateColorCodes('&', "&eGracz {PLAYER_NAME} jest juz w gildii!"))
                .replaceAll("\\{PLAYER_NAME}", name);
    }

    public static String getYouGotInviteMessage(String playerName, String guildName, String guildShortName) {
        return ConfigController.config.getString("messages.you_got_invite_message",
                        ChatColor.translateAlternateColorCodes('&', "&aDostales zaproszenie do gildii " +
                                "&7[&e{GUILD_SHORT_NAME}&7] &e%guild_name% &aod gracza {PLAYER_NAME}. Aby zaakceptowac zaproszenie, wpisz /g akceptuj"))
                .replaceAll("\\{GUILD_SHORT_NAME}", guildShortName)
                .replaceAll("\\{GUILD_NAME}", guildName)
                .replaceAll("\\{PLAYER_NAME}", playerName);
    }

    public static String getSentInviteMessage() {
        return ConfigController.config.getString("messages.invite_sent",
                ChatColor.translateAlternateColorCodes('&', "&aWyslano zaproszenie!"));
    }

    public static String getYouCantKickYourselfMessage() {
        return ConfigController.config.getString("messages.you_cant_kick_yourself",
                ChatColor.translateAlternateColorCodes('&', "&eNie mozesz wyrzucic sam siebie!"));
    }

    public static String getYouHaveBeenKickedOutOfGuildMessage(String guildName) {
        return ConfigController.config.getString("messages.you_have_been_kicked_out_of_guild",
                        ChatColor.translateAlternateColorCodes('&', "&eZostales wyrzucony z gildii {GUILD_NAME}!"))
                .replaceAll("\\{GUILD_NAME}", guildName);
    }

    public static String getYouKickedOutAUser(String playerName) {
        return ConfigController.config.getString("messages.you_kicked_out_a_user",
                        ChatColor.translateAlternateColorCodes('&', "&aWyrzuciles uzytkownika {PLAYER_NAME}!"))
                .replaceAll("\\{PLAYER_NAME}", playerName);
    }

    public static String getLocationIsNotInCuboid() {
        return ConfigController.config.getString("messages.location_is_not_in_cuboid",
                ChatColor.translateAlternateColorCodes('&', "&eTa lokalizacja nie jest w cuboidzie twojej gildii!"));
    }

    public static String getYouCantBeInBattle() {
        return ConfigController.config.getString("messages.you_cant_be_in_battle",
                ChatColor.translateAlternateColorCodes('&', "&eNie mozesz uzyc tej komendy podczas walki!"));
    }

    public static String getTeleportationIn(Integer seconds) {
        return ConfigController.config.getString("messages.teleportation_in",
                        ChatColor.translateAlternateColorCodes('&', "&6Teleportacja za {SECONDS} sekund..."))
                .replaceAll("\\{SECONDS}", seconds.toString());
    }

    public static String getMovedWhileTeleporting() {
        return ConfigController.config.getString("messages.moved_while_teleporting",
                ChatColor.translateAlternateColorCodes('&', "&eRuszyles sie! Teleportacja anulowana!"));
    }

    public static String getGuildDoesntExist() {
        return ConfigController.config.getString("messages.guild_doesnt_exist",
                ChatColor.translateAlternateColorCodes('&', "&eGildia nie istnieje!"));
    }
}