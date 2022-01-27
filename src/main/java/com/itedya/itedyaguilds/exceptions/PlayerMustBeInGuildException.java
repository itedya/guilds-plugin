package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class PlayerMustBeInGuildException extends Exception {
    public PlayerMustBeInGuildException() {
        super(MessagesController.getMessage("player_must_be_in_guild"));
    }
}
