package lol.maltest.arenasystem.templates;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Kit {

    public HashMap<ItemStack, Integer> contents = new HashMap<>();
    public ArrayList<ItemStack> armor = new ArrayList<>();

    public void giveKit(Player player) {
        // contents
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        for (Map.Entry<ItemStack, Integer> entry : contents.entrySet()) {
            ItemStack key = entry.getKey();
            Integer value = entry.getValue();
            player.getInventory().setItem(value, key);
        }
        // armor
        for(ItemStack item : armor) {
            switch (item.getType().toString().toLowerCase()) {
                case "chestplate":
                    player.getInventory().setChestplate(item);
                    break;
                case "helmet":
                    player.getInventory().setHelmet(item);
                    break;
                case "boots":
                    player.getInventory().setBoots(item);
                    break;
                case "leggings":
                    player.getInventory().setLeggings(item);
                    break;
            }
        }
        ItemStack[] armorArray = armor.toArray(new ItemStack[0]);
        player.getInventory().setArmorContents(armorArray);
    }

    public void addContent(ItemStack item, Integer slot) {
        System.out.println(item.getType().toString() + " " + slot);
        contents.put(item, slot);
    }

    public void addArmor(ItemStack item) {
        armor.add(item);
    }
}
