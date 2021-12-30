package lol.maltest.arenasystem.templates.games.stickfight;

import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.games.stickfight.kit.StickFightKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class StickFight extends Game implements Listener {

    private GameState gameState;
    private Random random = new Random();

    private static final String[] schematicNames = {
            "stickfight_map1"
    };

    public StickFight(ArenaManager arenaManager) {
        super(arenaManager,2, 2, schematicNames, new StickFightKit(arenaManager));
        Bukkit.getPluginManager().registerEvents(this, arenaManager.getPlugin());
        setState(GameState.STARTING);
    }

    public void setSettings() {
        this.canBreakBlocks = true;
        this.canPlaceBlocks = true;
        this.blockBreakAllowed.add(Material.WOOL);
        this.blockPlaceAllowed.add(Material.WOOL);
    }

    public void setState(GameState state) {
        if(this.getGameState().equals(GameState.ACTIVE) && state == GameState.STARTING) return;
        gameState = state;
        switch(state) {
            case STARTING:
                String map = schematicNames[random.nextInt(schematicNames.length)];
                pasteSchematic(map, getArenaManager().getFreeArenaLocation());
                setSettings();
                // teleport players, need ot make a spawn system
                break;
            case ACTIVE:
                // when countdown ends to start.
                break;
            case WON:
                break;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if(block.getType().equals(Material.WOOL)) {
            // do the thing
            if(block.getLocation().getY() > getArenaManager().arenaYHeight + 4) {
                e.setBuild(false);
            }
            e.setBuild(true);
        } else {
            e.setBuild(false);
        }
    }

}
