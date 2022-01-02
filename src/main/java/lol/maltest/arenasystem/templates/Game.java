package lol.maltest.arenasystem.templates;

import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Game {


    public void setArena(ArenaInstance arena);

    public void start();
    public void end();
    public void teleportToSpawnLocations();
    public void someoneJoined(Player player, boolean spectator);
    public void doDeath(Player player);
    public void doRespawn(Player player);
    public void broadcastMessage(String message);

    public GameGame.GameState getGameState();
    public GameplayFlags getGameplayFlags();
    public int getDefaultLives();
    public int getMinPlayers();
    public int getMaxPlayers();

    public ArenaScoreboard getScoreboard();

    public String getArenaSchematic();

}
