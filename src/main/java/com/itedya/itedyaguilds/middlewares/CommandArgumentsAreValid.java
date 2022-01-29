package com.itedya.itedyaguilds.middlewares;

import com.itedya.itedyaguilds.dtos.Dto;

public class CommandArgumentsAreValid extends AbstractHandler {
    private final Dto dto;

    public CommandArgumentsAreValid(Dto dto) {
        this.dto = dto;
    }

    @Override
    public String handle() {
        var res = dto.validate();
        if (res != null) return res;

        return super.handle();
    }
}
