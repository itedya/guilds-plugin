package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.models.NeededItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NeededItemsController {
    public static List<NeededItem> getNeededItems(Player player) {
        Inventory inventory = player.getInventory();

        List<NeededItem> neededItems = ConfigController.getNeededItems();

        return neededItems.stream().filter(item -> {
            boolean con = inventory.contains(item.material, item.quantity);

            return !con;
        }).toList();
    }

    public static void sendNeededItemsErrors(Player player, List<NeededItem> itemsToGet) {
        player.sendMessage(ChatColor.GRAY + "------------");
        player.sendMessage(ChatColor.RED + MessagesController.getMessage("not_enough_items_for_guild"));

        for (NeededItem item : itemsToGet) {
            player.sendMessage(ConfigController.getNeededItemError(item.material.name()));
        }

        player.sendMessage(ChatColor.GRAY + "------------");
    }

    public static void takeGuildNeededItems(Inventory inventory) {
        inventory.removeItemAnySlot(new ItemStack(Material.GOLDEN_APPLE, 16),
                new ItemStack(Material.ENDER_PEARL, 16),
                new ItemStack(Material.BOOK, 64),
                new ItemStack(Material.DIAMOND_BLOCK, 8),
                new ItemStack(Material.BROWN_MUSHROOM, 16),
                new ItemStack(Material.AMETHYST_BLOCK, 32),
                new ItemStack(Material.SLIME_BALL, 16));
    }

    public static void sendCongratsMessages(Player player) {
        player.sendMessage(ChatColor.GOLD + "------------");
        player.sendMessage(ChatColor.GREEN + "Gratulacje! Stworzyles gildie.");
        player.sendMessage("Teraz wybierz dobre miejsce i stworz cuboida wpisujac /guild claim");
        player.sendMessage(ChatColor.GOLD + "------------");
    }
}
