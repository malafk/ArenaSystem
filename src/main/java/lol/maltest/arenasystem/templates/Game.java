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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Timer;
import java.util.UUID;

public class Game implements Listener {

    public enum GameState {
        STARTING,
        ACTIVE,
        WON,
    }


    private ArenaManager arenaManager;
    private UUID gameUuid;

    private GameState gameState = GameState.STARTING;

    private int countdown = -1;
    private boolean prepareCountdown = false;

    // players
    private int maxPlayers;
    private int minPlayers;
    private String[] schematics;
    private int lifes;

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
    private ArenaScoreboard arenaScoreboard;
    private Kit kit;

    public Game(ArenaManager arenaManager, int minPlayers, int maxPlayers, String[] schematics, Kit kit) {
        this.gameUuid = UUID.randomUUID();
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.schematics = schematics;
        this.arenaManager = arenaManager;
        this.arenaScoreboard = new ArenaScoreboard(this);
        this.kit = kit;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

    public ArenaInstance getArenaInstance() {
        return arenaManager.getArena(this);
    }


    public GameState getGameState() {
        return gameState;
    }

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
