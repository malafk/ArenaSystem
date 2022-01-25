package lol.maltest.arenasystem.templates;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.AffineTransform;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.arena.ArenaScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameGame implements Listener {

    public enum GameState {
        STARTING,
        ACTIVE,
        WON,
    }

    /*
    OLD FILE DONT LOOK AT THIS SHIT
     */

    public ArenaManager arenaManager;
    public UUID gameUuid;

    public GameState gameState = GameState.STARTING;

    public HashMap<Boolean, Location> spawnPoints = new HashMap<>();

    public int countdown = -1;
    public boolean prepareCountdown = false;

    // players
    public int maxPlayers;
    public int minPlayers;
    public String[] schematics;
    public int lives = 3;

    // Gameplay Flags

    public boolean canPvP = true;
    public boolean canDamageTeamSelf = false;
    public boolean canDamageTeamMates = false;

    public boolean canBreakBlocks = true;
    public boolean canPlaceBlocks = true;

    public HashSet<Material> blockBreakAllowed = new HashSet<Material>();
    public HashSet<Material> blockPlaceAllowed = new HashSet<Material>();

    // Others

    public boolean teamArmor = true;

    // Gameplay Data

    public String winner = "No One";
    // TODO: Teams

    //    private
    public ArenaScoreboard arenaScoreboard;
    public Kit kit;

    public GameGame(ArenaManager arenaManager, int minPlayers, int maxPlayers, String[] schematics, Kit kit) {
        this.gameUuid = UUID.randomUUID();
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.schematics = schematics;
        this.arenaManager = arenaManager;
//        this.arenaScoreboard = new ArenaScoreboard(this);
        this.kit = kit;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

//    public ArenaInstance getArenaInstance() {
//        return arenaManager.getArena(this);
//    }


    public GameState getGameState() {
        return gameState;
    }

//    public void teleportToSpawnLocations() {
//        System.out.println("teleporting");
//        for(UUID pUuid : getArenaInstance().getPlayers()) {
//            Player player = Bukkit.getPlayer(pUuid);
//            for(Location location : spawnPoints.values()) {
//                for(Boolean used : spawnPoints.keySet()) {
//                    if(!used) {
//                        player.teleport(location);
//                        spawnPoints.replace(true, location);
//                    }
//                }
//            }
//        }
//    }

    public Clipboard pasteSchematic(String schemName, Location loc) {

        File schematic = new File("Schematics/" + schemName + ".schematic");
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            if (format != null) {
                Schematic schem = format.load(schematic);
                Timer timeToPaste = new Timer();
                AffineTransform transform = new AffineTransform();
                transform = transform.rotateY(180);
                EditSession session = schem.paste(BukkitUtil.getLocalWorld(loc.getWorld()),
                        new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ()), false, false, transform);
                System.out.println("Pasting in the schem");
                session.flushQueue();
            } else {
                System.out.println("Could not load schematic: " + schemName + ". Does not exist at location "
                        + schematic.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
