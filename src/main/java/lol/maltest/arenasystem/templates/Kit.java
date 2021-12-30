package lol.maltest.arenasystem.templates;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Kit {

    public HashMap<ItemStack, Integer> contents;
    public ArrayList<ItemStack> armor;

    public Kit(HashMap<ItemStack, Integer> contents, ArrayList<ItemStack> armor) {
        this.contents = contents;
        this.armor = armor;
    }

    public void giveKit(Player player) {
        // contents
        for(ItemStack item : contents.keySet()) {
            for(int slot : contents.values()) {
                player.getInventory().setItem(slot, item);
            }
        }
        // armor
        ItemStack[] armorArray = armor.toArray(new ItemStack[0]);
        player.getInventory().setArmorContents(armorArray);
    }

    public Integer addContent(ItemStack item, Integer slot) {
        return contents.put(item, slot);
    }

    public void addArmor(ItemStack item) {
        armor.add(item);
    }
}
