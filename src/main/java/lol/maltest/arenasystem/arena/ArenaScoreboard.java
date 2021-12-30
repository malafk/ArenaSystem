package lol.maltest.arenasystem.arena;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.templates.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        game.getArenaManager().getArena(game).getPlayers().forEach(player -> {
            Player p = Bukkit.getPlayer(player);
            scoreboard.addPlayer(p);
            int team = 0;
            List<JScoreboardTeam> teams = scoreboard.getTeams();
            if (teams.size() == 0) {
                scoreboard.createTeam("red","Red", ChatColor.RED);
            }
            else if (teams.size() == 1) {
                scoreboard.createTeam("blue","Blue", ChatColor.BLUE);
                team = 1;
            }
            else {
                if (teams.get(0).getEntities().size() > teams.get(1).getEntities().size()) {
                    team = 1;
                }
            }
            scoreboard.getTeams().get(team).addPlayer(p);
        });
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
