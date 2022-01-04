package lol.maltest.arenasystem.map;

import lol.maltest.arenasystem.templates.Game;
import org.bukkit.Location;

import java.util.*;


public class Map {

    private String name;
    private String schematicName;
    private HashSet<Integer> validAmountOfTeams = new HashSet<>();
    private HashSet<Location> spawnpoints = new HashSet<>();

    public Map(String name, String schematicName) {
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

    public void addSpawnpoint(Location relativeToOrigin) {
        spawnpoints.add(relativeToOrigin);
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

    public List<Location> getSpawnpoints(Location origin) {
        List<Location> spawnpoints = new ArrayList<>();
        for (Location relative : this.spawnpoints)
            spawnpoints.add(relative.clone().add(origin.getX(), origin.getY(), origin.getZ()));
//            spawnpoints.add(origin.clone().add(relative.getX(), relative.getY(), relative.getZ()));
        return spawnpoints;
    }
}