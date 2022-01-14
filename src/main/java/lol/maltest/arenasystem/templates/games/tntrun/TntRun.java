package lol.maltest.arenasystem.templates.games.tntrun;

import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameplayFlags;
import org.bukkit.entity.Player;

public class TntRun implements Game {
    @Override
    public void setArena(ArenaInstance arena) {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public void teleportToSpawnLocations() {

    }

    @Override
    public void someoneJoined(Player player, boolean spectator) {

    }

    @Override
    public void doDeath(Player player) {

    }

    @Override
    public void doRespawn(Player player) {

    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void tryEnd() {

    }

    @Override
    public GameplayFlags getGameplayFlags() {
        return null;
    }

    @Override
    public int getDefaultLives() {
        return 1;
    }

    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 2;
    }

    @Override
    public ArenaScoreboard getScoreboard() {
        return null;
    }

    @Override
    public String getArenaSchematic() {
        return null;
    }
}
