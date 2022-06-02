package lol.maltest.arenasystem.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lol.maltest.arenasystem.ArenaSystem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.*;

public class PlayerManager {

    private ArenaSystem plugin;

    public PlayerManager(ArenaSystem arenaSystem) {
        this.plugin = arenaSystem;
    }

    public void loadStats(Player player) {
        PlayerObject playerObject = null;
        Gson gson = new Gson();

        ArrayList<String> json = new ArrayList<>();
        if(plugin.data.playerExists(player.getUniqueId())) {
            try {
                PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM Minigames WHERE UUID=?");
                ps.setString(1, player.getUniqueId().toString());

                ResultSet rs = ps.executeQuery();
                rs.next();

                json.add(rs.getString("WINS").replace("{","").replace("}",""));
                json.add(rs.getString("KILLS").replace("{","").replace("}",""));
                json.add(rs.getString("PLAYED").replace("{","").replace("}",""));
                String json1 = "{" + String.join(",", json) + "}";
                playerObject = gson.fromJson(json1, PlayerObject.class);
                plugin.getPlayerData().put(player.getUniqueId(), playerObject);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            playerObject = new PlayerObject(plugin, player.getUniqueId());
            plugin.getPlayerData().put(player.getUniqueId(), playerObject);
        }
    }


    public void save(UUID playerUuid) {
        HashMap<String, JsonElement> xpData = new HashMap<>();
        HashMap<String, JsonElement> wins = new HashMap<>();
        HashMap<String, JsonElement> kills = new HashMap<>();
        HashMap<String, JsonElement> games = new HashMap<>();

        Gson gson = new Gson();
        System.out.println("saving");
        JsonElement object;
        HashMap<UUID, PlayerObject> hashMap = plugin.getPlayerData();
        object = gson.toJsonTree(hashMap.get(playerUuid));
        Set<Map.Entry<String, JsonElement>> entries = object.getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry: entries) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (key.contains("Wins")) wins.put(key, value);
            if (key.contains("Kills")) kills.put(key,value);
            if (key.contains("Games")) games.put(key,value );
            if (key.contains("Xp")) xpData.put(key,value );
        }
        String xpDataJson = gson.toJson(xpData);
        String winsDataJson = gson.toJson(wins);
        String killsDataJson = gson.toJson(kills);
        String gamesDataJson = gson.toJson(games);
        if(!plugin.data.playerExists(playerUuid)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement pss = plugin.sql.getConnection().prepareStatement("INSERT IGNORE INTO Minigames (UUID, XPDATA, WINS, KILLS, PLAYED) VALUES (?,?,?,?,?)");
                        pss.setString(1, playerUuid.toString());
                        pss.setString(2, xpDataJson);
                        pss.setString(3, winsDataJson);
                        pss.setString(4, killsDataJson);
                        pss.setString(5, gamesDataJson);
                        System.out.println("executing save");
                        pss.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(plugin);
            return;
        }
        try {
            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("UPDATE Minigames SET XPDATA = ?, WINS = ?, KILLS = ?, PLAYED = ? WHERE UUID = ?");
            ps.setString(1, xpDataJson);
            ps.setString(2, winsDataJson);
            ps.setString(3, killsDataJson);
            ps.setString(4, gamesDataJson);
            ps.setString(5, playerUuid.toString());
            System.out.println(17);
            System.out.println("Saving existing user");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Saved");
    }
}
