package lol.maltest.arenasystem.templates.games.testgame;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameGame;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.games.stickfight.kit.StickFightKit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import lol.maltest.arenasystem.util.TitleAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TestGame implements Game, Listener {

    private HashSet<Material> allowToPlace = new HashSet<>();
    private HashSet<Material> allowToBreak = new HashSet<>();

    HashMap<UUID, Location> spawnLocations = new HashMap<>();



    private StickFightKit stickFightKit;

    private GameManager gameManager;
    private ArenaInstance arenaInstance;
    private ArenaScoreboard arenaScoreboard;
    private UUID uuid;
    GameplayFlags thisGameFlags = new GameplayFlags();

    public TestGame(GameManager gameManager, UUID uuid) {
        this.gameManager = gameManager;
        this.uuid = uuid;
        this.stickFightKit = new StickFightKit();
        this.arenaScoreboard = new ArenaScoreboard(gameManager);
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
    }

    @Override
    public void start() {
        System.out.println("start called");
        arenaScoreboard.addPlayersToScoreboard(uuid);
        arenaScoreboard.updateLives(uuid);
        teleportToSpawnLocations();
    }

    @Override
    public void end() {

    }

    @Override
    public void teleportToSpawnLocations() {
//        for(Map map : gameManager.getMapSettings().stickFightMaps) {
//            if(map.getSchematicName().equals(getArenaSchematic())) {
//                System.out.println("done added spawnlocs");
//                map.getSpawnpoints(arenaInstance.getLocation()).forEach(loc -> spawnLocations.put(loc, false));
//            }
//        }
//        gameManager.getMapSettings().stickFightMaps.forEach(loc -> {
//            if(loc.getSchematicName().equals(arenaInstance.getSchemName())) {
//                loc.getSpawnpoints(arenaInstance.getLocation()).forEach(location -> spawnLocations.put(location, false));
//                System.out.println("added a spawnpoint");
//                return;
//            }
//        });
        Map stickFight = gameManager.getMapSettings().stickFightMaps.get(0);
        stickFight.getSpawnpoints(arenaInstance.getLocation()).forEach(location -> {
            spawnLocations.put(null, location);
            System.out.println("added a spawn");
        });
        ArrayList<UUID> alreadyTeleported = new ArrayList<>();
        for(UUID pUuid : gameManager.getPlayers(uuid)) {
            Player player = Bukkit.getPlayer(pUuid);
            for (java.util.Map.Entry<UUID, Location> entry : spawnLocations.entrySet()) {
                Location loc = entry.getValue();
                UUID used = entry.getKey();
                if (!(used == null) && !alreadyTeleported.contains(pUuid)) {
                    player.teleport(loc);
                    System.out.println("teleported " + player + " to " + loc);
                    alreadyTeleported.add(pUuid);
                    spawnLocations.replace(player.getUniqueId(), loc);
                }
            }
        }
//        for(UUID pUuid : gameManager.getPlayers(uuid)) {
//            for(Location loc : spawnLocations.keySet()) {
//                for(Boolean used : spawnLocations.values()) {
//                    if(!used) {
//                        player.teleport(loc);
//                        spawnLocations.replace(loc, true);
//                    }
//                }
//            }
//        }
    }

    @Override
    public void someoneJoined(Player player, boolean spectator) {
        if(!spectator) {
            stickFightKit.giveKit(player);
            player.sendMessage(ChatUtil.clr("&7You have been put in to " + uuid));
        }
    }

    @Override
    public void doDeath(Player player) {
        gameManager.getPlayerObject(player.getUniqueId()).takeLive(1);
        arenaScoreboard.updateLives(uuid);
        player.teleport(arenaInstance.getLocation());
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 10));
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
                    TitleAPI titleDie = new TitleAPI(ChatUtil.clr("&e&lYOU DIED!"));
                    titleDie.SubTitle(ChatUtil.clr("&7You will respawn in &c" + seconds + " &7seconds!"));
                    titleDie.sendToPlayer(player);
                } else {
                    doRespawn(player);
                    cancel();
                }
                seconds--;
            }
        }.runTaskTimerAsynchronously(gameManager.getPlugin(), 0, 20l);
    }

    @Override
    public void doRespawn(Player player) {
        player.getActivePotionEffects().clear();
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        stickFightKit.giveKit(player);
        player.teleport(spawnLocations.get(player.getUniqueId()));
    }

    @Override
    public GameGame.GameState getGameState() {
        return null;
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
    public int getDefaultLives() {
        return 3;
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

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(getPlayers().contains(player.getUniqueId())) {
            if(player.getLocation().getY() <= 85) {

            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack placed = e.getItemInHand();
        if(getPlayers().contains(player.getUniqueId())) {
            if(e.getBlock().getY() > getDefaultY() + 9) {
                e.setCancelled(true);
                player.sendMessage(ChatUtil.clr("&cYou can't build this high!"));
            }
            Block b = e.getBlock();
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    BlockPosition blockPosition = new BlockPosition(new Vector(b.getX(), b.getY(), b.getZ()));
                    for(UUID pUuid : getPlayers()) {
                        Player player1 = Bukkit.getPlayer(pUuid);
                        Random r = new Random();

                        PacketContainer minePacket = gameManager.getPlugin().getManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
                        minePacket.getIntegers().write(0, getBlockEntityId(b) + r.nextInt(55557));
                        minePacket.getBlockPositionModifier().write(0, blockPosition);
                        minePacket.getIntegers().write(1, i);
                        try {
                            gameManager.getPlugin().getManager().sendServerPacket(player1, minePacket);
                        } catch (InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(i == 11) {
                        e.getBlock().setType(Material.AIR);
//                        PacketContainer minePacket = gameManager.getPlugin().getManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
//                        minePacket.getIntegers().write(0, getBlockEntityId(b));
//                        minePacket.getBlockPositionModifier().write(0, blockPosition);
//                        minePacket.getIntegers().write(1, 0);
                        for(UUID pUuid : getPlayers()) {
                            Player p = Bukkit.getPlayer(pUuid);
//                            try {
//                                gameManager.getPlugin().getManager().sendServerPacket(p, minePacket);
//                            } catch (InvocationTargetException ex) {
//                                ex.printStackTrace();
//                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    ItemStack giveBack = new ItemBuilder(placed.getType(), placed.getData().getData()).setAmount(1).build();
                                    p.getInventory().addItem(giveBack);
                                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                                }
                            }.runTaskLater(gameManager.getPlugin(), 60L);
                        }
                        i = 1;
                        cancel();
                    }
                    i++;
                }
            }.runTaskTimer(gameManager.getPlugin(), 0, 5L);
        }
    }

    private static int getBlockEntityId(Block block) {
        // There will be some overlap here, but these effects are very localized so it should be OK.
        return   ((block.getX() & 0xFFF) << 20)
                | ((block.getZ() & 0xFFF) << 8)
                | (block.getY() & 0xFF);
    }

}
