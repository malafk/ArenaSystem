package lol.maltest.arenasystem.templates.games.stickfight.kit;

import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class StickFightKit extends Kit {

    public StickFightKit() {
        // add contents....
        addContent(new ItemBuilder(Material.STICK).setDisplayName(ChatUtil.clr("&7Knockback Stick")).addEnchant(Enchantment.KNOCKBACK, 1).build(), 0);
        addContent(new ItemBuilder(Material.SHEARS).setDisplayName(ChatUtil.clr("&7Shears")).setUnbreakable(true).build(), 1);
        addContent(new ItemBuilder(Material.WOOL).setAmount(16).build(), 4);
        addArmor(new ItemStack(Material.LEATHER_BOOTS));
        addArmor(new ItemStack(Material.LEATHER_LEGGINGS));
        addArmor(new ItemStack(Material.LEATHER_CHESTPLATE));
        addArmor(new ItemStack(Material.LEATHER_HELMET));

    }

}
