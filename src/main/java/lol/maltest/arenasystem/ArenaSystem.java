package lol.maltest.arenasystem;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lol.maltest.arenasystem.commands.AdminCommand;
import lol.maltest.arenasystem.commands.MakeVoid;
import lol.maltest.arenasystem.commands.PasteSchematic;
import lol.maltest.arenasystem.commands.TestPaste;
import lol.maltest.arenasystem.impl.PlayerManager;
import lol.maltest.arenasystem.impl.PlayerObject;
import lol.maltest.arenasystem.impl.sql.MySQL;
import lol.maltest.arenasystem.impl.sql.SQLMethods;
import lol.maltest.arenasystem.listeners.MainListener;
import lol.maltest.arenasystem.redis.MessageAction;
import lol.maltest.arenasystem.redis.RedisManager;
import lol.maltest.arenasystem.redis.RedisMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public final class ArenaSystem extends JavaPlugin {

    private ArenaSystem plugin;
    private GameManager gameManager;

    private PlayerManager playerManager;
    public RedisManager redisManager;
    ProtocolManager manager;
    private boolean j = true;

    public MySQL sql;
    public SQLMethods data;

    public HashMap<UUID, PlayerObject> playerData = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        if(j) {
            this.redisManager = new RedisManager(this);
            redisManager.subscribe();
        }

        this.gameManager = new GameManager(this);
        this.playerManager = new PlayerManager(this);

        this.sql = new MySQL();
        this.data = new SQLMethods(this);
        try {
            sql.connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        if(sql.isConnected()) {
            System.out.println("[MiniGames] connected to database.");
            data.createTable();
        }


        manager = ProtocolLibrary.getProtocolManager();

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MainListener(this), this);

        getCommand("testpaste").setExecutor(new TestPaste(this));
        getCommand("makevoid").setExecutor(new MakeVoid());
        getCommand("admin").setExecutor(new AdminCommand(this));
        getCommand("pasteschematic").setExecutor(new PasteSchematic());

        if(j) {
            System.out.println("registering game with proxy");
            String json = new RedisMessage(MessageAction.ADD_SERVER)
                    .setParam("server", "game")
                    .setParam("port", String.valueOf(getServer().getPort()))
                    .toJSON();
            plugin.redisManager.publish("ayrie:servers", json);
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
            plugin.redisManager.publish("ayrie:servers", json);
            System.out.println("done");
            plugin.redisManager.subscriber.close();
        }
        try {
            sql.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Plugin shutdown logic
    }

    public HashMap<UUID, PlayerObject> getPlayerData() {
        return playerData;
    }

    public GameManager gameManager() {
        return gameManager;
    }

    public ProtocolManager getManager() {
        return manager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
