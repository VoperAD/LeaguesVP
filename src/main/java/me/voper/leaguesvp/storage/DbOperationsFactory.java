package me.voper.leaguesvp.storage;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.managers.SettingsManager;
import me.voper.leaguesvp.storage.mysql.MySQLOperations;
import me.voper.leaguesvp.storage.sqlite.SQLiteOperations;

/**
 * Factory class for creating the correct DatabaseOperations implementation based on the configuration.
 * It might be changed in the future to include more database types.
 */
public class DbOperationsFactory {

    public static DbOperations createDatabaseOperations() {
        boolean isMysqlEnabled = LeaguesVP.getInstance().getSettingsManager().getBoolean(SettingsManager.ConfigField.MYSQL_ENABLE);
        if (isMysqlEnabled) {
            return new MySQLOperations();
        } else {
            return new SQLiteOperations();
        }
    }

}
