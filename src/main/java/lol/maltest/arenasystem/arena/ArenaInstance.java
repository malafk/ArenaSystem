package lol.maltest.arenasystem.arena;

import lol.maltest.arenasystem.map.Map;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaInstance {

    private GameType gameType;
    private Map map;
    private Location location;
    private HashMap<String, UUID> players;
    private boolean isFFA = true;

    public ArenaInstance(ArenaManager arenaManager, GameType gameType, Map map, HashMap<String, UUID> players) {
        this.gameType = gameType;
        this.map = map;
        this.location = arenaManager.register(this);
        this.players = players;

        checkFFA:
        for (String team : players.keySet()) {
            for (String otherTeam : players.keySet()) {
                if (!team.equals(otherTeam)) {
                    setFFA(false);
                    break checkFFA;
                }
            }
        }
    }

    public void end() {
        // todo - undo schematic paste
    }

    public void setFFA(boolean FFA) {
        isFFA = FFA;
    }

    public boolean isFFA() {
        return isFFA;
    }

    public GameType getGameType() {
        return gameType;
    }

    public Map getMap() {
        return map;
    }

    public Location getLocation() {
        return location;
    }

    public List<UUID> getPlayers() {
        return new ArrayList<>(players.values());
    }
}