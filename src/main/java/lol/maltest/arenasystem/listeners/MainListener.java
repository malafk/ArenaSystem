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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MainListener implements Listener {

    private ArenaSystem plugin;

    public MainListener(ArenaSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
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
        if (plugin.gameManager().getPlayerObject(player.getUniqueId()) != null) {
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
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setFlying(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.getInventory().clear();
        for(PotionEffect effect:player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(new Location(Bukkit.getWorld("void"), 0, 64, 0));
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void dropItemEvent(PlayerDropItemEvent e) {
        if (!e.getPlayer().hasPermission("mal.admin")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getItem() != null) {
                if (e.getItem().hasItemMeta()) {
                    if (e.getItem().getItemMeta().hasDisplayName()) {
                        String name = e.getItem().getItemMeta().getDisplayName();
                        if (name.equalsIgnoreCase(ChatUtil.clr("&cReturn to lobby"))) {
//                            e.getPlayer().sendMessage("Debug: Send to lobby...");
                            plugin.gameManager().removeFromGames(e.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVoidDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                e.setCancelled(true);
            }
            if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
    }

    @EventHandler
    public void playerInteract(EntityInteractEvent e) {
        if(e.getEntity() instanceof Player) {
            if(((Player) e.getEntity()).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        if(plugin.gameManager().getPlayerObject(player.getUniqueId()) != null) {
            player.spigot().respawn();
            GamePlayer gamePlayer = plugin.gameManager().getPlayerObject(player.getUniqueId());
            Game game = plugin.gameManager().getGame(gamePlayer.getGameUuid());
            game.doDeath(player);
            gamePlayer.takeLive(1);
            game.broadcastMessage(ChatUtil.clr("&e" + player.getName() + " &7has died!"));
            game.tryEnd();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(plugin.gameManager().isSpec(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(plugin.gameManager().isSpec(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onProjectileThrow(ProjectileLaunchEvent e) {
        if(plugin.gameManager().isSpec((Player) e.getEntity().getShooter())) e.setCancelled(true);
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            if(plugin.gameManager().getPlayerObject(e.getDamager().getUniqueId()).isSpectator()) e.setCancelled(true);
            System.out.println(e.getDamager().getName() +" tried to hit in spectator");
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if(!event.getWhoClicked().hasPermission("mal.admin")) {
            event.setCancelled(true);
        }
    }

}
