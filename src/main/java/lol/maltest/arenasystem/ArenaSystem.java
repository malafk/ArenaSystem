package lol.maltest.arenasystem;

import lol.maltest.arenasystem.commands.TestPaste;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArenaSystem extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("testpaste").setExecutor(new TestPaste());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
