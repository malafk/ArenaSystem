package lol.maltest.arenasystem.arena;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private Random random = new Random();
    private HashMap<Location, ArenaInstance> arenas = new HashMap<>();

    private double arenaYHeight = 64;
    private double distanceBetweenArenas = 1000;
    private int numberOfArenasPerAxis = 1000;

    private World world;

    public ArenaManager(World world) {
        this.world = world;
    }

    public Location register(ArenaInstance arenaInstance) {
        Location location = getFreeArenaLocation();
        arenas.put(location, arenaInstance);
        return location;
    }

    public Location getFreeArenaLocation() {
        Location location;
        long timestamp = System.currentTimeMillis();
        while (System.currentTimeMillis() - timestamp < 50) {
            location = getRandomArenaLocation();
            if (!arenas.containsKey(location))
                return location;
        }
        throw new NullPointerException("no free arena location found");
    }

    private Location getRandomArenaLocation() {
        return new Location(getWorld(),
                (random.nextBoolean() ? -1 : 1) * distanceBetweenArenas * random.nextInt(numberOfArenasPerAxis), arenaYHeight,
                (random.nextBoolean() ? -1 : 1) * distanceBetweenArenas * random.nextInt(numberOfArenasPerAxis));
    }

    public List<ArenaInstance> getArenas() {
        return new ArrayList<>(arenas.values());
    }

    public World getWorld() {
        return world;
    }
}