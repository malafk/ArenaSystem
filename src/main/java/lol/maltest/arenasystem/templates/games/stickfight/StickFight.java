package lol.maltest.arenasystem.templates.games.stickfight;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.connorlinfoot.titleapi.TitleAPI;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameGame;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.games.pvpbrawl.PvPBrawl;
import lol.maltest.arenasystem.templates.games.stickfight.kit.StickFightKit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.*;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class StickFight implements Game, Listener {

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


    private StickFightKit stickFightKit;

    private GameManager gameManager;
    private ArenaInstance arenaInstance;
    private ArenaScoreboard arenaScoreboard;
    private UUID uuid;
    GameplayFlags thisGameFlags = new GameplayFlags();

    Map map;
    Random random;

    public StickFight(GameManager gameManager, UUID uuid) {
        random = new Random();
        this.gameManager = gameManager;
        this.uuid = uuid;
        this.stickFightKit = new StickFightKit();
        this.arenaScoreboard = new ArenaScoreboard(gameManager, "Stick Fight");
        Bukkit.getPluginManager().registerEvents(this, gameManager.getPlugin());

        allowToPlace.add(Material.WOOL);
        allowToBreak.add(Material.WOOL);
        thisGameFlags.blockBreakAllowed = allowToBreak;
        thisGameFlags.blockPlaceAllowed = allowToPlace;
        thisGameFlags.canBreakBlocks = true;
        thisGameFlags.canPlaceBlocks = true;
        thisGameFlags.canDamageTeamSelf = false;
        thisGameFlags.canPvP = true;
    }

    @Override
    public void setArena(ArenaInstance arena) {
        arenaInstance = arena;
        map = gameManager.getMapSettings().stickFightMaps.get(random.nextInt(gameManager.getMapSettings().stickFightMaps.size()));
        arenaInstance.setSchemName(map.getSchematicName());
    }

    @Override
    public void start() {
        gameState = GameState.ACTIVE;
        System.out.println("start called");
        arenaScoreboard.addPlayersToScoreboard(uuid);
        arenaScoreboard.updateLives(uuid);
        gameManager.teleportToSpawnLocations(map, arenaInstance, arenaScoreboard, stickFightKit, uuid, spawnLocations, spawnLocationsStart);
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
//                    TitleAPI titleDie = new TitleAPI(ChatUtil.clr("&e&lYOU DIED!"));
//                    titleDie.SubTitle(ChatUtil.clr("&7You will respawn in &c" + seconds + " &7seconds!"));
//                    titleDie.sendToPlayer(player);
                }
                if(seconds == 0) {
                    TitleAPI.sendTitle(player, 15, 30, 15, ChatUtil.clr("&e&lRESPAWNED!"), "");
                    doRespawn(player);
                    cancel();
                }
                seconds--;
            }
        }.runTaskTimer(gameManager.getPlugin(), 0, 20l);
        lastHitter.remove(player.getUniqueId());

    }

    @Override
    public void doRespawn(Player player) {
//        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        giveKit(player);
        player.teleport(spawnLocations.get(player.getUniqueId()));
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
        if(gameManager.getPlayers(uuid).size() >= 2) {
            if(gameManager.getPlayersAlive(uuid).size() >= 2) {
                if(gameManager.getTeamsAlive(uuid).size() <= 1) {
                    setGameState(GameState.WON);
                    gameManager.updateStats(uuid, "stickfight");
                    gameManager.endGame(uuid, true, false);
                    return;
                }
            }
        }
        if(gameManager.getPlayersAlive(uuid).size() <= 1) {
            System.out.println("ending game");
            setGameState(GameState.WON);
            gameManager.updateStats(uuid, "stickfight");
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
        return 5;
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

    public void giveKit(Player player) { 
        if(gameState.equals(GameState.WON)) return;
        stickFightKit.giveKit(player);
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!getPlayers().contains(e.getDamager().getUniqueId())) return;
        if(gameState == GameState.WON) {
            e.setCancelled(true);
            e.getDamager().sendMessage(ChatUtil.clr("&cThe game has ended!"));
            return;
        }
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player target = (Player) e.getEntity();
            if(damager.getScoreboard().getPlayerTeam(damager).getEntries().contains(target.getName())) {
                damager.sendMessage(ChatUtil.clr("&cFriendly fire is disabled."));
                e.setCancelled(true);
                return;
            }
            lastHitter.put(target.getUniqueId(), damager.getUniqueId());
//            lasthit = target.getUniqueId();
//            whohit = damager.getUniqueId();
            if(target.getHealth() - e.getFinalDamage() < 0.5) {
                gameManager.getPlayerObject(damager.getUniqueId()).addKill(1);
                doDeath(target);
                broadcastMessage("&e" + target.getName() + " &7was killed by &e" + damager.getName());
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
    public void playerMoveEvent(PlayerMoveEvent e) {
        if(!getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(gameState != GameState.ACTIVE) return;
        Player player = e.getPlayer();
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        if(getPlayers().contains(player.getUniqueId())) {
            if(player.getLocation().getY() <= 85) {
                if(lastHitter.get(player.getUniqueId()) == null) {
                    broadcastMessage("&e" + player.getName() + " &7jumped into the void!");
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
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!getPlayers().contains(e.getPlayer().getUniqueId())) return;
        if(gameState != GameState.ACTIVE) return;
        if(getGameplayFlags().canPlaceBlocks) {
            if(!getGameplayFlags().blockPlaceAllowed.contains(e.getBlock().getType())) {
                if(!e.getPlayer().isOp()) {
                    e.setCancelled(true);
                    e.setBuild(false);
                }
            }
        }
        if(!e.canBuild()) return;
        Player player = e.getPlayer();
        ItemStack placed = e.getItemInHand();
        if(getPlayers().contains(player.getUniqueId())) {
            Location spawn = spawnLocations.get(player.getUniqueId());
            Block b = e.getBlock();
            map.getSpawnpoints(arenaInstance.getLocation()).forEach(loc -> {
                System.out.println(loc);
                System.out.println(b.getLocation());
                if(loc.equals(b.getLocation())) {
                    player.sendMessage(ChatUtil.clr("&cYou can't build on spawn locations"));
                    e.setCancelled(true);
                    e.setBuild(false);
                }
            });
            if(e.getBlock().getZ() >= spawn.getZ() + 6 || e.getBlock().getZ() <= spawn.getZ() - 6) {
                e.setBuild(false);
                e.setCancelled(true);
                player.sendMessage(ChatUtil.clr("&cYou can't build this far out!"));
            }
            if(e.getBlock().getY() > getDefaultY() + 9) {
                e.setCancelled(true);
                e.setBuild(false);
                player.sendMessage(ChatUtil.clr("&cYou can't build this high!"));
            }
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if(!e.canBuild()) return;
                    if(!player.isOnline()) {
                        e.getBlock().setType(Material.AIR);
                        cancel();
                    }
                    BlockPosition blockPosition = new BlockPosition(new Vector(b.getX(), b.getY(), b.getZ()));
                    for(UUID pUuid : getPlayers()) {
                        Player player1 = Bukkit.getPlayer(pUuid);

                        PacketContainer minePacket = gameManager.getPlugin().getManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
                        minePacket.getIntegers().write(0, getBlockEntityId(b));
                        minePacket.getBlockPositionModifier().write(0, blockPosition);
                        minePacket.getIntegers().write(1, i);
                        try {
                            if(!player1.isOnline()) break;
                            gameManager.getPlugin().getManager().sendServerPacket(player1, minePacket);
                        } catch (InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(i == 9) {
                        for(Player player1 : Bukkit.getOnlinePlayers()) {
                            e.getBlock().setType(Material.AIR);
                            PacketContainer minePacket = gameManager.getPlugin().getManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
                            minePacket.getIntegers().write(0, getBlockEntityId(b));
                            minePacket.getBlockPositionModifier().write(0, blockPosition);
                            minePacket.getIntegers().write(1, 0);
                            try {
                                gameManager.getPlugin().getManager().sendServerPacket(player1, minePacket);
                            } catch (InvocationTargetException ex) {
                                ex.printStackTrace();
                            }
                        }
//                        PacketContainer minePacket = gameManager.getPlugin().getManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
//                        minePacket.getIntegers().write(0, getBlockEntityId(b));
//                        minePacket.getBlockPositionModifier().write(0, blockPosition);
//                        minePacket.getIntegers().write(1, 0);
//                            try {
//                                gameManager.getPlugin().getManager().sendServerPacket(p, minePacket);
//                            } catch (InvocationTargetException ex) {
//                                ex.printStackTrace();
//                            }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ItemStack giveBack = new ItemBuilder(placed.getType(), placed.getData().getData()).setAmount(1).build();
                                player.getInventory().addItem(giveBack);
                                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            }
                        }.runTaskLater(gameManager.getPlugin(), 60L);
                        i = 1;
                        cancel();
                    }
                    i++;
                }
            }.runTaskTimer(gameManager.getPlugin(), 0, 15L);
        }
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    private static int getBlockEntityId(Block block) {
        // There will be some overlap here, but these effects are very localized so it should be OK.
        return   ((block.getX() & 0xFFF) << 20) | ((block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
    }

}
