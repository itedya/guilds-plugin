package com.itedya.itedyaguilds.controllers;

import com.itedya.itedyaguilds.exception.IntersectionRegionsException;
import com.itedya.itedyaguilds.models.Guild;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldGuardController {
    private static RegionContainer getRegionContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public static RegionManager getRegionManager() {
        var container = WorldGuardController.getRegionContainer();

        org.bukkit.World bukkitWorld = Bukkit.getServer().getWorld("world");
        assert bukkitWorld != null : "Bukkit world is null";

        World world = BukkitAdapter.adapt(bukkitWorld);

        RegionManager manager = container.get(world);
        assert manager != null : "Region manager is null";

        return manager;
    }

    private static ProtectedCuboidRegion createCuboid(String regionId, BlockVector3 origin) {
        BlockVector3 min = origin.subtract(75, 0, 75).withY(-500);
        BlockVector3 max = origin.add(75, 0, 75).withY(500);
        return new ProtectedCuboidRegion(regionId, min, max);
    }

    public static void createGuildRegion(Location location, Guild guild) throws IntersectionRegionsException {
        var manager = WorldGuardController.getRegionManager();

        String regionId = "guild_" + guild.short_name + "_" + guild.uuid;
        ProtectedCuboidRegion region = WorldGuardController.createCuboid(regionId,
                BlockVector3.at(location.getX(), location.getY(), location.getZ()));

        List<ProtectedRegion> regions = new ArrayList<>(manager.getRegions().values());

        var interesctingRegions = region.getIntersectingRegions(regions);

        if (interesctingRegions.size() != 0) {
            throw new IntersectionRegionsException();
        }

        manager.addRegion(region);

        region.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
    }

    public static void addPlayerToGuildCuboid(Player player, Guild guild) {
        String regionId = "guild_" + guild.short_name + "_" + guild.uuid;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg addmember -w world " + regionId + " " + player.getName());
    }

    public static void removePlayerFromGuildCuboid(Player player, Guild guild) {
        String regionId = "guild_" + guild.short_name + "_" + guild.uuid;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removemember -w world " + regionId + " " + player.getName());
    }

    public static void removeGuildRegion(Guild guild) {
        var manager = WorldGuardController.getRegionManager();

        String regionId = "guild_" + guild.short_name + "_" + guild.uuid;

        manager.removeRegion(regionId);
    }

    public static ProtectedRegion getGuildRegionThatPlayerIsIn(Player player) {
        Pattern compiledPattern = Pattern.compile("guild_[a-zA-Z0-9]*_[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}");

        var manager = WorldGuardController.getRegionManager();

        Location loc = player.getLocation();

        for (ProtectedRegion rg : manager.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))) {
            Matcher matcher = compiledPattern.matcher(rg.getId());

            if (matcher.find()) {
                return rg;
            }
        }

        return null;
    }
}
