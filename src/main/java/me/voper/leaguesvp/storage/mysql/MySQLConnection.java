package me.voper.leaguesvp.storage.mysql;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.managers.SettingsManager;
import me.voper.leaguesvp.storage.DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import static me.voper.leaguesvp.managers.SettingsManager.ConfigField.*;

public class MySQLConnection implements DbConnection {

    private final LeaguesVP plugin = LeaguesVP.getInstance();

    private Connection connection;
    private final Logger logger;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQLConnection() {
        SettingsManager sm = plugin.getSettingsManager();
        this.logger = plugin.getLogger();

        this.host = sm.getString(MYSQL_HOST);
        this.port = sm.getInt(MYSQL_PORT);
        this.database = sm.getString(MYSQL_DATABASE);
        this.username = sm.getString(MYSQL_USERNAME);
        this.password = sm.getString(MYSQL_PASSWORD);
    }

    @Override
    public void initConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        } catch (ClassNotFoundException ex) {
            logger.severe("Failed to load MySQL driver: " + ex.getMessage());
        } catch (SQLException e) {
            logger.severe("Failed to connect to MySQL database: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed() || !this.connection.isValid(0)) {
                this.initConnection();
            }
        } catch (SQLException e) {
            this.initConnection();
        }

        return this.connection;
    }

    @Override
    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            logger.severe("Failed to close MySQL connection: " + e.getMessage());
        }
    }

}
