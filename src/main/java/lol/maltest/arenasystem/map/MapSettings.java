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
        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -15, 5, -26));
        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -41, 5, -26));
        stickFightMaps.add(stickFightOne);
    }

}
