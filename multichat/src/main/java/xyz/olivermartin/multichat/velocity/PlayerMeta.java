package xyz.olivermartin.multichat.velocity;

import java.util.UUID;

public class PlayerMeta {

    public UUID uuid;
    public String name;
    public String nick;
    public String spigotDisplayName;
    public String prefix;
    public String suffix;
    public String world;

    public PlayerMeta(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        nick = name;
        spigotDisplayName = nick;
        prefix = "";
        suffix = "";
        world = "";
    }

    public String getSpigotDisplayname() {
        return this.spigotDisplayName;
    }
}
