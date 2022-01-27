package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class PlayerCantBeOwnerOfGuildException extends Exception {
    public PlayerCantBeOwnerOfGuildException() {
        super(MessagesController.getMessage("player_can_not_be_owner_of_guild"));
    }
}
