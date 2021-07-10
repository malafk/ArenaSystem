package lol.maltest.arenasystem.map;

import org.bukkit.Location;

import java.util.*;

public class Map {

    private String id;
    private String name;
    private String schematicName;
    private HashSet<Integer> validAmountOfTeams = new HashSet<>();
    private HashMap<Integer, LinkedHashSet<Location>> spawnpoints = new LinkedHashMap<>();

    public Map(String id, String name, String schematicName) {
        this.id = id;
        this.name = name;
        this.schematicName = schematicName;
    }

    public void setValid(int teams, boolean valid) {
        if (valid) {
            validAmountOfTeams.add(teams);
        } else {
            validAmountOfTeams.remove(teams);
        }
    }

    public void addSpawnpoint(Location relativeToOrigin, int team) {
        if (!spawnpoints.containsKey(team)) spawnpoints.put(team, new LinkedHashSet<>());
        spawnpoints.get(team).add(relativeToOrigin);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public List<Integer> getValidAmountOfTeams() {
        return new ArrayList<>(validAmountOfTeams);
    }

    public boolean isValidAmountOfTeams(int teams) {
        return getValidAmountOfTeams().contains(teams);
    }

    public List<Location> getSpawnpoints(Location origin, int team) {
        List<Location> spawnpoints = new ArrayList<>();
        for (Location relative : this.spawnpoints.get(team))
            spawnpoints.add(origin.clone().add(relative));
        return spawnpoints;
    }
}