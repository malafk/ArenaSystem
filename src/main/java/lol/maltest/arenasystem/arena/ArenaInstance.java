package lol.maltest.arenasystem.arena;

import org.bukkit.Location;

public class ArenaInstance {

    private Location location;

    public ArenaInstance(ArenaManager arenaManager) {
        location = arenaManager.register(this);
    }
}