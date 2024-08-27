package me.voper.leaguesvp.storage;

import java.sql.Connection;

public interface DbConnection {

    void initConnection();

    Connection getConnection();

    void close();

}
