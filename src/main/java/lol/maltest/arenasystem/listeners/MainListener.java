package lol.maltest.arenasystem.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MainListener implements Listener {

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
