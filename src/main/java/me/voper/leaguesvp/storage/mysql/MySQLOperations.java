package me.voper.leaguesvp.storage.mysql;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.storage.SqlDbOperations;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLOperations extends SqlDbOperations {

    public MySQLOperations() {
        super(LeaguesVP.getInstance(), new MySQLConnection());
    }

    @Override
    public void initSchema() {
        String table = null;
        try (Connection c = connection.getConnection()) {
            table = "lvp_clans";
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS lvp_clans (tag VARCHAR(25) PRIMARY KEY, points INT NOT NULL)");
        } catch (SQLException e) {
            logger.severe("Failed while creating table `%s`: %s".formatted(table, e.getMessage()));
        }
    }

}
