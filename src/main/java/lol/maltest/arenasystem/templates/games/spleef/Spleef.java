package lol.maltest.arenasystem.templates.games.spleef;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.connorlinfoot.titleapi.TitleAPI;
import com.google.common.base.Splitter;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.games.pvpbrawl.PvPBrawl;
import lol.maltest.arenasystem.templates.games.spleef.kit.SpleefKit;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.templates.games.tntrun.TntRun;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

public class Spleef implements Game, Listener {
    public enum GameState {

        STARTING, ACTIVE, WON
    }

    public GameState gameState = GameState.STARTING;

    private HashSet<Material> allowToPlace = new HashSet<>();
    private HashSet<Material> allowToBreak = new HashSet<>();

    HashMap<UUID, Location> spawnLocations = new HashMap<>();
    HashMap<Location, Boolean> spawnLocationsStart = new HashMap<>();



    private HashMap<UUID, UUID> lastHitter = new HashMap<>();
//    UUID lasthit;
//    UUID whohit;
    // playerHit, personHitting


    private SpleefKit spleefKit;

    private GameManager gameManager;
    private ArenaInstance arenaInstance;
    private ArenaScoreboard arenaScoreboard;
    private UUID uuid;
    GameplayFlags thisGameFlags = new GameplayFlags();

    Random random;
    Map map;

    public Spleef(GameManager gameManager, UUID uuid) {
        random = new Random();
        this.gameManager = gameManager;
        this.uuid = uuid;
        this.spleefKit = new SpleefKit();
        this.arenaScoreboard = new ArenaScoreboard(gameManager, "Spleef Fight");
        Bukkit.getPluginManager().registerEvents(this, gameManager.getPlugin());

        allowToBreak.add(Material.SNOW_BLOCK);
        thisGameFlags.blockBreakAllowed = allowToBreak;
        thisGameFlags.canBreakBlocks = true;
        thisGameFlags.canPlaceBlocks = false;
        thisGameFlags.canDamageTeamSelf = false;
        thisGameFlags.canPvP = false;
    }

    @Override
    public void setArena(ArenaInstance arena) {
        arenaInstance = arena;
        map = gameManager.getMapSettings().spleefMaps.get(random.nextInt(gameManager.getMapSettings().spleefMaps.size()));
        arenaInstance.setSchemName(map.getSchematicName());
    }

    @Override
    public void start() {
        gameState = GameState.ACTIVE;
        System.out.println("start called");
        arenaScoreboard.addPlayersToScoreboard(uuid);
        arenaScoreboard.updateLives(uuid);
        gameManager.teleportToSpawnLocations(map, arenaInstance, arenaScoreboard, spleefKit, uuid, spawnLocations, spawnLocationsStart);
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
        gameManager.getPlayers(uuid).forEach(p -> {
            Player player = Bukkit.getPlayer(p);
            player.sendMessage(ChatUtil.clr(message));
        });
    }

    @Override
    public void tryEnd() {
        if(gameState == GameState.WON) return;
        if(gameManager.getPlayers(uuid).size() >= 2) {
            if(gameManager.getPlayersAlive(uuid).size() >= 2) {
                if(gameManager.getTeamsAlive(uuid).size() <= 1) {
                    setGameState(GameState.WON);
                    gameManager.endGame(uuid, true, false);
                    return;
                }
            }
        }
        if(gameManager.getPlayersAlive(uuid).size() <= 1) {
            System.out.println("ending game");
            setGameState(GameState.WON);
            gameManager.endGame(uuid, false, false);
        }
    }

    @Override
    public GameplayFlags getGameplayFlags() {
        return thisGameFlags;
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
    public int getDefaultLives() {
        return 1;
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
        if(gameState.equals(PvPBrawl.GameState.WON)) return;
        spleefKit.giveKit(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!getPlayers().contains(e.getDamager().getUniqueId())) return;
        if(gameState == GameState.WON) {
            e.setCancelled(true);
            e.getDamager().sendMessage(ChatUtil.clr("&cThe game has ended!"));
            return;
        }
        if(!getPlayers().contains(e.getEntity().getUniqueId())) return;
        e.setCancelled(true);
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
    public void onProjectile(ProjectileHitEvent e) {
        Projectile target = (Projectile) e.getEntity();
        Player p = (Player) target.getShooter();
        if(!getPlayers().contains(p.getUniqueId())) return;
        if(getPlayers().contains(p.getUniqueId())) {
                Location loc = target.getLocation();
                Vector vec = target.getVelocity();
                Location loc2 = new Location(loc.getWorld(), loc.getX()+vec.getX(), loc.getY()+vec.getY(), loc.getZ()+vec.getZ());
                System.out.println(loc2.getBlock().getType().toString());
                if (loc2.getBlock().getType().equals(Material.SNOW_BLOCK)) {
                    loc2.getBlock().setType(Material.AIR);
                }
//                {
//                System.out.println(target.getLocation().getBlock().getType());
//                if(target.getLocation().getBlock().getType().equals(Material.SNOW)) {
//                    System.out.println("lo1");
//                }
            }
        }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        if(!getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(gameState != GameState.ACTIVE) return;
        Player player = e.getPlayer();
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        if(getPlayers().contains(player.getUniqueId())) {
            if(player.getLocation().getY() <= 85) {
                if(lastHitter.get(player.getUniqueId()) == null) {
                    broadcastMessage("&e" + player.getName() + " &7fell into the void!");
                    doDeath(player);
                    return;
                }
                Player whoHit = Bukkit.getPlayer(lastHitter.get(player.getUniqueId()));
                broadcastMessage("&e" + player.getName() + " &7was hit into the void by &e" + whoHit.getName());
                gameManager.getPlayerObject(whoHit.getUniqueId()).addKill(1);
                doDeath(player);
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
            } else {
                e.getBlock().setType(Material.AIR);
                Location checkLoc = e.getBlock().getLocation();
                for(UUID p : getPlayers()) {
                    Player pl = Bukkit.getPlayer(p);
                    Location playerLoc = new Location(pl.getWorld(), pl.getLocation().getX(), pl.getLocation().getY(), pl.getLocation().getZ());
                    double distance = pl.getLocation().distance(checkLoc);
                    if(distance <= 1.5) {
                        OfflinePlayer getPlayer = e.getPlayer();
                        if(e.getPlayer().getScoreboard().getPlayerTeam(getPlayer).hasPlayer(pl)) {
                            e.setCancelled(true);
                            getPlayer.getPlayer().sendMessage(ChatUtil.clr("&cDon't team grief."));
                            return;
                        }
                    }
                }
                if(random.nextInt(3) == 1) {
                    ItemStack snowball = new ItemBuilder(Material.SNOW_BALL).setAmount(1).build();
                    e.getPlayer().getInventory().addItem(snowball);
                }
            }
        }
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
    }
}
