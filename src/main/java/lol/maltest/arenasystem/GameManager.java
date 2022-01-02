package lol.maltest.arenasystem;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.AffineTransform;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.map.MapSettings;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GamePlayer;
import lol.maltest.arenasystem.templates.GameplayFlags;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.IOException;
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

        playerGame.put(new GamePlayer(player.getUniqueId(), lives), gameUuid);

        game.someoneJoined(player, spectator);

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

    public GamePlayer getPlayerObject(UUID playerUuid) {
        ArrayList<UUID> players = new ArrayList<>();
        for(GamePlayer gamePlayer : playerGame.keySet()) {
            if(gamePlayer.getPlayerUuid() == playerUuid) {
                return gamePlayer;
            }
        }
        return null;
    }

    public ArrayList<UUID> getPlayers(UUID gameUuid) {
        ArrayList<UUID> players = new ArrayList<>();
        for(GamePlayer playerUuid : playerGame.keySet()) {
            if(playerGame.get(playerUuid).equals(gameUuid)) {
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