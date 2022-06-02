package lol.maltest.arenasystem.templates.games.tntrun;

import com.connorlinfoot.titleapi.TitleAPI;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.games.pvpbrawl.PvPBrawl;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class TntRun implements Game, Listener {

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

    private boolean tntRunEnabled = false;

    public TntRun(GameManager gameManager, UUID uuid) {
        random = new Random();
        this.gameManager = gameManager;
        this.uuid = uuid;
        this.arenaScoreboard = new ArenaScoreboard(gameManager, "TNT Run Fight");
        Bukkit.getPluginManager().registerEvents(this, gameManager.getPlugin());

        thisGameFlags.canBreakBlocks = false;
        thisGameFlags.canPlaceBlocks = false;
        thisGameFlags.canDamageTeamSelf = false;
        thisGameFlags.canPvP = false;
    }


    @Override
    public void setArena(ArenaInstance arena) {
        arenaInstance = arena;
        map = gameManager.getMapSettings().tntRunMaps.get(random.nextInt(gameManager.getMapSettings().tntRunMaps.size()));
        arenaInstance.setSchemName(map.getSchematicName());
    }

    @Override
    public void start() {
        gameState = GameState.ACTIVE;
        arenaScoreboard.addPlayersToScoreboard(uuid);
        System.out.println("start called");
        arenaScoreboard.updateLives(uuid);
        gameManager.teleportToSpawnLocations(map, arenaInstance, arenaScoreboard, null, uuid, spawnLocations, spawnLocationsStart);
        broadcastMessage(ChatUtil.clr("&cTNT Run &7will commence in &c10 &7seconds get away from others!"));
        new BukkitRunnable() {
            @Override
            public void run() {
                tntRunEnabled = true;
                broadcastMessage(ChatUtil.clr("&cTNT Run &7mechanics have been enabled!"));
            }
        }.runTaskLater(gameManager.getPlugin(), 20 * 10);
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
        gameManager.getPlayerObject(player.getUniqueId()).takeLive(1);
        arenaScoreboard.updateLives(uuid);
        player.teleport(arenaInstance.getLocation());
        gameManager.toggleSpectator(player, true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.getInventory().clear();
        if(gameManager.getPlayerObject(player.getUniqueId()).getLives() <= 0) {
            broadcastMessage("&e" + player.getName() + " &7has been eliminated!");
            TitleAPI.sendTitle(player, 15, 40, 15, ChatUtil.clr("&c&lELIMINATED!"), ChatUtil.clr("&7Better luck next time!"));
            gameManager.setEndGame(player);
            tryEnd();
            return;
        }
        broadcastMessage("&e" + player.getName() + " &7has &e" +  gameManager.getPlayerObject(player.getUniqueId()).getLives() + " &7lives left.");
        new BukkitRunnable() {
            int seconds = 5;
            @Override
            public void run() {
                if(seconds != 0) {
                    TitleAPI.sendTitle(player, 5, 30, 0, ChatUtil.clr("&c&lYOU DIED!"), ChatUtil.clr("&7You will respawn in &c" + seconds + " &7seconds!"));
                }
                if(seconds == 0) {
                    TitleAPI.sendTitle(player, 15, 30, 15, ChatUtil.clr("&e&lRESPAWNED!"), "");
                    doRespawn(player);
                    cancel();
                }
                seconds--;
            }
        }.runTaskTimer(gameManager.getPlugin(), 0, 20l);
    }

    @Override
    public void doRespawn(Player player) {
        player.teleport(spawnLocations.get(player.getUniqueId()));
        giveKit(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                gameManager.toggleSpectator(player, false);
            }
        }.runTaskLater(gameManager.getPlugin(), 6L);
    }

    @Override
    public void broadcastMessage(String message) {
        gameManager.broadcastMessage(message, uuid);
    }

    @Override
    public void tryEnd() {
        if(gameState == GameState.WON) return;
        if(gameState != GameState.ACTIVE) return;
        if(gameManager.getPlayers(uuid).size() >= 2) {
            if(gameManager.getPlayersAlive(uuid).size() >= 2) {
                if(gameManager.getTeamsAlive(uuid).size() <= 1) {
                    setGameState(GameState.WON);
                    gameManager.updateStats(uuid, "tntrun");
                    gameManager.endGame(uuid, true, false);
                    return;
                }
            }
        }
        if(gameManager.getPlayersAlive(uuid).size() <= 1) {
            System.out.println("ending game");
            setGameState(GameState.WON);
            gameManager.updateStats(uuid, "tntrun");
            gameManager.endGame(uuid, false, false);
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

    public ArrayList<UUID> getPlayers() {
        return gameManager.getPlayers(uuid);
    }

    public int getDefaultY() {
        return (int) gameManager.getArenaManager().arenaYHeight;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void giveKit(Player player) {

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!getPlayers().contains(e.getDamager().getUniqueId())) return;
        if(gameState == GameState.WON) {
            e.setCancelled(true);
            e.getDamager().sendMessage(ChatUtil.clr("&cThe game has ended!"));
            return;
        }
        if(!getGameplayFlags().canPvP) e.setCancelled(true);
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player target = (Player) e.getEntity();
            if(damager.getScoreboard().getPlayerTeam(damager).getEntries().contains(target.getName())) {
                damager.sendMessage(ChatUtil.clr("&cFriendly fire is disabled."));
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!getPlayers().contains(e.getEntity().getUniqueId())) return;
        if(gameState != GameState.ACTIVE) return;
        if(e.getEntity() instanceof Player) {
            Player target = (Player) e.getEntity();
            if(target.getHealth() - e.getFinalDamage() < 0.5) {
                doDeath(target);
                broadcastMessage("&e" + target.getName() + " &7was killed!");
            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(!getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(getGameplayFlags().canBreakBlocks) {
            if(!getGameplayFlags().blockBreakAllowed.contains(e.getBlock().getType())) {
                if(!e.getPlayer().isOp()) {
                    e.setCancelled(true);
                }
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(gameState != GameState.ACTIVE) {
            e.setCancelled(true);
            e.setBuild(false);
        }
        if(getGameplayFlags().canPlaceBlocks) {
            if(!getGameplayFlags().blockPlaceAllowed.contains(e.getBlock().getType())) {
                if(!e.getPlayer().isOp()) {
                    e.setCancelled(true);
                    e.setBuild(false);
                }
            }
        }
        e.setCancelled(true);
        e.setBuild(false);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(e.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        if(gameState != GameState.ACTIVE) return;
        Player p = e.getPlayer();
        if(p.getLocation().getY() <= 80) {
            doDeath(p);
            broadcastMessage("&e" + p.getName() + " &7fell into the void!");
            return;
        }
        if(!p.isOnGround()) return;
        if(tntRunEnabled && gameState == GameState.ACTIVE) {
            Block firstBlock = p.getLocation().subtract(0, 1, 0).getBlock();
            Block secondBlock = p.getLocation().subtract(0, 2, 0).getBlock();

            if (firstBlock.getType().equals(Material.SAND) || firstBlock.getType().equals(Material.GRAVEL)) {
                if (secondBlock.getType().equals(Material.TNT)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            firstBlock.setType(Material.AIR);
                            secondBlock.setType(Material.AIR);
                        }
                    }.runTaskLater(gameManager.getPlugin(), 10L);
                }
            }
        }
    }

}
