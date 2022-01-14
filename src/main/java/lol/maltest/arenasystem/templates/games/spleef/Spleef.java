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
import lol.maltest.arenasystem.templates.games.spleef.kit.SpleefKit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
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

    public Spleef(GameManager gameManager, UUID uuid) {
        random = new Random();
        this.gameManager = gameManager;
        this.uuid = uuid;
        this.spleefKit = new SpleefKit();
        this.arenaScoreboard = new ArenaScoreboard(gameManager, "Spleef");
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
        arenaInstance.setSchemName(gameManager.getMapSettings().spleefMaps.get(random.nextInt(gameManager.getMapSettings().spleefMaps.size())).getSchematicName());
    }

    @Override
    public void start() {
        gameState = GameState.ACTIVE;
        System.out.println("start called");
        arenaScoreboard.addPlayersToScoreboard(uuid);
        arenaScoreboard.updateLives(uuid);
        teleportToSpawnLocations();
    }

    @Override
    public void end() {
        arenaScoreboard.getScoreboard().getTeams().clear();
        arenaScoreboard.getScoreboard().destroy();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void teleportToSpawnLocations() {
            Map maps = gameManager.getMapSettings().spleefMaps.get(0);
            maps.getSpawnpoints(arenaInstance.getLocation()).forEach(location -> {
                spawnLocationsStart.put(location, false);
                System.out.println("added a spawn " + location);
            });
            ArrayList<UUID> alreadyTeleported = new ArrayList<>();
            if (gameManager.getPlayers(uuid).size() <= 2) {
                for(UUID pUuid : gameManager.getPlayers(uuid)) {
                    Player player = Bukkit.getPlayer(pUuid);
                    if(player.isDead()) {
                        player.spigot().respawn();
                    }
                    for (java.util.Map.Entry<Location, Boolean> entry : spawnLocationsStart.entrySet()) {
                        Boolean used = entry.getValue();
                        Location loc = entry.getKey();
                        if (!used && !alreadyTeleported.contains(pUuid)) {
                            spawnLocations.put(player.getUniqueId(), loc);
                            spawnLocationsStart.replace(loc, true);
                            player.teleport(loc);
                            alreadyTeleported.add(pUuid);
                            player.setGameMode(GameMode.SURVIVAL);
                            giveKit(player);
                        }
                    }
                }
            } else {
                for(JScoreboardTeam team : arenaScoreboard.getScoreboard().getTeams()) { // insert get scoreboardteam
                    Location loc = null;
                    for (java.util.Map.Entry<Location, Boolean> entry : spawnLocationsStart.entrySet()) {
                        if (!entry.getValue()) {
                            loc = entry.getKey();
                            break;
                        }
                    }
                    spawnLocationsStart.replace(loc,true);
                    for (UUID p : team.getEntities()) {
                        Player player = Bukkit.getPlayer(p);
                        spawnLocations.put(player.getUniqueId(), loc);
                        player.teleport(loc);
                        alreadyTeleported.add(p);
                        player.setGameMode(GameMode.SURVIVAL);
                        giveKit(player);
                    }
                }
                for (java.util.Map.Entry<Location, Boolean> entry : spawnLocationsStart.entrySet()) {
                    Location loc = entry.getKey();
                }
            }
        }

    @Override
    public void someoneJoined(Player player, boolean spectator) {
        if(!spectator) {
            player.sendMessage(ChatUtil.clr("&7You have been put in to " + uuid));
        }
    }

    @Override
    public void doDeath(Player player) {
        gameManager.getPlayerObject(player.getUniqueId()).takeLive(1);
        arenaScoreboard.updateLives(uuid);

        if(gameManager.getPlayerObject(player.getUniqueId()).getLives() <= 0) {
            broadcastMessage("&e" + player.getName() + " &7has been eliminated!");
            TitleAPI.sendTitle(player, 15, 40, 15, ChatUtil.clr("&c&lELIMINATED!"), ChatUtil.clr("&7Better luck next time!"));
            player.teleport(arenaInstance.getLocation());
            player.setGameMode(GameMode.SPECTATOR);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.closeInventory();
            player.getInventory().clear();
            tryEnd();
            return;
        }
        broadcastMessage("&e" + player.getName() + " &7has &e" +  gameManager.getPlayerObject(player.getUniqueId()).getLives() + " &7lives left.");
        player.teleport(arenaInstance.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.getInventory().clear();
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
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(spawnLocations.get(player.getUniqueId()));
        player.setAllowFlight(false);
        giveKit(player);
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
        if(gameManager.getPlayers(uuid).size() >= 2) {
            if(gameManager.getPlayersAlive(uuid).size() >= 2) {
                if(gameManager.getTeamsAlive(uuid).size() <= 1) {
                    setGameState(GameState.WON);
                    gameManager.endGame(uuid, true);
                    return;
                }
            }
        }
        if(gameManager.getPlayersAlive(uuid).size() <= 1) {
            System.out.println("ending game");
            setGameState(GameState.WON);
            gameManager.endGame(uuid, false);
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
        spleefKit.giveKit(player);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(gameState != GameState.ACTIVE) {
            e.setCancelled(true);
            return;
        }
        if(!getPlayers().contains(e.getEntity().getUniqueId())) return;
        if(e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            Projectile p = (Projectile)e.getDamager();
            Player shooter = (Player) p.getShooter();
            if(p.getShooter() instanceof Player) {
                if(shooter.getScoreboard().getPlayerTeam(shooter).getEntries().contains(e.getEntity().getName())) {
                    shooter.sendMessage(ChatUtil.clr("&cFriendly fire is disabled."));
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player target = (Player) e.getEntity();
            if(damager.getScoreboard().getPlayerTeam(damager).getEntries().contains(target.getName())) {
                damager.sendMessage(ChatUtil.clr("&cFriendly fire is disabled."));
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
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
        if(player.getGameMode().equals(GameMode.SPECTATOR)) return;
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
                Location checkLoc = e.getBlock().getLocation();
                for(UUID p : getPlayers()) {
                    Player pl = Bukkit.getPlayer(p);
                    Location playerLoc = new Location(pl.getWorld(), pl.getLocation().getX(), pl.getLocation().getY(), pl.getLocation().getZ());
                    double distance = pl.getLocation().distance(checkLoc);
                    if(distance <= 1.5) {
                        OfflinePlayer getPlayer = e.getPlayer();
                        System.out.println("afsdf");
                        if(e.getPlayer().getScoreboard().getPlayerTeam(getPlayer).hasPlayer(pl)) {
                            e.setCancelled(true);
                            getPlayer.getPlayer().sendMessage(ChatUtil.clr("&cDon't team grief."));
                            return;
                        }
                    }
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
