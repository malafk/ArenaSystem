package lol.maltest.arenasystem.templates.games.pvpbrawl.kit;

import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class OldKit extends Kit {

    public OldKit() {
        addContent(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatUtil.clr("&7Sword")).build(), 0);
        addArmor(new ItemStack(Material.DIAMOND_BOOTS));
        addArmor(new ItemStack(Material.DIAMOND_LEGGINGS));
        addArmor(new ItemStack(Material.DIAMOND_CHESTPLATE));
        addArmor(new ItemStack(Material.DIAMOND_HELMET));
    }
}
