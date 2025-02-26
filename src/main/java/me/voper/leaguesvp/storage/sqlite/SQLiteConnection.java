package me.voper.leaguesvp.storage.sqlite;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.storage.DbConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLiteConnection implements DbConnection {

    private final LeaguesVP plugin = LeaguesVP.getInstance();

    private Connection connection;
    private final Logger logger;
    private final String dbPath;
    private File file;

    public SQLiteConnection() {
        this.logger = plugin.getLogger();
        this.dbPath = plugin.getDataFolder().getAbsolutePath();
        this.initConnection();
    }

    @Override
    public void initConnection() {
        if (this.file == null) {
            File pluginFolder = plugin.getDataFolder();
            if (!pluginFolder.exists() && !pluginFolder.mkdirs()) {
                logger.severe("Failed to create plugin folder");
                return;
            }

            this.file = new File(this.dbPath, "leaguesvp.db");
        }

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
        } catch (ClassNotFoundException ex) {
            logger.severe("Failed to load SQLite driver: " + ex.getMessage());
        } catch (SQLException e) {
            logger.severe("Failed to connect to SQLite database: " + e.getMessage());
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
            logger.severe("Failed to close SQLite connection: " + e.getMessage());
        }
    }

}
