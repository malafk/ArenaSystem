package lol.maltest.arenasystem.templates;

import lol.maltest.arenasystem.util.ChatUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Kit {

    public HashMap<Integer, ItemStack> contents = new HashMap<>();
    public ArrayList<ItemStack> armor = new ArrayList<>();

    public void giveKit(Player player) {
        // contents
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        for (Map.Entry<Integer, ItemStack> entry : contents.entrySet()) {
            Integer key = entry.getKey();
            ItemStack value = entry.getValue();
            System.out.println(player.getScoreboard().getPlayerTeam(player).getName());
            if (player.getScoreboard().getPlayerTeam(player).getName().equals("&c&lRed")) {
                setArmorColor(Color.RED);
            } else {
                setArmorColor(Color.BLUE);
            }
            if (key >= 10000) {
                player.getInventory().addItem(value);
            } else {
                player.getInventory().setItem(key, value);
            }
            if (value.getType().equals(Material.WOOL)) {
                ItemStack keyCloned = value.clone();
                if (player.getScoreboard().getPlayerTeam(player).getName().equals("&c&lRed")) {
                    keyCloned.setDurability((short) 14);
                    player.getInventory().setItem(key, keyCloned);
                } else {
                    keyCloned.setDurability((short) 3);
                    player.getInventory().setItem(key, keyCloned);
                }
            }
        }
        // armor
//        for(ItemStack item : armor) {
//            switch (item.getType().toString().toLowerCase()) {
//                case "chestplate":
//                    player.getInventory().setChestplate(item);
//                    break;
//                case "helmet":
//                    player.getInventory().setHelmet(item);
//                    break;
//                case "boots":
//                    player.getInventory().setBoots(item);
//                    break;
//                case "leggings":
//                    player.getInventory().setLeggings(item);
//                    break;
//            }
//        }
        player.getInventory().setArmorContents(armor.toArray(new ItemStack[0]));
    }

    public void addContent(ItemStack item, Integer slot) {
        contents.put(slot, item);
    }

    int i = 1;
    public void addContent(ItemStack item) {
        contents.put(10000 + i, item);
        i++;
    }

    public void addArmor(ItemStack item) {
        armor.add(item);
    }

    public void setArmorColor(Color color) {
        for(ItemStack item : armor) {
            if (item.getType().toString().contains("LEATHER")) {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                meta.setColor(color);
                item.setItemMeta(meta);
            }
        }
    }
}
