package me.voper.leaguesvp.storage.sqlite;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.storage.SqlDbOperations;

import java.sql.Connection;

public class SQLiteOperations extends SqlDbOperations {

    public SQLiteOperations() {
        super(LeaguesVP.getInstance(), new SQLiteConnection());
    }

    @Override
    public void initSchema() {
        String table = null;
        try (Connection c = connection.getConnection()) {
            table = "lvp_clans";
            logger.info("Creating table " + table);
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS lvp_clans (tag TEXT PRIMARY KEY, points INTEGER)");
        } catch (Exception e) {
            logger.severe("Failed while creating table `%s`: %s".formatted(table, e.getMessage()));
        }
    }

}
