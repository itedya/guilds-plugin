PRAGMA foreign_keys = ON;

CREATE DATABASE IF NOT EXISTS itedya_guilds;

CREATE TABLE IF NOT EXISTS guild_homes
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    x          INTEGER NOT NULL,
    y          INTEGER NOT NULL,
    z          INTEGER NOT NULL,
    created_at DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS guilds
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          VARCHAR NOT NULL UNIQUE,
    short_name    VARCHAR NOT NULL UNIQUE,
    guild_home_id VARCHAR NOT NULL UNIQUE,
    created_at    DATETIME default CURRENT_TIMESTAMP,
    FOREIGN KEY (guild_home_id) REFERENCES guild_homes (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS guild_members
(
    player_uuid VARCHAR NOT NULL UNIQUE,
    guild_id    INTEGER NOT NULL,
    role        VARCHAR NOT NULL,
    created_at  DATETIME default CURRENT_TIMESTAMP,
    FOREIGN KEY (guild_id) REFERENCES guilds (id) ON DELETE CASCADE
);