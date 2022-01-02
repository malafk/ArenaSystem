package lol.maltest.arenasystem;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldedit.EditSession;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.commands.MakeVoid;
import lol.maltest.arenasystem.commands.PasteSchematic;
import lol.maltest.arenasystem.commands.TestPaste;
import lol.maltest.arenasystem.listeners.MainListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class ArenaSystem extends JavaPlugin {

    private ArenaSystem plugin;
    private GameManager gameManager;

    private HashMap<EditSession, ArenaInstance> games = new HashMap<>();

    ProtocolManager manager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        this.gameManager = new GameManager(this);

        manager = ProtocolLibrary.getProtocolManager();

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MainListener(), this);

        getCommand("testpaste").setExecutor(new TestPaste(this));
        getCommand("makevoid").setExecutor(new MakeVoid());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public HashMap<EditSession, ArenaInstance> getGames() {
        return games;
    }

    public GameManager gameManager() {
        return gameManager;
    }

    public ProtocolManager getManager() {
        return manager;
    }

}
