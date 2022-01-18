package lol.maltest.arenasystem.commands;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.AffineTransform;
import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PasteSchematic implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.isOp()) {
                if(args.length < 1) {
                    player.sendMessage(ChatUtil.clr("/pasteschematic <sschem>"));
                } else {
                    pasteSchematic(args[0], player.getLocation());
                    player.sendMessage("done");
                }
            }
        }
        return false;
    }

    public Clipboard pasteSchematic(String schemName, Location loc) {

        File schematic = new File("Schematics/" + schemName + ".schematic");
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            if (format != null) {
                Schematic schem = format.load(schematic);
                Date beforepaste = new Date();
                AffineTransform transform = new AffineTransform();
                transform = transform.rotateY(180);
                EditSession session = schem.paste(BukkitUtil.getLocalWorld(loc.getWorld()),
                        new Vector(loc.getX(), loc.getY(), loc.getZ()), false, false, transform);
                Date afterPaste = new Date();
                System.out.println("Pasting in the schem took " + (beforepaste.getTime() - afterPaste.getTime()));
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
