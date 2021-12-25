package lol.maltest.arenasystem;

import com.sk89q.worldedit.EditSession;
import lol.maltest.arenasystem.arena.ArenaInstance;
import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.commands.PasteSchematic;
import lol.maltest.arenasystem.commands.TestPaste;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class ArenaSystem extends JavaPlugin {

    private ArenaSystem plugin;
    private ArenaManager arenaManager;

    private HashMap<EditSession, ArenaInstance> games = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getCommand("testpaste").setExecutor(new TestPaste(plugin));
        getCommand("pasteschem").setExecutor(new PasteSchematic());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public HashMap<EditSession, ArenaInstance> getGames() {
        return games;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}
