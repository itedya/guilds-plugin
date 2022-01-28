package com.itedya.itedyaguilds.middlewares;

import org.bukkit.entity.Player;

public class PlayerHasPermission extends AbstractHandler {
    private final Player player;
    private final String permission;

    public PlayerHasPermission(Player player, String permission) {
        this.player = player;
        this.permission = permission;
    }

    @Override
    public String handle() {
        if (player.hasPermission(permission)) {
            return super.handle();
        }

        return "Brak permisji!";
    }
}
