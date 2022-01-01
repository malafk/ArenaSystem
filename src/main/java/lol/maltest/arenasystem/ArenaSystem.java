package lol.maltest.arenasystem;

import com.sk89q.worldedit.EditSession;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.commands.MakeVoid;
import lol.maltest.arenasystem.commands.PasteSchematic;
import lol.maltest.arenasystem.commands.TestPaste;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class ArenaSystem extends JavaPlugin {

    private ArenaSystem plugin;
    private GameManager gameManager;

    private HashMap<EditSession, ArenaInstance> games = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        this.gameManager = new GameManager(this);

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveDefaultConfig();

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

}
