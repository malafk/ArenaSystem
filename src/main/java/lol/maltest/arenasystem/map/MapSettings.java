package lol.maltest.arenasystem.map;

import lol.maltest.arenasystem.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class MapSettings {

    public ArrayList<Map> stickFightMaps = new ArrayList<>();

    private GameManager gameManager;

    public MapSettings(GameManager gameManager) {
        this.gameManager = gameManager;
        addStickFightMaps();
    }

    private void addStickFightMaps() {
        Map mapOne = new Map("Testing Map", "stickmap");
        mapOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -15, 5, -26));
        mapOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -41, 5, -26));
        stickFightMaps.add(mapOne);
    }
}
