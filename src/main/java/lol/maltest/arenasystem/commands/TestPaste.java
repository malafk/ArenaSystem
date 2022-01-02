package lol.maltest.arenasystem.commands;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.AffineTransform;
import lol.maltest.arenasystem.ArenaSystem;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.GameType;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.templates.games.testgame.TestGame;
import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.junit.rules.TestName;

import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestPaste implements CommandExecutor {

    private ArenaSystem main;

    public TestPaste(ArenaSystem main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            UUID uuid = UUID.randomUUID();
            TestGame game = new TestGame(main.gameManager(), uuid);
            main.gameManager().addGame(uuid, game);
            if(args.length > 0) {
                Player p = Bukkit.getPlayer(args[0]);
                if(p == null) {
                    player.sendMessage(ChatUtil.clr("&c/testpaste [2ndplayer]"));
                }
                main.gameManager().addPlayerToGame(uuid, p, game.getDefaultLives(), false);
            }
            main.gameManager().addPlayerToGame(uuid, player, game.getDefaultLives(), false);
            player.sendMessage(ChatUtil.clr("Created game with uuid " + uuid + " teleporting in 2 seconds."));
//            new BukkitRunnable() {
//                @Override
//                public void run() {
////                    main.gameManager().startGame(uuid);
//                    player.sendMessage(ChatUtil.clr("&aStarting game &7uuid: " + uuid));
//                }
//            }.runTaskLater(main, 40L);
//            new ArenaInstance(main.getArenaManager().getPlugin(), main.getArenaManager(), GameType.STICKFIGHT_SINGLE, players);
//            new StickFight(main.getArenaManager());
            player.sendMessage(ChatColor.GREEN + "Game should of been made.");
        } else {
            System.out.println("You can only use this ingame.");

        }
        return false;
    }



}
