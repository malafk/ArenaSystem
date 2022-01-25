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
    public ArrayList<Map> parkourRaceMaps = new ArrayList<>();

    private GameManager gameManager;
    public Map stickFightOne;
    public Map spleefOne;
    public Map spleefTwo;
    public Map pvpBrawlOne;
    public Map pvpBrawlTwo;
    private Map tntRunOne;
    private Map tntRunTwo;
    private Map tntRunThree;
    private Map parkourRaceOne;
    private Map parkourRaceTwo;
    private Map parkourRaceThree;

    public MapSettings(GameManager gameManager) {
        this.gameManager = gameManager;
        addStickFightMaps();
        addSpleefMaps();
        addPvpBrawlMaps();
        addTntRunMaps();
        addParkourRaceMaps();
    }

    private void addStickFightMaps() {
        stickFightOne = new Map("Testing Map", "stickmap");
        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14, 6, -25, 90, 0));
        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40, 6, -25, -90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5.1, -25.5, 90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5.1, -25.5, -90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5.2, -25.5, 90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5.2, -25.5, -90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -14.5, 5.3, -25.5, 90, 0));
//        stickFightOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -40.5, 5.3, -25.5, -90, 0));
        stickFightMaps.add(stickFightOne);
    }

    private void addSpleefMaps() {
        spleefOne = new Map("Spleef 1", "spleef");
        spleefOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 6.5, 5, 13.5, -90, 0));
        spleefOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 21.5, 5, 13.5, 90, 0));
        spleefMaps.add(spleefOne);

        spleefTwo = new Map("Spleef 2", "spleef2");
        spleefTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), -19.5, 11.5, 12.5, 0, 0));
        spleefTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), -19.5, 11.5, 28.5, 180, 0));
        spleefMaps.add(spleefTwo);
    }

    public void addPvpBrawlMaps() {
        pvpBrawlOne = new Map("PvP Brawl 1", "pvpbrawl2");
        pvpBrawlOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -15.5, -12, 9, 0, 0));
        pvpBrawlOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), -15.5, -12, 23.5, -180, 0));

        pvpBrawlTwo = new Map("PvP Brawl 2", "pvpbrawl1");
        pvpBrawlTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), 12.5, 5.5, -21.5, -90, 0));
        pvpBrawlTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), 32.5, 5.5, -21.5, 90, 0));
        pvpBrawlMaps.add(pvpBrawlTwo);
    }

    public void addTntRunMaps() {
        tntRunOne = new Map("TNT Run 1", "tntrun1");
        tntRunOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 16.5, 7, -17.5, 90, 0));
        tntRunOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 16.5, 7.2, -17.5, 90, 0));
        tntRunMaps.add(tntRunOne);

        tntRunTwo = new Map("TNT Run 2", "tntrun2");
        tntRunTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), -7, 11, 7, 90, 0));
        tntRunTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), -7, 11.2, 7, 90, 0));
        tntRunMaps.add(tntRunTwo);

        tntRunThree = new Map("TNT Run 3", "tntrun3");
        tntRunThree.addSpawnpoint(new Location(Bukkit.getWorld("void"), -23, 7.5, -21, 90, 0));
        tntRunThree.addSpawnpoint(new Location(Bukkit.getWorld("void"), -23, 7.6, -21, 90, 0));
        tntRunMaps.add(tntRunThree);
    }

    public void addParkourRaceMaps() {
        parkourRaceOne = new Map("Oak Bunnies", "parkourrace1");
        parkourRaceOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 28.5, 12.8,-19.5, -100, 0));
        parkourRaceOne.addSpawnpoint(new Location(Bukkit.getWorld("void"), 28.5, 12.7,-19.5, -100, 0));
        parkourRaceMaps.add(parkourRaceOne);

        parkourRaceTwo = new Map("Bikini Bottom", "parkourrace2");
        parkourRaceTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), -28.5, 9.8,-27.5, 150, 0));
        parkourRaceTwo.addSpawnpoint(new Location(Bukkit.getWorld("void"), -28.5, 9.7,-27.5, 150, 0));
        parkourRaceMaps.add(parkourRaceTwo);

        parkourRaceThree = new Map("Birch Bunnies", "parkourrace3");
        parkourRaceThree.addSpawnpoint(new Location(Bukkit.getWorld("void"), 23.5, 4.8,-16.5, -130, 0));
        parkourRaceThree.addSpawnpoint(new Location(Bukkit.getWorld("void"), 23.5, 4.7,-16.5, -130, 0));
        parkourRaceMaps.add(parkourRaceThree);
    }

}
