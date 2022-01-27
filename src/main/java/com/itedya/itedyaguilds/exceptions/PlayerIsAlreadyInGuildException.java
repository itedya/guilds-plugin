package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class PlayerIsAlreadyInGuildException extends Exception {
    public PlayerIsAlreadyInGuildException() {
        super(MessagesController.getMessage("player_is_already_in_guild"));
    }
}
