package lol.maltest.arenasystem.templates;

import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {

    UUID playerUuid;
    UUID gameUuid;
    int lives;
    int kills;
    boolean spectator;
//    JScoreboardTeam team;

    public GamePlayer(UUID playerUuid, UUID gameUuid, int lives) {
//        this.team = team;
        this.gameUuid = gameUuid;
        this.playerUuid = playerUuid;
        this.lives = lives;
        this.kills = 0;
    }

    public int getLives() {
        return lives;
    }

    public void takeLive(int amount) {
        lives -= amount;
    }

    public void giveLive(int amount) {
        lives += amount;
    }

    public void setLives(int amount) {
        lives = amount;
    }

    public void addKill(int amount) {
        kills += amount;
    }

    public int getKills() {
        return kills;
    }

    public void setSpectator(boolean yes) {
        spectator = yes;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

    //    public JScoreboardTeam getTeam() {
//        return team;
//    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }
}
