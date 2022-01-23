package com.itedya.itedyaguilds.validators;

import org.bukkit.ChatColor;

public class GuildShortNameValidator {
    private final Integer minLength = 2;
    private final Integer maxLength = 6;

    private String value = null;

    public GuildShortNameValidator(String val) {
        this.value = val;
    }

    public String validate() {
        if (value.length() < minLength) {
            return ChatColor.YELLOW + "Skrot gildii musi miec minimum " + minLength + " znaki/ow!";
        }

        if (value.length() > maxLength) {
            return ChatColor.YELLOW + "Skrot gildii musi miec maximum " + maxLength + " znaki/ow!";
        }

        if (! value.matches("^[a-zA-Z0-9]{" + minLength + "," + maxLength + "}$")) {
            return ChatColor.YELLOW + "Skrot gildii moze zawierac cyfry oraz duze i male litery!";
        }

        return null;
    }
}
