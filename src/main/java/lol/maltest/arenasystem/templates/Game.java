package lol.maltest.arenasystem.templates;

import lol.maltest.arenasystem.arena.ArenaInstance;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Game {


    public void setArena(ArenaInstance arena);

    public void start();
    public void end();
    public void teleportToSpawnLocations();
    public void someoneJoined(Player player, boolean spectator);

    public GameGame.GameState getGameState();
    public GameplayFlags getGameplayFlags();
    public int getMinPlayers();
    public int getMaxPlayers();

    public String getArenaSchematic();

}
