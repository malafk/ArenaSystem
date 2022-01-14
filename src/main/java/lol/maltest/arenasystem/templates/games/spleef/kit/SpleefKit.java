package lol.maltest.arenasystem.templates.games.spleef.kit;

import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class SpleefKit extends Kit {

    public SpleefKit() {
        // add contents....
        addContent(new ItemBuilder(Material.DIAMOND_SPADE).setDisplayName(ChatUtil.clr("&7Shovel")).addEnchant(Enchantment.DIG_SPEED, 5).build(), 0);
        addArmor(new ItemStack(Material.LEATHER_BOOTS));
        addArmor(new ItemStack(Material.LEATHER_LEGGINGS));
        addArmor(new ItemStack(Material.LEATHER_CHESTPLATE));
        addArmor(new ItemStack(Material.LEATHER_HELMET));

    }
}
