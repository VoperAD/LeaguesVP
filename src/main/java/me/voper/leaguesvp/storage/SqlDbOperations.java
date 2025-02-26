package me.voper.leaguesvp.storage;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.LVPClan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public abstract class SqlDbOperations implements DbOperations {

    protected DbConnection connection;
    protected LeaguesVP plugin;
    protected Logger logger;

    public SqlDbOperations(LeaguesVP plugin, DbConnection connection) {
        this.connection = connection;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @Override
    public void insertClan(LVPClan lvpClan) {
        try (Connection c = connection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO lvp_clans (tag, points) VALUES (?, ?)");
            ps.setString(1, lvpClan.getTag());
            ps.setInt(2, lvpClan.getPoints());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed while inserting clan `%s`: %s".formatted(lvpClan.getTag(), e.getMessage()));
        }
    }

    @Override
    public Optional<LVPClan> getClan(String tag) {
        try (Connection c = connection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM lvp_clans WHERE tag = ?");
            ps.setString(1, tag);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String rsTag = rs.getString("tag");
                int points = rs.getInt("points");
                return Optional.of(new LVPClan(rsTag, points));
            }
        } catch (SQLException e) {
            logger.severe("Failed while getting clan `%s`: %s".formatted(tag, e.getMessage()));
        }

        return Optional.empty();
    }

    @Override
    public Set<LVPClan> getAllClans() {
        Set<LVPClan> clans = new HashSet<>();
        try (Connection c = connection.getConnection()) {
            var rs = c.createStatement().executeQuery("SELECT * FROM lvp_clans");
            while (rs.next()) {
                String tag = rs.getString("tag");
                int points = rs.getInt("points");
                clans.add(new LVPClan(tag, points));
            }
        } catch (SQLException e) {
            logger.severe("Failed while getting all clans: %s".formatted(e.getMessage()));
        }

        return clans;
    }

    @Override
    public void updatePoints(String tag, int points) {
        try (Connection c = connection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE lvp_clans SET points = ? WHERE tag = ?");
            ps.setInt(1, points);
            ps.setString(2, tag);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed while updating points for clan `%s`: %s".formatted(tag, e.getMessage()));
        }
    }

    @Override
    public void deleteClan(String tag) {
        try (Connection c = connection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM lvp_clans WHERE tag = ?");
            ps.setString(1, tag);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed while deleting clan `%s`: %s".formatted(tag, e.getMessage()));
        }
    }

    @Override
    public void deleteAllClans() {
        try (Connection c = connection.getConnection()) {
            c.createStatement().execute("DELETE FROM lvp_clans");
        } catch (SQLException e) {
            logger.severe("Failed while deleting all clans: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public DbConnection getDbConnection() {
        return this.connection;
    }

}
