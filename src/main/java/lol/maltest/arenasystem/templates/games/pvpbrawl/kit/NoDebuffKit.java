package lol.maltest.arenasystem.templates.games.pvpbrawl.kit;

import lol.maltest.arenasystem.templates.Kit;
import lol.maltest.arenasystem.util.ChatUtil;
import lol.maltest.arenasystem.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class NoDebuffKit extends Kit {

    public NoDebuffKit() {
        addContent(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(ChatUtil.clr("&7Sword")).addEnchant(Enchantment.DAMAGE_ALL, 3).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.FIRE_ASPECT, 2).build(), 0);
        addContent(new ItemBuilder(Material.ENDER_PEARL).setAmount(16).build(), 1);
        addContent(new ItemBuilder(Material.COOKED_BEEF).setAmount(64).build(), 8);
        addContent(potionBuilder(PotionType.SPEED, 2, "Potion of Swiftness", false, ""), 2);
        addContent(potionBuilder(PotionType.SPEED, 2, "Potion of Swiftness", false, ""), 35);
        addContent(potionBuilder(PotionType.SPEED, 2, "Potion of Swiftness", false, ""), 26);
        addContent(potionBuilder(PotionType.SPEED, 2, "Potion of Swiftness", false, ""), 17);
        addContent(potionBuilder(PotionType.FIRE_RESISTANCE, 1, "Potion of Fire Resistance", false, ""), 3);
        for(int i =1; i < 28; i++) {
            System.out.println("added a poyion");
            addContent(potionBuilder(PotionType.INSTANT_HEAL, 2, "Splash Potion of Healing", true, ""));
        }
        addArmor(new ItemStack(Material.DIAMOND_BOOTS));
        addArmor(new ItemStack(Material.DIAMOND_LEGGINGS));
        addArmor(new ItemStack(Material.DIAMOND_CHESTPLATE));
        addArmor(new ItemStack(Material.DIAMOND_HELMET));
    }

    public static ItemStack potionBuilder(PotionType type, int level, String name, boolean splashable, String... lore){

        Potion potion = new Potion(type, level);
        potion.setSplash(splashable);

        ItemStack item = potion.toItemStack(1);


        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtil.clr(name));
        meta.setLore(Arrays.asList(lore));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        item.setItemMeta(meta);

        return item;
    }
}
