package com.itedya.itedyaguilds.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CreateGuildDto extends Dto {

    @NotEmpty(message = "Musisz podac nazwe gildii!")
    @Size(min = 6, max = 64, message = "Nazwa gildii musi zawierac od 6 do 64 znakow!")
    private String name;

    @NotEmpty(message = "Musisz podac krotka nazwe gildii!")
    @Size(min = 2, max = 6, message = "Nazwa gildii musi zawierac od 2 do 6 znakow!")
    private String shortName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public static CreateGuildDto fromCommandArgs(String[] args) {
        var dto = new CreateGuildDto();

        if (0 < args.length) {
            dto.setName(args[0]);
        }

        if (1 < args.length) {
            dto.setShortName(args[1]);
        }

        return dto;
    }
}
