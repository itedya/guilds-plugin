package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class NotEnoughPermissionsException extends Exception {
    public NotEnoughPermissionsException() {
        super(MessagesController.getMessage("not_enough_permissions"));
    }
}
