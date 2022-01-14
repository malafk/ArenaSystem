package lol.maltest.arenasystem.map;

import lol.maltest.arenasystem.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class MapSettings {

    public ArrayList<Map> stickFightMaps = new ArrayList<>();
    public ArrayList<Map> spleefMaps = new ArrayList<>();
    public ArrayList<Map> pvpBrawlMaps = new ArrayList<>();
    public ArrayList<Map> tntRunMaps = new ArrayList<>();

    private GameManager gameManager;
    public Map stickFightOne;
    public Map spleefOne;
    public Map pvpBrawlOne;
    private Map tntRunOne;

    public MapSettings(GameManager gameManager) {
        this.gameManager = gameManager;
        addStickFightMaps();
        addSpleefMaps();
        addPvpBrawlMaps();
        addTntRunMaps();
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

    private void addSpleefMaps() {
        spleefOne = new Map("Spleef Circle", "spleef");
        spleefOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 6.5, 5, 13.5, -90, 0));
        spleefOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 21.5, 5, 13.5, 90, 0));
        spleefMaps.add(spleefOne);
    }

    public void addPvpBrawlMaps() {
        pvpBrawlOne = new Map("PvP Brawl", "pvpbrawl");
        pvpBrawlOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 0.5, 7, 19.5, 90, 0));
        pvpBrawlOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 32.5, 7, 19.5, -90, 0));
        pvpBrawlMaps.add(pvpBrawlOne);
    }

    public void addTntRunMaps() {
        tntRunOne = new Map("TnT Run Uno", "tntrun");
        tntRunOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 18.5, 4, 16.5, 90, 0));
        tntRunMaps.add(tntRunOne);
    }

}
