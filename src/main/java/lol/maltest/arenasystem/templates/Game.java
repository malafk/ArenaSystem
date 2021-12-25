package lol.maltest.arenasystem.templates;

import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public class Game implements Listener {

    public enum GameState {
        STARTING,
        ACTIVE,
        WON,
    }

    private ArenaManager arenaManager;
    private UUID gameUuid;

    private int countdown = -1;
    private boolean prepareCountdown = false;

    // players
    private int maxPlayers;
    private int minPlayers;
    private String[] schematics;

    // Gameplay Flags

    public boolean canPvP = true;
    public boolean canDamageTeamSelf = false;
    public boolean canDamageTeamMates = false;

    public boolean canBreakBlocks = true;
    public boolean canPlaceBlocks = true;

    public HashSet<Integer> blockBreakAllowed = new HashSet<Integer>();
    public HashSet<Integer> blockPlaceAllowed = new HashSet<Integer>();

    // Others

    public boolean teamArmor = true;

    // Gameplay Data

    public String winner = "No One";
    // TODO: Teams

//    private
    private ArenaScoreboard arenaScoreboard;
    private Kit kit;

    public Game(ArenaManager arenaManager, int minPlayers, int maxPlayers, String[] schematics, Kit kit) {
        this.gameUuid = UUID.randomUUID();
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.schematics = schematics;
        this.arenaManager = arenaManager;
        this.arenaScoreboard = new ArenaScoreboard(this);
        this.kit = kit;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

    public ArenaInstance getArenaInstance() {
        return arenaManager.getArena(this);
    }

}
