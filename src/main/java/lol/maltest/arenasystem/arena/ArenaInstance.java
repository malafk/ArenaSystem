package lol.maltest.arenasystem.arena;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.AffineTransform;
import lol.maltest.arenasystem.ArenaSystem;
import lol.maltest.arenasystem.GameManager;
import lol.maltest.arenasystem.map.Map;
import lol.maltest.arenasystem.templates.GameGame;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaInstance {

    private GameManager gameManager;
    private Location location;
    private String schemName;
    private boolean isFFA = true;

    public ArenaInstance(GameManager gameManager, String schemName) {
        this.gameManager = gameManager;
        this.schemName = schemName;
        this.location = gameManager.getArenaManager().register(gameManager.getPlugin(), this);
//        checkFFA:
//        for (String team : players.keySet()) {
//            for (String otherTeam : players.keySet()) {
//                if (!team.equals(otherTeam)) {
//                    setFFA(false);
//                    break checkFFA;
//                }
//            }
//        }
        pasteSchematic(schemName, location);
    }

    public void deleteSchemArena() {
        // todo - unload chunks, send to lobby..
    }

    public String getSchemName() {
        return schemName;
    }

    public Location getLocation() {
        return location;
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
                        new Vector(loc.getX(), loc.getY(), loc.getZ()), false, false, transform);
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