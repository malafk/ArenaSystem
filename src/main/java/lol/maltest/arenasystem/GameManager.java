package lol.maltest.arenasystem;

import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.map.MapSettings;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GamePlayer;
import lol.maltest.arenasystem.util.ChatUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private ArenaSystem plugin;
    private ArenaManager arenaManager;
    private MapSettings mapSettings;


    public GameManager(ArenaSystem plugin) {
        this.plugin = plugin;
        this.arenaManager = new ArenaManager(Bukkit.getWorld("void"));
        this.mapSettings = new MapSettings(this);
    }

    private HashMap<UUID, Game> activeGames = new HashMap<>();
    private HashMap<GamePlayer, UUID> playerGame = new HashMap<>();
    // player, game

    public void addGame(UUID gameUuid, Game game) {
        // this is what happens when we want to create a new game instance

        // first of all, we need to create the arena
        // ask the game what schematic it needs us to paste

        // create an arena for the game
        ArenaInstance arena = new ArenaInstance(this, mapSettings.stickFightMaps.get(0).getSchematicName());
        game.setArena(arena);
        activeGames.put(gameUuid, game);
    }

    public void addPlayerToGame(UUID gameUuid, Player player, int lives, boolean spectator) {
        Game game = activeGames.getOrDefault(gameUuid, null);

        if (game == null) {
            Bukkit.getLogger().severe("tried to add a player to a game that doesn't exist! " + gameUuid.toString());
        }
        System.out.println(player.getName() + " got added to a game");

        playerGame.put(new GamePlayer(player.getUniqueId(), gameUuid, lives), gameUuid);

        game.someoneJoined(player, spectator);

        if(spectator) return;

        if (getPlayers(gameUuid).size() >= game.getMaxPlayers()) {
            // game is full! we should start it
            startGame(gameUuid);
        }
    }

    public void addPlayerToGame(UUID gameUuid, ArrayList<Player> player, int lives, boolean spectator) {
        Game game = activeGames.getOrDefault(gameUuid, null);

        if (game == null) {
            Bukkit.getLogger().severe("tried to add a player to a game that doesn't exist! " + gameUuid.toString());
        }


        player.forEach(p -> {
            System.out.println(p.getName() + " got added to a game");
            playerGame.put(new GamePlayer(p.getUniqueId(), gameUuid, lives), gameUuid);
            game.someoneJoined(p, spectator);
        });

        if(spectator) return;

        if (getPlayers(gameUuid).size() >= game.getMaxPlayers()) {
            // game is full! we should start it
            startGame(gameUuid);
        }
    }


    public void startGame(UUID gameUuid) {
        Game game = activeGames.getOrDefault(gameUuid, null);

        if(game == null) {
            Bukkit.getLogger().severe("Cant get that game.. probs doesnt exist");
        }


        game.start();
    }

    public void endGame(UUID uuid, boolean teams) {
        Game game = activeGames.getOrDefault(uuid, null);

        if(game == null) {
            Bukkit.getLogger().severe("Cant get that game.. probs doesnt exist");
            return;
        }
        ArrayList<String> teamWhoWon = new ArrayList<>();
        boolean noWinner = false;
        Player whoWon = null;
        if(getPlayersAlive(uuid).size() == 0) {
            noWinner = true;
        } else {
            whoWon = Bukkit.getPlayer(getPlayersAlive(uuid).get(0));
        }
        if(!noWinner) {
            getTeamsAlive(uuid).get(0).getEntities().forEach(p -> {
                teamWhoWon.add(Bukkit.getPlayer(p).getName());
            });
        }
        if(!noWinner) {
            Player finalWhoWon = whoWon;
            new BukkitRunnable() {
                int amountOfFireworks = 5;
                @Override
                public void run() {

                    int r = (int) (Math.random() * 256);
                    int g = (int) (Math.random() * 256);
                    int b = (int) (Math.random() * 256);

                    Location newLocation = finalWhoWon.getLocation().add(new Vector(Math.random() - 0.5, 0, Math.random() - 0.5).multiply(5));

                    spawnFireworks(newLocation, 1, Color.fromRGB(r, g, b));

                    amountOfFireworks--;
                    if(amountOfFireworks == 0) {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
        game.broadcastMessage("&7" + uuid);
        game.broadcastMessage("&7&m--------------------------");
        game.broadcastMessage("&6");
        game.broadcastMessage("&e&lGame Ended!");
        game.broadcastMessage("");
//        if(noWinner) {
//            game.broadcastMessage("&7The winner is: No one!");
//            game.broadcastMessage("&6");
//            game.broadcastMessage("&7Kills:");
//            HashMap<UUID, Integer> result = getKillers(uuid).entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//            int i = 1;
//            for (Map.Entry<UUID, Integer> entry : result.entrySet()) {
//                UUID key = entry.getKey();
//                Integer kills = entry.getValue();
//                Player player = Bukkit.getPlayer(key);
//                if(i == result.size()) {
//                    break;
//                }
//                if(i == 3) {
//                    game.broadcastMessage("&c1st &7 - " + player.getName() + "- " + kills);
//                }
//                if(i == 2) {
//                    game.broadcastMessage("&62nd &7 - " + player.getName() + "- " + kills);
//                }
//                if(i == 1) {
//                    game.broadcastMessage("&e3rd &7 - " + player.getName() + "- " + kills);
//                }
//                i++;
//            }
//        } else {
//
//        }
        game.broadcastMessage("&7The winning team is: " + (Objects.equals(whoWon, null) ? "No One" : whoWon.getScoreboard().getPlayerTeam(whoWon).getDisplayName()));
        if(!noWinner) {
            if(teams) {
                game.broadcastMessage("&7Winners: &e" + StringUtils.join(teamWhoWon, ", "));
            } else {
                game.broadcastMessage("&7Winners: &e" + whoWon.getName());

            }
        }
        game.broadcastMessage("&6");
        game.broadcastMessage("&7Kills:");
        LinkedHashMap<UUID, Integer> result = getKillers(uuid).entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));;
        int i = 0;
        List<UUID> keyList = new ArrayList<UUID>(result.keySet());
        Collections.reverse(keyList);
        for (UUID k : keyList) {
            UUID key = k;
            Integer kills = result.get(key);
            Player player = Bukkit.getPlayer(key);
            if(i == 0) {
                game.broadcastMessage("&c1st &7 - " + player.getName() + " - " + kills);
            }
            if(i == 1) {
                game.broadcastMessage("&62nd &7 - " + player.getName() + " - " + kills);
            }
            if(i == 2) {
                game.broadcastMessage("&e3rd &7 - " + player.getName() + " - " + kills);
            }
            if(i == result.size()) {
                break;
            }
            i++;
        }
        game.broadcastMessage("&c");
        game.broadcastMessage("&7&m--------------------------");
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers(uuid).forEach(p -> {
                    Player player = Bukkit.getPlayer(p);
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(new Location(arenaManager.getWorld(), 0, 5,0));
                    // TODO: bungee send to hub
                    game.end();
                    playerGame.remove(getPlayerObject(p));
                });
                activeGames.remove(uuid);
            }
        }.runTaskLater(plugin, 20 * 8);
    }

    public void removePlayerFromGame(GamePlayer player) {
        playerGame.remove(player);
    }

    public Game getGame(UUID uuid) {
        return activeGames.get(uuid);
    }

    public void spawnFireworks(Location location, int amount, Color color){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public GamePlayer getPlayerObject(UUID playerUuid) {
        for(GamePlayer gamePlayer : playerGame.keySet()) {
            if(gamePlayer.getPlayerUuid() == playerUuid) {
                return gamePlayer;
            }
        }
        return null;
    }

    public ArrayList<JScoreboardTeam> getTeamsAlive(UUID gameUuid) {
        ArrayList<JScoreboardTeam> aliveTeams = new ArrayList<>();
        Game game = activeGames.getOrDefault(gameUuid, null);
        for(JScoreboardTeam team : game.getScoreboard().getScoreboard().getTeams()) {
            for(UUID p : team.getEntities()) {
                if(getPlayerObject(p) != null) {
                    if(getPlayerObject(p).getLives() > 0) {
                        aliveTeams.add(team);
                        break;
                    }
                }
            }
        }
        return aliveTeams;
    }

    public ArrayList<UUID> getPlayersAlive(UUID gameUuid) {
        ArrayList<UUID> players = new ArrayList<>();
        for(GamePlayer player : playerGame.keySet()) {
            if(player.getGameUuid().equals(gameUuid)) {
                if(player.getLives() >= 1) {
                    players.add(player.getPlayerUuid());
                }
            }
        }
        return players;
    }

    public HashMap<UUID, Integer> getKillers(UUID gameUuid) {
        HashMap<UUID, Integer> killers = new HashMap<>();
        for(GamePlayer player : playerGame.keySet()) {
            if(player.getGameUuid().equals(gameUuid)) {
                if(player.getKills() > 0) {
                    killers.put(player.getPlayerUuid(), player.getKills());
                }
            }
        }
        return killers;
    }

    public int getUniqueKills(UUID gameUuid) {
        int kills = 0;
        for(GamePlayer playerUuid : playerGame.keySet()) {
            if(playerUuid.getGameUuid().equals(gameUuid)) {
                if(playerUuid.getKills() > 0) {
                    kills++;
                }
            }
        }
        return kills;
    }

    public ArrayList<UUID> getPlayers(UUID gameUuid) {
        ArrayList<UUID> players = new ArrayList<>();
        for(GamePlayer playerUuid : playerGame.keySet()) {
            if(playerUuid.getGameUuid().equals(gameUuid)) {
                players.add(playerUuid.getPlayerUuid());
            }
        }
        return players;
    }




    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public MapSettings getMapSettings() {
        return mapSettings;
    }

    public ArenaSystem getPlugin() {
        return plugin;
    }
}