package lol.maltest.arenasystem.map;

import lol.maltest.arenasystem.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class MapSettings {

    public ArrayList<Map> stickFightMaps = new ArrayList<>();

    private GameManager gameManager;
    public Map stickFightOne;

    public MapSettings(GameManager gameManager) {
        this.gameManager = gameManager;
        addStickFightMaps();
    }

    private void addStickFightMaps() {
        stickFightOne = new Map("Testing Map", "stickmap");
        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5, -25.5, 90, 0));
        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5, -25.5, -90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5.1, -25.5, 90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5.1, -25.5, -90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5.2, -25.5, 90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5.2, -25.5, -90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5.3, -25.5, 90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5.3, -25.5, -90, 0));
        stickFightMaps.add(stickFightOne);
    }

}
