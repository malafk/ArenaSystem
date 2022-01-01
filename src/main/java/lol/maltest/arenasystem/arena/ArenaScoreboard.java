package lol.maltest.arenasystem.arena;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaScoreboard {

    private GameManager gameManager;

    private JGlobalMethodBasedScoreboard scoreboard;

    private ArrayList<String> lines = new ArrayList<>();

    public ArenaScoreboard(GameManager gameManager) {
        this.gameManager = gameManager;
        scoreboard = new JGlobalMethodBasedScoreboard();
        lines.add("&7&m-------------------");
        lines.add("&7Waiting for players...");
        lines.add("&7&m-------------------");
        scoreboard.setTitle("&e&lArena System");
        scoreboard.setLines(lines);
    }

    public void addPlayersToScoreboard(UUID gameUuid) {
        gameManager.getPlayers(gameUuid).forEach(player -> {
            Player p = Bukkit.getPlayer(player);
            scoreboard.addPlayer(p);
            int team = 0;
            List<JScoreboardTeam> teams = scoreboard.getTeams();
            if (teams.size() == 0) {
                scoreboard.createTeam("red","&c&lR", ChatColor.RED);
            } else if (teams.size() == 1) {
                scoreboard.createTeam("blue","&9&lB", ChatColor.BLUE);
                team = 1;
            } else {
                if (teams.get(0).getEntities().size() > teams.get(1).getEntities().size()) {
                    team = 1;
                }
            }
            scoreboard.getTeams().get(team).addPlayer(p);
        });
    }

    public void updateLives(UUID gameUuid) {
        lines.clear();
        if( gameManager.getPlayers(gameUuid).size() >= 2) { // idk what im doing rn
            lines.add("&7&m-------------------");
            lines.add("&f&lPlayers:");
            for(UUID pUuid : gameManager.getPlayers(gameUuid)) {
                Player loopedPlayer = Bukkit.getPlayer(pUuid);
                for(JScoreboardTeam team : scoreboard.getTeams()) {
                    if(team.isOnTeam(pUuid)) {
                        lines.add(team.getDisplayName() + " &7" + loopedPlayer.getName() + "&f: " + 1);
                    }
                }
            }
            lines.add("&7&m-------------------");
        }
        scoreboard.setLines(lines);
    }

    public JGlobalMethodBasedScoreboard getScoreboard() {
        return scoreboard;
    }
}
