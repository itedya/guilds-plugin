package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.MessagesController;

public class InvalidUsageException extends Exception {
    public InvalidUsageException() {
        super(MessagesController.getMessage("invalid_usage"));
    }
}
