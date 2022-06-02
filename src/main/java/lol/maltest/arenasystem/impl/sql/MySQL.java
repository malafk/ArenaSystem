package lol.maltest.arenasystem.impl.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private final String host = "localhost";
    private final String port = "3306";
    private final String database = "";
    private final String username = "";
    private final String password = "";

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if(!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&maxReconnects=10", username, password);
        } else {
            System.out.println("Already connected to database.");
        }
    }

    public void disconnect() throws SQLException {
        if(isConnected()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
