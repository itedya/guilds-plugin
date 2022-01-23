package com.itedya.itedyaguilds.validators;

import org.bukkit.ChatColor;

public class GuildNameValidator {
    private final Integer minLength = 6;
    private final Integer maxLength = 32;

    private String value = null;

    public GuildNameValidator(String val) {
        this.value = val;
    }

    public String validate() {
        if (value.length() < minLength) {
            return ChatColor.YELLOW + "Nazwa gildii musi miec minimum " + minLength + " znaki/ow!";
        }

        if (value.length() > maxLength) {
            return ChatColor.YELLOW + "Nazwa gildii musi miec maximum " + maxLength + " znaki/ow!";
        }

        if (! value.matches("^[a-zA-Z0-9]{" + minLength + "," + maxLength + "}$")) {
            return ChatColor.YELLOW + "Nazwa gildii moze zawierac cyfry oraz duze i male litery!";
        }

        return null;
    }
}
