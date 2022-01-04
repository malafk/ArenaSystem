package lol.maltest.arenasystem.commands;

import lol.maltest.arenasystem.ArenaSystem;
import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AdminCMD implements CommandExecutor {

    private ArenaSystem plugin;

    public AdminCMD(ArenaSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("ayrie.admin")) {
                if(args.length < 1) {
                    player.sendMessage(ChatUtil.clr("&c/admin <setlives/addkills>"));
                } else {
                    switch (args[0]) {
                        case "setlives":
                            if(args.length > 2) {
                                if(!isInt(args[2])) {
                                    player.sendMessage(ChatUtil.clr("&c" + args[2] + " isnt a number!"));
                                }
                                if(Bukkit.getPlayer(args[1]) == null) {
                                    player.sendMessage(ChatUtil.clr("&cI cannot find " + args[1]));
                                } else {
                                    Player target = Bukkit.getPlayer(args[1]);
                                    int amount = Integer.parseInt(args[2]);
                                    if(plugin.gameManager().getPlayerObject(target.getUniqueId()) == null) {
                                        player.sendMessage(ChatUtil.clr("&cThat player isn't in a game!"));
                                    }
                                    plugin.gameManager().getPlayerObject(target.getUniqueId()).setLives(amount);
                                    player.sendMessage(ChatUtil.clr("&aDone! Gave " + target.getName() + " " + amount + " lives!"));
                                    UUID game = plugin.gameManager().getPlayerObject(target.getUniqueId()).getGameUuid();
                                    plugin.gameManager().getGame(game).getScoreboard().updateLives(game);
                                }
                            } else {
                                player.sendMessage(ChatUtil.clr("&c/admin setlives <player> >amount>"));
                            }
                            break;
                        case "addkills":
                            if(args.length > 2) {
                                if(!isInt(args[2])) {
                                    player.sendMessage(ChatUtil.clr("&c" + args[2] + " isnt a number!"));
                                }
                                if(Bukkit.getPlayer(args[1]) == null) {
                                    player.sendMessage(ChatUtil.clr("&cI cannot find " + args[1]));
                                } else {
                                    Player target = Bukkit.getPlayer(args[1]);
                                    int amount = Integer.parseInt(args[2]);
                                    if(plugin.gameManager().getPlayerObject(target.getUniqueId()) == null) {
                                        player.sendMessage(ChatUtil.clr("&cThat player isn't in a game!"));
                                    }
                                    plugin.gameManager().getPlayerObject(target.getUniqueId()).addKill(amount);
                                    player.sendMessage(ChatUtil.clr("&aDone! Gave " + target.getName() + " " + amount + " kills!"));
//                                    UUID game = plugin.gameManager().getPlayerObject(target.getUniqueId()).getGameUuid();
//                                    plugin.gameManager().getGame(game).getScoreboard().updateLives(game);
                                }
                            } else {
                                player.sendMessage(ChatUtil.clr("&c/admin setlives <player> <amount>"));
                            }
                            break;
                        default:
                            player.sendMessage(ChatUtil.clr("&cCant find that"));
                            break;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
