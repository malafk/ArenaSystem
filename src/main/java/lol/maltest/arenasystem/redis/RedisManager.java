package lol.maltest.arenasystem.redis;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.games.parkourrace.ParkourRace;
import lol.maltest.arenasystem.templates.games.pvpbrawl.PvPBrawl;
import lol.maltest.arenasystem.templates.games.spleef.Spleef;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.templates.games.tntrun.TntRun;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import lol.maltest.arenasystem.ArenaSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RedisManager {

    private ArenaSystem plugin;

    // A static getter for the instance so that it can be retrieved without making a new instance.

    // A private constructor so instances cannot be created from out of the class.
    private Game game;
    public RedisManager(ArenaSystem plugin) {
        this.plugin = plugin;
    }

    public Jedis subscriber;
    public void subscribe(){
            subscriber = new Jedis("127.0.0.1", 6379);
            subscriber.connect();
            new Thread("Redis Subscriber") {
                @Override
                public void run() {
                    String[] channels = {"ayrie:servers"};
                    subscriber.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            RedisMessage redisMessage = new Gson().fromJson(message, RedisMessage.class);
                            System.out.println(redisMessage.toJSON());
                            if(redisMessage.getAction() == null) return;
                            switch (redisMessage.getAction()) {
                                case MAKE_GAME:
                                    String port = redisMessage.getParam("port");
                                    System.out.println("recieved make gmae checking if port is me");
                                    if (Integer.parseInt(port) != Bukkit.getServer().getPort()) break;
                                    System.out.println("got told to mkae a game..");
                                    QueueType queueType = QueueType.valueOf(redisMessage.getParam("type"));
                                    String players = redisMessage.getParam("players");
                                    String[] elements = players.split(", ");
                                    ArrayList<String> p = new ArrayList<>(Arrays.asList(elements));
                                    UUID uuid = UUID.randomUUID();
                                    switch (queueType) {
                                        case PVPBRAWL_SINGLES:
                                        case PVPBRAWL_DOUBLES:
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    game = new PvPBrawl(plugin.gameManager(), uuid);
                                                }
                                            }.runTask(plugin);
                                            break;
                                        case SPLEEF_DOUBLES:
                                        case SPLEEF_SINGLES:
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    game = new Spleef(plugin.gameManager(), uuid);
                                                }
                                            }.runTask(plugin);
                                            break;
                                        case TNTRUN_DOUBLES:
                                        case TNTRUN_SINGLES:
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    game = new TntRun(plugin.gameManager(), uuid);
                                                }
                                            }.runTask(plugin);
                                            break;
                                        case STICKFIGHT_SINGLES:
                                        case STICKFIGHT_DOUBLES:
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    game = new StickFight(plugin.gameManager(), uuid);
                                                }
                                            }.runTask(plugin);
                                            break;
                                        case PARKOURRACE_SINGLES:
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    game = new ParkourRace(plugin.gameManager(), uuid);
                                                }
                                            }.runTask(plugin);
                                            break;
                                    }
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            plugin.gameManager().addGame(uuid, game);
                                            plugin.gameManager().addPlayerToGame(uuid, p, game.getDefaultLives(), false);
                                        }
                                    }.runTask(plugin);
                                case SYNC_SERVERS:
                                    String synctype = redisMessage.getParam("type");
                                    switch (synctype) {
                                        case "game":
                                            String json = new RedisMessage(MessageAction.ADD_SERVER)
                                                    .setParam("server", "game")
                                                    .setParam("port", String.valueOf(plugin.getServer().getPort()))
                                                    .toJSON();
                                            publish("ayrie:servers", json);
                                            System.out.println("synced server with bungee!");
                                            break;
                                    }
//                                String serverName = redisMessage.getParam("server");
//                                String port = redisMessage.getParam("port");
//
////                                System.out.println(serverName + " port is " + port);

//                                addServer(serverName, Integer.parseInt(port));

                                    // Do actions here
                                    break;
                            }
                        }
                    }, channels);
                }
            }.start();
    }

    public void publish(String channel, String message) {
        try(Jedis publisher = new Jedis()) {
            publisher.publish(channel, message);
        }
    }
}
