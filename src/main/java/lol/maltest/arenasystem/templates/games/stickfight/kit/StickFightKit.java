package lol.maltest.arenasystem.templates.games.stickfight.kit;

import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

        // add contents....
        addContent(new ItemBuilder(Material.STICK).setDisplayName(ChatUtil.clr("&7Knockback Stick")).addEnchant(Enchantment.KNOCKBACK, 1).build(), 0);
        addContent(new ItemBuilder(Material.SHEARS).setDisplayName(ChatUtil.clr("&7Shears")).setUnbreakable(true).build(), 1);
        addContent(new ItemBuilder(Material.WOOL, (byte) 5).setAmount(16).build(), 4);
    }
}
