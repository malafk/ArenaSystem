package lol.maltest.arenasystem.templates.games.testgame;

import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameGame;
import lol.maltest.arenasystem.templates.GameplayFlags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class TestGame implements Game {

    private HashSet<Material> allowToPlace = new HashSet<>();
    private HashSet<Material> allowToBreak = new HashSet<>();


    private GameManager gameManager;
    private ArenaInstance arenaInstance;
    private Map stickFightMap;
    private UUID uuid;
    GameplayFlags thisGameFlags = new GameplayFlags();

    public TestGame(GameManager gameManager, UUID uuid) {
        this.gameManager = gameManager;
        this.uuid = uuid;
        allowToPlace.add(Material.WOOL);
        allowToBreak.add(Material.WOOL);
        thisGameFlags.blockBreakAllowed = allowToBreak;
        thisGameFlags.blockPlaceAllowed = allowToPlace;
        thisGameFlags.canBreakBlocks = true;
        thisGameFlags.canPlaceBlocks = true;
        thisGameFlags.canDamageTeamSelf = false;
        thisGameFlags.canPvP = true;
    }

    @Override
    public void setArena(ArenaInstance arena) {
        arenaInstance = arena;
    }

    @Override
    public void start() {
        teleportToSpawnLocations();

    }

    @Override
    public void end() {

    }

    @Override
    public void teleportToSpawnLocations() {
        HashMap<Location, Boolean> spawnLocations = new HashMap<>();
        for(Map map : gameManager.getMapSettings().stickFightMaps) {
            if(map.getSchematicName().equals(getArenaSchematic())) {
                map.getSpawnpoints(arenaInstance.getLocation()).forEach(loc -> spawnLocations.put(loc, false));
            }
        }
        for(UUID pUuid : gameManager.getPlayers(uuid)) {
            Player player = Bukkit.getPlayer(pUuid);
            for(Location loc : spawnLocations.keySet()) {
                for(Boolean used : spawnLocations.values()) {
                    if(!used) {
                        player.teleport(loc);
                        spawnLocations.replace(loc, true);
                    }
                }
            }
        }
    }

    @Override
    public void someoneJoined(Player player, boolean spectator) {

    }

    @Override
    public GameGame.GameState getGameState() {
        return null;
    }

    @Override
    public GameplayFlags getGameplayFlags() {
        return thisGameFlags;
    }

    @Override
    public int getMinPlayers() {
        return 2;
    }

    @Override
    public int getMaxPlayers() {
        return 4;
    }

    @Override
    public String getArenaSchematic() {
        return arenaInstance.getSchemName();
    }

}
