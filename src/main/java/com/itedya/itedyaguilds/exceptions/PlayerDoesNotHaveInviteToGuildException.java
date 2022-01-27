package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class PlayerDoesNotHaveInviteToGuildException extends Exception {
    public PlayerDoesNotHaveInviteToGuildException() {
        super(MessagesController.getMessage("player_does_not_have_invite_to_guild"));
    }
}
