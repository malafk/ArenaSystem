package lol.maltest.arenasystem;

import lol.maltest.arenasystem.commands.TestPaste;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArenaSystem extends JavaPlugin {

    private ArenaSystem plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getCommand("testpaste").setExecutor(new TestPaste(plugin));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
