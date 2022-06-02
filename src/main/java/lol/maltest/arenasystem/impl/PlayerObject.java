package lol.maltest.arenasystem.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lol.maltest.arenasystem.ArenaSystem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerObject {

    public ArenaSystem getPlugin() {
        return plugin;
    }

    private transient ArenaSystem plugin;

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    private UUID playerUuid;

    private int pkRaceGames = 0, pvpBrawlGames = 0, spleefGames = 0, stickfightGames = 0, tntRunGames = 0, totalGames = 0;
    private int pkRaceWins = 0, pvpBrawlWins = 0, spleefWins = 0, stickFightWins = 0, tntRunWins = 0, totalWins = 0;
    private int pvpBrawlKills = 0, stickFightKills = 0, totalKills = 0;
    private long playerXpAmount = 0;
    private long playerXpLevel = 0;

    HashMap<String, JsonElement> xpData = new HashMap<>();
    HashMap<String, JsonElement> wins = new HashMap<>();
    HashMap<String, JsonElement> kills = new HashMap<>();
    HashMap<String, JsonElement> games = new HashMap<>();

    public PlayerObject(ArenaSystem plugin, UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.plugin = plugin;
    }

//    public void save() {
//        System.out.println("saving");
//        JsonElement object = gson.toJsonTree(plugin.getPlayerData().get(playerUuid));
//        Set<Map.Entry<String, JsonElement>> entries = object.getAsJsonObject().entrySet();
//        for (Map.Entry<String, JsonElement> entry: entries) {
//            String key = entry.getKey();
//            JsonElement value = entry.getValue();
//            if (key.contains("Wins")) wins.put(key, value);
//            if (key.contains("Kills")) kills.put(key,value);
//            if (key.contains("Games")) games.put(key,value );
//            if (key.contains("Xp")) xpData.put(key,value );
//        }
//        String xpDataJson = gson.toJson(xpData);
//        String winsDataJson = gson.toJson(wins);
//        String killsDataJson = gson.toJson(kills);
//        String gamesDataJson = gson.toJson(games);
//
//        if(!plugin.data.playerExists(playerUuid)) {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    try {
//                        PreparedStatement pss = plugin.sql.getConnection().prepareStatement("INSERT IGNORE INTO Minigames (UUID, XPDATA, WINS, KILLS, PLAYED) VALUES (?,?,?,?,?)");
//                        pss.setString(1, playerUuid.toString());
//                        pss.setString(2, xpDataJson);
//                        pss.setString(3, winsDataJson);
//                        pss.setString(4, killsDataJson);
//                        pss.setString(5, gamesDataJson);
//                        System.out.println("executing save");
//                        pss.executeUpdate();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.runTaskAsynchronously(plugin);
//            return;
//        }
//        try {
//            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("UPDATE Minigames SET XPDATA= " + xpDataJson  + ", WINS=" + winsDataJson + ", KILLS=" + killsDataJson + ", PLAYED=" + gamesDataJson + " WHERE UUID=?");
//            ps.setString(1, playerUuid.toString());
//            System.out.println("executing upadte sace");
//            ps.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("Saved");
//    }

    public int getPkRaceWins() {
        return pkRaceWins;
    }

    public void addPkRaceWins(int pkRaceWins) {
        this.pkRaceWins += pkRaceWins;
    }

    public int getPvpBrawlWins() {
        return pvpBrawlWins;
    }

    public void addPvpBrawlWins(int pvpBrawlWins) {
        this.pvpBrawlWins += pvpBrawlWins;
    }

    public int getSpleefWins() {
        return spleefWins;
    }

    public void addSpleefWins(int spleefWins) {
        this.spleefWins = spleefWins;
    }

    public int getStickFightWins() {
        return stickFightWins;
    }

    public void addStickFightWins(int stickFightWins) {
        this.stickFightWins += stickFightWins;
    }

    public int getTntRunWins() {
        return tntRunWins;
    }

    public void addTntRunWins(int tntRunWins) {
        this.tntRunWins += tntRunWins;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void addTotalWins(int totalWins) {
        this.totalWins += totalWins;
    }

    public int getPvpBrawlKills() {
        return pvpBrawlKills;
    }

    public void addPvpBrawlKills(int pvpBrawlKills) {
        this.pvpBrawlKills += pvpBrawlKills;
    }

    public int getStickFightKills() {
        return stickFightKills;
    }

    public void addStickFightKills(int stickFightKills) {
        this.stickFightKills += stickFightKills;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void addTotalKills(int totalKills) {
        this.totalKills += totalKills;
    }

    public long getPlayerXp() {
        return playerXpAmount;
    }

    public void addPlayerXp(long playerXp) {
        this.playerXpAmount += playerXp;
    }

    public long getPlayerLevel() {
        return playerXpLevel;
    }

    public void addPlayerLevel(long playerLevel) {
        this.playerXpLevel += playerLevel;
    }

    public int getPkRaceGames() {
        return pkRaceGames;
    }

    public int getPvpBrawlGames() {
        return pvpBrawlGames;
    }

    public int getSpleefGames() {
        return spleefGames;
    }

    public int getStickfightGames() {
        return stickfightGames;
    }

    public int getTntRunGames() {
        return tntRunGames;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void addPkRaceGames(int pkRaceGames) {
        this.pkRaceGames += pkRaceGames;
    }

    public void addPvpBrawlGames(int pvpBrawlGames) {
        this.pvpBrawlGames += pvpBrawlGames;
    }

    public void addSpleefGames(int spleefGames) {
        this.spleefGames += spleefGames;
    }

    public void addStickfightGames(int stickfightGames) {
        this.stickfightGames += stickfightGames;
    }

    public void addTntRunGames(int tntRunGames) {
        this.tntRunGames += tntRunGames;
    }

    public void addTotalGames(int totalGames) {
        this.totalGames += totalGames;
    }

}
