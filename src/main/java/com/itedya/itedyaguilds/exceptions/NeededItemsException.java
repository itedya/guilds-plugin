package com.itedya.itedyaguilds.exceptions;

import com.itedya.itedyaguilds.controllers.ConfigController;
import com.itedya.itedyaguilds.models.NeededItem;

import java.util.List;
import java.util.stream.Collectors;

public class NeededItemsException extends Exception {
    private final List<NeededItem> neededItems;

    public NeededItemsException(List<NeededItem> neededItems) {
        this.neededItems = neededItems;
    }

    @Override
    public String getMessage() {
        return neededItems
                .stream()
                .map(item -> ConfigController.getNeededItemError(item.material.toString().toLowerCase()))
                .collect(Collectors.joining("\n"));
    }
}
