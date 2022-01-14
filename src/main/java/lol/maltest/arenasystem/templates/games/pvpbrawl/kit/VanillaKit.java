package lol.maltest.arenasystem.templates.games.pvpbrawl.kit;

import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class VanillaKit extends Kit {

    public VanillaKit() {
        // add contents....
        addContent(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatUtil.clr("&7Sword")).addEnchant(Enchantment.DURABILITY, 69).build(), 0);
        addContent(new ItemBuilder(Material.FISHING_ROD).setDisplayName(ChatUtil.clr("&7Rod")).build(), 1);
        addContent(new ItemBuilder(Material.FLINT_AND_STEEL).setDisplayName(ChatUtil.clr("&7Fire")).setDurability(59).build(), 8);
        addArmor(new ItemStack(Material.IRON_BOOTS));
        addArmor(new ItemStack(Material.IRON_LEGGINGS));
        addArmor(new ItemStack(Material.IRON_CHESTPLATE));
        addArmor(new ItemStack(Material.IRON_HELMET));
    }
}
