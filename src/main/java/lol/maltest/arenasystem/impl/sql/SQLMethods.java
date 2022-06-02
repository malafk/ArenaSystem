package lol.maltest.arenasystem.impl.sql;

import lol.maltest.arenasystem.ArenaSystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLMethods {

    private ArenaSystem plugin;

    public SQLMethods(ArenaSystem plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = plugin.sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Minigames (UUID VARCHAR(100), XPDATA MEDIUMTEXT, WINS MEDIUMTEXT, KILLS MEDIUMTEXT, PLAYED MEDIUMTEXT)");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try {
            PreparedStatement ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM Minigames WHERE UUID=?");
            ps.setString(1, uuid.toString());

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
