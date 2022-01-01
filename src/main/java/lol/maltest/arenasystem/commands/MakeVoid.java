package lol.maltest.arenasystem.commands;

import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MakeVoid implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.isOp()) {
                WorldCreator wc = new WorldCreator("void");
                wc.type(WorldType.FLAT);
                wc.generatorSettings("2;0;1;"); //This is what makes the world empty (void)
                wc.createWorld();
                Bukkit.broadcastMessage(ChatColor.GREEN + "Created a void world! Teleporting..");
                for(Player player1 : Bukkit.getOnlinePlayers()) {
                    player.teleport(new Location(Bukkit.getWorld("void"),0,0,0));
                }
            } else {
                player.sendMessage(ChatUtil.clr("No"));
            }
        }
        return false;
    }
}
