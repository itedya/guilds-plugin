PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS guilds
(
    uuid       VARCHAR PRIMARY KEY,
    name       VARCHAR NOT NULL UNIQUE,
    short_name VARCHAR NOT NULL UNIQUE,
    created_at DATETIME default CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS guild_homes (
    uuid        VARCHAR PRIMARY NOT NULL,
    x           INTEGER NOT NULL,
    y           INTEGER NOT NULL,
    z           INTEGER NOT NULL,
    created_at  DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS guild_members
(
    player_uuid VARCHAR NOT NULL UNIQUE,
    guild_uuid  VARCHAR NOT NULL,
    role        VARCHAR NOT NULL,
    created_at  DATETIME default CURRENT_TIMESTAMP,
    FOREIGN KEY (guild_uuid) REFERENCES guilds (uuid) ON DELETE CASCADE
);