package lol.maltest.arenasystem.templates.games.stickfight.kit;

import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class StickFightKit extends Kit {

    private static HashMap<ItemStack, Integer> contents = new HashMap<>();
    private static ArrayList<ItemStack> armor = new ArrayList<>();

    private ArenaManager arenaManager;

    public StickFightKit(ArenaManager arenaManager) {
        super(contents, armor);
        this.arenaManager = arenaManager;
    }
}
