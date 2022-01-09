package lol.maltest.arenasystem.redis;

import com.google.gson.Gson;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
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
    public Jedis subscriber;
    public RedisManager(ArenaSystem plugin) {
        subscriber = new Jedis();

        this.plugin = plugin;
    }

    public void subscribe(){
        subscriber.connect();

        new Thread("Redis Subscriber"){
            @Override
            public void run(){
                String[] channels = {"ayrie:servers"};
                subscriber.subscribe(new JedisPubSub(){
                    @Override
                    public void onMessage(String channel, String message){
                        RedisMessage redisMessage = new Gson().fromJson(message, RedisMessage.class);
                        switch (redisMessage.getAction())
                        {
                            case MAKE_GAME:
                                QueueType queueType = QueueType.valueOf(redisMessage.getParam("type"));
                                String players = redisMessage.getParam("players");
                                String[] elements = players.split(", ");
                                ArrayList<String> p = new ArrayList<>(Arrays.asList(elements));
                                UUID uuid = UUID.randomUUID();
                                switch (queueType) {
                                    case STICKFIGHT_SINGLES:
                                    case STICKFIGHT_DOUBLES:
                                        StickFight game = new StickFight(plugin.gameManager(), uuid);
                                        plugin.gameManager().addGame(uuid, game);
                                        plugin.gameManager().addPlayerToGame(uuid, p, game.getDefaultLives(), false);
                                }
                            case SYNC_SERVERS:
                                String synctype = redisMessage.getParam("type");
                                switch (synctype) {
                                    case "game":
                                        String json = new RedisMessage(MessageAction.ADD_SERVER)
                                                .setParam("server", "game")
                                                .setParam("port", String.valueOf(plugin.getServer().getPort()))
                                                .toJSON();
                                        plugin.pool.getResource().publish("ayrie:servers", json);
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
}
