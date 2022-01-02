package lol.maltest.arenasystem.templates;

import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {

    UUID playerUuid;
    int lives;
//    JScoreboardTeam team;

    public GamePlayer(UUID playerUuid, int lives) {
//        this.team = team;
        this.playerUuid = playerUuid;
        this.lives = lives;
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

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    //    public JScoreboardTeam getTeam() {
//        return team;
//    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }
}
