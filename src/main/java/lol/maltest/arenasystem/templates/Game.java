package lol.maltest.arenasystem.templates;

import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Game {


    void setArena(ArenaInstance arena);

    void start();
    void end();
    void someoneJoined(Player player, boolean spectator);
    void doDeath(Player player);
    void doRespawn(Player player);
    void broadcastMessage(String message);
    void tryEnd();

    GameplayFlags getGameplayFlags();
    int getDefaultLives();
    int getMinPlayers();
    int getMaxPlayers();

    ArenaScoreboard getScoreboard();

    String getArenaSchematic();

}
