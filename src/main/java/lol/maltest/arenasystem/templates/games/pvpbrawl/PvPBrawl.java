package lol.maltest.arenasystem.templates.games.pvpbrawl;

import com.connorlinfoot.titleapi.TitleAPI;
import dev.jcsoftware.jscoreboards.JScoreboardTeam;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.templates.games.pvpbrawl.kit.NoDebuffKit;
import lol.maltest.arenasystem.templates.games.pvpbrawl.kit.OldKit;
import lol.maltest.arenasystem.templates.games.pvpbrawl.kit.VanillaKit;
import lol.maltest.arenasystem.templates.games.spleef.Spleef;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PvPBrawl implements Game, Listener {
    public enum GameState {

        STARTING, ACTIVE, WON
    }

    public GameState gameState = GameState.STARTING;

    private HashSet<Material> allowToPlace = new HashSet<>();
    private HashSet<Material> allowToBreak = new HashSet<>();

    HashMap<UUID, Location> spawnLocations = new HashMap<>();
    HashMap<Location, Boolean> spawnLocationsStart = new HashMap<>();

    private HashMap<UUID, UUID> lastHitter = new HashMap<>();

    private Kit kit;

    private ArrayList<Kit> kits = new ArrayList<>();

    Kit noDebuff = new Kit();
    Kit oldKit = new Kit();
    Kit vanillaKit = new Kit();

    private GameManager gameManager;
    private ArenaInstance arenaInstance;
    private ArenaScoreboard arenaScoreboard;
    private UUID uuid;
    GameplayFlags thisGameFlags = new GameplayFlags();

    Random random;

    Map map;

    public PvPBrawl(GameManager gameManager, UUID uuid) {
        random = new Random();
        this.gameManager = gameManager;
        this.uuid = uuid;
        kits.add(new NoDebuffKit());
        kits.add(new OldKit());
        kits.add(new VanillaKit());
        kit = kits.get(random.nextInt(kits.size())); // IDK
        this.arenaScoreboard = new ArenaScoreboard(gameManager, "PvP Brawl");
        Bukkit.getPluginManager().registerEvents(this, gameManager.getPlugin());

        thisGameFlags.blockPlaceAllowed.add(Material.FIRE);
        thisGameFlags.blockBreakAllowed = allowToBreak;
        thisGameFlags.canBreakBlocks = false;
        thisGameFlags.canPlaceBlocks = false;
        thisGameFlags.canDamageTeamSelf = false;
        thisGameFlags.canPvP = true;
    }

    @Override
    public void setArena(ArenaInstance arena) {
        arenaInstance = arena;
        map = gameManager.getMapSettings().pvpBrawlMaps.get(random.nextInt(gameManager.getMapSettings().pvpBrawlMaps.size()));
        arenaInstance.setSchemName(map.getSchematicName());
    }

    @Override
    public void start() {
        gameState = GameState.ACTIVE;
        arenaScoreboard.addPlayersToScoreboard(uuid);
        System.out.println("start called");
        arenaScoreboard.updateLives(uuid);
        gameManager.teleportToSpawnLocations(map, arenaInstance, arenaScoreboard, kit, uuid, spawnLocations, spawnLocationsStart);
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
                    gameManager.updateStats(uuid, "pvpbrawl");
                    gameManager.endGame(uuid, true, false);
                    return;
                }
            }
        }
        if(gameManager.getPlayersAlive(uuid).size() <= 1) {
            System.out.println("ending game");
            setGameState(GameState.WON);
            gameManager.updateStats(uuid, "pvpbrawl");
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
        if(gameState.equals(GameState.WON)) return;
        kit.giveKit(player);
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
            if(target.getHealth() - e.getFinalDamage() < 0.5) {
                gameManager.getPlayerObject(damager.getUniqueId()).addKill(1);
                doDeath(target);
                broadcastMessage("&e" + target.getName() + " &7was killed by &e" + damager.getName());
                e.setDamage(0);
            }
        }
    }

//    @EventHandler
//    public void onDamage(EntityDamageEvent e) {
//        if(!getPlayers().contains(e.getEntity().getUniqueId())) return;
//        if(gameState != GameState.ACTIVE) return;
//        if(e.getEntity() instanceof Player) {
//            Player target = (Player) e.getEntity();
//            if(target.getHealth() - e.getFinalDamage() < 0.5) {
//                doDeath(target);
//                broadcastMessage("&e" + target.getName() + " &7was killed!");
//            }
//        }
//    }

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

    public void makeKits() {
        // NoDebuff
        noDebuff.addContent(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatUtil.clr("&7Sword")).addEnchant(Enchantment.DAMAGE_ALL, 3).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.FIRE_ASPECT, 2).build(), 0);
        noDebuff.addContent(new ItemBuilder(Material.ENDER_PEARL).setAmount(16).build(), 1);
        noDebuff.addContent(new ItemBuilder(Material.COOKED_BEEF).setAmount(64).build(), 8);
        noDebuff.addContent(potionBuilder(PotionType.SPEED, 1, "Potion of Swiftness", false, ""), 2);
        noDebuff.addContent(potionBuilder(PotionType.SPEED, 1, "Potion of Swiftness", false, ""), 35);
        noDebuff.addContent(potionBuilder(PotionType.SPEED, 1, "Potion of Swiftness", false, ""), 26);
        noDebuff.addContent(potionBuilder(PotionType.SPEED, 1, "Potion of Swiftness", false, ""), 17);
        noDebuff.addContent(potionBuilder(PotionType.FIRE_RESISTANCE, 0, "Potion of Fire Resistance", false, ""), 3);
        for(int i =1; i < 28; i++) {
            noDebuff.addContent(potionBuilder(PotionType.INSTANT_HEAL, 1, "Splash Potion of Healing", true, ""));
        }
        noDebuff.addArmor(new ItemStack(Material.DIAMOND_BOOTS));
        noDebuff.addArmor(new ItemStack(Material.DIAMOND_LEGGINGS));
        noDebuff.addArmor(new ItemStack(Material.DIAMOND_CHESTPLATE));
        noDebuff.addArmor(new ItemStack(Material.DIAMOND_HELMET));

        // oldkit

        oldKit.addContent(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatUtil.clr("&7Sword")).build(), 0);
        oldKit.addArmor(new ItemStack(Material.DIAMOND_BOOTS));
        oldKit.addArmor(new ItemStack(Material.DIAMOND_LEGGINGS));
        oldKit.addArmor(new ItemStack(Material.DIAMOND_CHESTPLATE));
        oldKit.addArmor(new ItemStack(Material.DIAMOND_HELMET));

        // vanilla kit

        vanillaKit.addContent(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatUtil.clr("&7Sword")).addEnchant(Enchantment.DURABILITY, 69).build(), 0);
        vanillaKit.addContent(new ItemBuilder(Material.FISHING_ROD).setDisplayName(ChatUtil.clr("&7Rod")).build(), 1);
        vanillaKit.addContent(new ItemBuilder(Material.FLINT_AND_STEEL).setDisplayName(ChatUtil.clr("&7Fire")).setDurability(5).build(), 8);
        vanillaKit.addArmor(new ItemStack(Material.IRON_BOOTS));
        vanillaKit.addArmor(new ItemStack(Material.IRON_LEGGINGS));
        vanillaKit.addArmor(new ItemStack(Material.IRON_CHESTPLATE));
        vanillaKit.addArmor(new ItemStack(Material.IRON_HELMET));
    }

    public static ItemStack potionBuilder(PotionType type, int level, String name, boolean splashable, String... lore){

        Potion potion = new Potion(type, level);
        potion.setSplash(splashable);

        ItemStack item = potion.toItemStack(1);


        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtil.clr(name));
        meta.setLore(Arrays.asList(lore));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        item.setItemMeta(meta);

        return item;
    }
}
