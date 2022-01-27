package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class PlayerMustBeOwnerOfGuildException extends Exception {
    public PlayerMustBeOwnerOfGuildException() {
        super(MessagesController.getMessage("player_must_be_owner_of_guild"));
    }
}
