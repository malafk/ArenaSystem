package lol.maltest.arenasystem.listeners;

import com.mojang.authlib.GameProfile;
import lol.maltest.arenasystem.ArenaSystem;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.GamePlayer;
import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MainListener implements Listener {

    private ArenaSystem plugin;

    public MainListener(ArenaSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void leafDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if(plugin.gameManager().getPlayerObject(player.getUniqueId()) != null) {
            GamePlayer gamePlayer = plugin.gameManager().getPlayerObject(player.getUniqueId());
            Game game = plugin.gameManager().getGame(gamePlayer.getGameUuid());
            plugin.gameManager().removePlayerFromGame(gamePlayer);
            game.broadcastMessage(ChatUtil.clr("&e" + player.getName() + " &7has quit!"));
            game.tryEnd();
        }
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setFlying(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.getInventory().clear();
        player.teleport(new Location(Bukkit.getWorld("void"), 0, 5, 0));
    }

}
