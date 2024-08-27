package me.voper.leaguesvp.storage;

import me.voper.leaguesvp.data.LVPClan;

import java.util.Optional;
import java.util.Set;

public interface DbOperations {

    void initSchema();

    void insertClan(LVPClan lvpClan);

    Optional<LVPClan> getClan(String tag);

    Set<LVPClan> getAllClans();

    default void updateClan(LVPClan clan) {
        updatePoints(clan.getTag(), clan.getPoints());
    }

    void updatePoints(String tag, int points);

    void deleteClan(String tag);

    void deleteAllClans();

    DbConnection getDbConnection();

}
