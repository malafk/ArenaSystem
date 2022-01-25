package lol.maltest.arenasystem.templates.games.parkourrace;

import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.games.spleef.Spleef;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.templates.games.tntrun.TntRun;
import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class ParkourRace implements Game, Listener {

    public enum GameState {

        STARTING, ACTIVE, WON
    }

    public GameState gameState = GameState.STARTING;

    HashMap<UUID, Location> spawnLocations = new HashMap<>();
    HashMap<Location, Boolean> spawnLocationsStart = new HashMap<>();

    private GameManager gameManager;
    private ArenaInstance arenaInstance;
    private ArenaScoreboard arenaScoreboard;
    private UUID uuid;

    GameplayFlags thisGameFlags = new GameplayFlags();
    Random random;
    Map map;

    public ParkourRace(GameManager gameManager, UUID uuid) {
        random = new Random();
        this.gameManager = gameManager;
        this.uuid = uuid;
        this.arenaScoreboard = new ArenaScoreboard(gameManager, "Parkour Race");
        Bukkit.getPluginManager().registerEvents(this, gameManager.getPlugin());

        thisGameFlags.canBreakBlocks = false;
        thisGameFlags.canPlaceBlocks = false;
        thisGameFlags.canDamageTeamSelf = false;
        thisGameFlags.canPvP = false;
    }

    @Override
    public void setArena(ArenaInstance arena) {
        arenaInstance = arena;
        map = gameManager.getMapSettings().parkourRaceMaps.get(random.nextInt(gameManager.getMapSettings().parkourRaceMaps.size()));
        arenaInstance.setSchemName(map.getSchematicName());
    }

    @Override
    public void start() {
        gameState = GameState.ACTIVE;
        arenaScoreboard.addPlayersToScoreboard(uuid);
        arenaScoreboard.updateParkourRace(uuid);
        gameManager.teleportToSpawnLocations(map, arenaInstance, arenaScoreboard, null, uuid, spawnLocations, spawnLocationsStart);
    }

    @Override
    public void end() {
        arenaScoreboard.getScoreboard().getTeams().clear();
        arenaScoreboard.getScoreboard().destroy();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void someoneJoined(Player player, boolean spectator) {
        gameManager.onJoin(player, spectator, uuid);
    }

    @Override
    public void doDeath(Player player) {
        doRespawn(player);
    }

    @Override
    public void doRespawn(Player player) {
        player.teleport(spawnLocations.get(player.getUniqueId()));
    }

    @Override
    public void broadcastMessage(String message) {
        gameManager.broadcastMessage(message, uuid);
    }

    @Override
    public void tryEnd() {
        if(gameState == GameState.WON) return;
        if(gameManager.getPlayers(uuid).size() >= 2) {
            if(gameManager.getTeamsCompleted(uuid).size() == 1) {
                setGameState(GameState.WON);
                gameManager.endGame(uuid, true, true);
                return;
            }
        }
        if(gameManager.getPlayersAlive(uuid).size() <= 1) {
            setGameState(GameState.WON);
            gameManager.endGame(uuid, false, true);
        }
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public void setCompleted(Player player) {
        gameManager.getPlayerObject(player.getUniqueId()).setCompleted(true);
        arenaScoreboard.updateParkourRace(uuid);
        broadcastMessage("&e" + player.getName() + " &7has completed the parkour!");
        gameManager.toggleSpectator(player, true);
        tryEnd();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!gameManager.getPlayers(uuid).contains(e.getDamager().getUniqueId())) return;
        if(!getGameplayFlags().canPvP) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(!gameManager.getPlayers(uuid).contains(e.getPlayer().getUniqueId())) return;
        if(!getGameplayFlags().canPlaceBlocks) e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(!gameManager.getPlayers(uuid).contains(e.getPlayer().getUniqueId())) return;
        if(!getGameplayFlags().canPlaceBlocks) e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerInteractEvent e) {
        if(!gameManager.getPlayers(uuid).contains(e.getPlayer().getUniqueId())) return;
        if(gameManager.isSpec(e.getPlayer())) return;

        if (e.getAction() != Action.PHYSICAL) {
            return;
        }
        if (e.getClickedBlock().getType() != Material.GOLD_PLATE) {
            return;
        }

        if(gameManager.getPlayerObject(e.getPlayer().getUniqueId()) != null) {
            setCompleted(e.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!gameManager.getPlayers(uuid).contains(e.getPlayer().getUniqueId())) return;
        if(gameManager.isSpec(e.getPlayer())) return;
        if(e.getPlayer().getLocation().getY() <= 20) {
            doDeath(e.getPlayer());
        }
    }


    @Override
    public GameplayFlags getGameplayFlags() {
        return thisGameFlags;
    }

    @Override
    public int getDefaultLives() {
        return 1;
    }

    @Override
    public int getMinPlayers() {
        return 1;
    }

    @Override
    public int getMaxPlayers() {
        return 2;
    }

    @Override
    public ArenaScoreboard getScoreboard() {
        return arenaScoreboard;
    }

    @Override
    public String getArenaSchematic() {
        return arenaInstance.getSchemName();
    }
}
