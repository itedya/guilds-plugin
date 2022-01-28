package com.itedya.itedyaguilds.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

public class Guild {
    @NotEmpty(message = "Musisz podac id")
    @Positive(message = "Id musi byc liczba dodatnia")
    private Integer id;

    @NotEmpty(message = "Musisz podac nazwe gildii")
    @Size(min = 6, max = 64, message = "Nazwa gildii musi moze miec od 6 do 64 znakow")
    private String name;

    @NotEmpty(message = "Musisz podac krotka nazwe gildii")
    @Size(min = 2, max = 6, message = "Krotka nazwa gildii musi miec od 2 do 6 znakow")
    private String shortName;

    private Integer guildHomeId;

    private Date createdAt = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getGuildHomeId() {
        return guildHomeId;
    }

    public void setGuildHomeId(Integer guildHomeId) {
        this.guildHomeId = guildHomeId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
