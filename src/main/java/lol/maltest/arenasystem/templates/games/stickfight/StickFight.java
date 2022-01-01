package lol.maltest.arenasystem.templates.games.stickfight;

import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GameGame;
import lol.maltest.arenasystem.templates.GameplayFlags;
import lol.maltest.arenasystem.templates.games.stickfight.kit.StickFightKit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Random;

public class StickFight implements Game, Listener {


    @Override
    public void setArena(ArenaInstance arena) {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public void teleportToSpawnLocations() {

    }

    @Override
    public void someoneJoined(Player player, boolean spectator) {

    }

    @Override
    public GameGame.GameState getGameState() {
        return null;
    }

    @Override
    public GameplayFlags getGameplayFlags() {
        return null;
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public String getArenaSchematic() {
        return null;
    }

//    private Random random = new Random();
//
//    private static final String[] schematicNames = {
//            "stickfight_map1"
//    };
//
//
//    public StickFight(ArenaManager arenaManager) {
//        super(arenaManager,2, 2, schematicNames, new StickFightKit(arenaManager));
//        Bukkit.getPluginManager().registerEvents(this, arenaManager.getPlugin());
//        setState(GameState.STARTING);
//    }
//
//    public void setSettings() {
//        this.lives = 5;
//        this.canBreakBlocks = true;
//        this.canPlaceBlocks = true;
//        this.blockBreakAllowed.add(Material.WOOL);
//        this.blockPlaceAllowed.add(Material.WOOL);
//    }
//
//    public void setState(GameState state) {
//        if(this.getGameState().equals(GameState.ACTIVE) && state == GameState.STARTING) return;
//        gameState = state;
//        switch(state) {
//            case STARTING:
//                Location mapPasteLoc = getArenaManager().getFreeArenaLocation();
//                String map = schematicNames[random.nextInt(schematicNames.length)];
//                for(String key : arenaManager.getPlugin().getConfig().getConfigurationSection("spawnlocations." + map).getKeys(false)) {
//                    int x = arenaManager.getPlugin().getConfig().getInt("spawnlocations." + map + "." + key + ".x");
//                    int y = arenaManager.getPlugin().getConfig().getInt("spawnlocations." + map + "." + key + ".y");
//                    int z = arenaManager.getPlugin().getConfig().getInt("spawnlocations." + map + "." + key + ".z");
//                    spawnPoints.put(false, new Location(mapPasteLoc.getWorld(), mapPasteLoc.getX() + x, mapPasteLoc.getY() + y, mapPasteLoc.getZ() + z));
//                    // spawnPoints.put(usedBefore, location)
//                }
//                pasteSchematic(map, mapPasteLoc);
//                setSettings();
//                teleportToSpawnLocations();
//                // teleport players, need ot make a spawn system
//                break;
//            case ACTIVE:
//                // when countdown ends to start.
//                break;
//            case WON:
//                break;
//        }
//    }
//
//    @EventHandler
//    public void onBlockPlace(BlockPlaceEvent e) {
//        Block block = e.getBlock();
//        if(block.getType().equals(Material.WOOL)) {
//            // do the thing
//            if(block.getLocation().getY() > getArenaManager().arenaYHeight + 4) {
//                e.setBuild(false);
//            }
//            e.setBuild(true);
//        } else {
//            e.setBuild(false);
//        }
//    }

}
