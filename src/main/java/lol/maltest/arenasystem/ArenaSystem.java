package lol.maltest.arenasystem;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.EditSession;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.commands.AdminCMD;
import lol.maltest.arenasystem.commands.MakeVoid;
import lol.maltest.arenasystem.commands.PasteSchematic;
import lol.maltest.arenasystem.commands.TestPaste;
import lol.maltest.arenasystem.listeners.MainListener;
import lol.maltest.arenasystem.redis.MessageAction;
import lol.maltest.arenasystem.redis.RedisManager;
import lol.maltest.arenasystem.redis.RedisMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;

public final class ArenaSystem extends JavaPlugin {

    private ArenaSystem plugin;
    private GameManager gameManager;
    public JedisPool pool;
    public RedisManager redisManager;
    ProtocolManager manager;
    private boolean j = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        if(j) {
            this.redisManager = new RedisManager(this);
            pool = new JedisPool("127.0.0.1", 6379);
            System.out.println("connected to redis!");
            redisManager.subscribe();
        }

        this.gameManager = new GameManager(this);

        manager = ProtocolLibrary.getProtocolManager();

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MainListener(this), this);

        getCommand("testpaste").setExecutor(new TestPaste(this));
        getCommand("makevoid").setExecutor(new MakeVoid());
        getCommand("admin").setExecutor(new AdminCMD(this));

        if(j) {
            System.out.println("registering game with proxy");
            String json = new RedisMessage(MessageAction.ADD_SERVER)
                    .setParam("server", "game")
                    .setParam("port", String.valueOf(getServer().getPort()))
                    .toJSON();
            plugin.pool.getResource().publish("ayrie:servers", json);
        }
    }

    @Override
    public void onDisable() {
        if(j) {
            System.out.println("Sending shutting down redis message");
            String json = new RedisMessage(MessageAction.REMOVE_SERVER)
                    .setParam("server", "game")
                    .setParam("port", String.valueOf(getServer().getPort()))
                    .toJSON();
            plugin.pool.getResource().publish("ayrie:servers", json);
            System.out.println("done");
            pool.close();
        }
        // Plugin shutdown logic
    }


    public GameManager gameManager() {
        return gameManager;
    }

    public ProtocolManager getManager() {
        return manager;
    }

}
