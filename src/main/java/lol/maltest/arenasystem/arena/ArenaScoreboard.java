package lol.maltest.arenasystem.arena;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import lol.maltest.arenasystem.templates.Game;
import org.apache.commons.lang.CharUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ArenaScoreboard {

    private Game game;

    private JGlobalMethodBasedScoreboard scoreboard;

    private ArrayList<String> lines = new ArrayList<>();

    public ArenaScoreboard(Game game) {
        scoreboard = new JGlobalMethodBasedScoreboard();
        lines.add("&7&m-------------------");
        lines.add("&7Waiting for players...");
        lines.add("&7&m-------------------");
        scoreboard.setTitle("&e&lArena System");
        scoreboard.setLines(lines);
    }

    public void addPlayersToScoreboard() {
        game.getArenaManager().getArena(game).getPlayers().forEach(p -> scoreboard.addPlayer(Bukkit.getPlayer(p)));
    }

    public void updateLives() {
        lines.clear();
        if(game.getArenaInstance().getPlayers().size() == 2) { // idk what im doing rn
            lines.add("&7&m-------------------");
            lines.add("&f&lPlayers:");
            lines.add("&cRed: &e3");
            lines.add("&9Blue: &e3");
            lines.add("&7&m-------------------");
        }
        scoreboard.setLines(lines);
    }

    public JGlobalMethodBasedScoreboard getScoreboard() {
        return scoreboard;
    }
}
