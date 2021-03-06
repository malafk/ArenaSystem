package lol.maltest.arenasystem.arena;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardOptions;
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ArenaScoreboard {

    private GameManager gameManager;

    private JGlobalMethodBasedScoreboard scoreboard;

    private List<String> lines = new ArrayList<>();

    public ArenaScoreboard(GameManager gameManager, String title) {
        this.gameManager = gameManager;
        scoreboard = new JGlobalMethodBasedScoreboard();
        scoreboard.setOptions(new JScoreboardOptions(JScoreboardTabHealthStyle.NONE, true));
        System.out.println("create scoreboard called.");
        scoreboard.setTitle("&e&l" + title);
    }

    public void waitingScoreboard(UUID gameUuid) {
        lines.add("&7&m-------------------");
        lines.add("&7Waiting for players...");
        lines.add("&7&m-------------------");
        lines.add("&7mcevents.club");
        scoreboard.setLines(lines);
    }

    public void addPlayersToScoreboard(UUID gameUuid) {
        System.out.println("adding players to teams");
        gameManager.getPlayers(gameUuid).forEach(player -> {
            Player p = Bukkit.getPlayer(player);
            scoreboard.addPlayer(p);
            int team = 0;
            List<JScoreboardTeam> teams = scoreboard.getTeams();
            if (teams.size() == 0) {
                scoreboard.createTeam("&c&lRed","&c&lR &7", ChatColor.RED);
            } else if (teams.size() == 1) {
                scoreboard.createTeam("&b&lBlue","&b&lB &7", ChatColor.AQUA);
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
        int people = 0;
        lines.clear();
        lines.add("&7&m-------------------");
        lines.add("&f&lPlayers:");
        for(UUID pUuid : gameManager.getPlayers(gameUuid)) {
            Player loopedPlayer = Bukkit.getPlayer(pUuid);
            for(JScoreboardTeam team : scoreboard.getTeams()) {
                if(team.isOnTeam(pUuid)) {
                    if(gameManager.getPlayerObject(loopedPlayer.getUniqueId()).getLives() > 0) {
                        lines.add(team.getDisplayName() + " &7" + loopedPlayer.getName() + "&f: " + gameManager.getPlayerObject(loopedPlayer.getUniqueId()).getLives());
                    }
                }
            }
        }
        lines.add("&7&m-------------------");
        lines.add("&7mcevents.club");
        scoreboard.setLines(lines);
    }

    public void updateParkourRace(UUID gameUuid) {
        lines.clear();
        lines.add("&7&m-------------------");
        lines.add("&f&lHas Completed:");
        for(UUID pUuid : gameManager.getPlayers(gameUuid)) {
            Player loopedPlayer = Bukkit.getPlayer(pUuid);
            for(JScoreboardTeam team : scoreboard.getTeams()) {
                if(team.isOnTeam(pUuid)) {
                    lines.add(team.getDisplayName() + " &7" + loopedPlayer.getName() + "&f: " + (gameManager.getPlayerObject(loopedPlayer.getUniqueId()).completed() ? "&a???" : "&cX"));
                }
            }
        }
        lines.add("&7&m-------------------");
        lines.add("&7mcevents.club");
        scoreboard.setLines(lines);
    }

    public JGlobalMethodBasedScoreboard getScoreboard() {
        return scoreboard;
    }
}
