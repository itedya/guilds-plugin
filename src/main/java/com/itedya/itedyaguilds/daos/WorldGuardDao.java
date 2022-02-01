package com.itedya.itedyaguilds.daos;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.entity.Player;

public interface WorldGuardDao {

    public void add(ProtectedCuboidRegion region);

    public boolean doesCuboidIntersect(ProtectedCuboidRegion cuboid);

    public void delete(String regionName);

    public void addPlayerToRegion(ProtectedCuboidRegion region, Player player);
}
