package me.voper.leaguesvp.managers;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.LVPClan;
import me.voper.leaguesvp.storage.DbOperations;
import me.voper.leaguesvp.storage.DbOperationsFactory;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StorageManager {

    private final LeaguesVP plugin;
    private final DbOperations dbOperations;

    private final HashMap<String, LVPClan> lvpClansByTag = new HashMap<>();
    private final List<LVPClan> sortedClans = new ArrayList<>();
    private final Set<LVPClan> modifiedClans = new HashSet<>();

    public StorageManager(LeaguesVP plugin) {
        this.plugin = plugin;
        this.dbOperations = DbOperationsFactory.createDatabaseOperations();
        this.dbOperations.initSchema();
        for (LVPClan clan : dbOperations.getAllClans()) {
            lvpClansByTag.put(clan.getTag(), clan);
        }
        this.sortedClans.addAll(lvpClansByTag.values());
        this.synchronizeWithSimpleClansDb();
    }

    public List<LVPClan> getClansSorted() {
        sortedClans.sort(Comparator.reverseOrder());
        return this.sortedClans;
    }

    private void synchronizeWithSimpleClansDb() {
        this.plugin.debug("Synchronizing database with SimpleClans...");
        ClanManager clanManager = SimpleClans.getInstance().getClanManager();
        Set<String> tags = clanManager.getClans().stream()
                .map(Clan::getTag)
                .collect(Collectors.toSet());

        for (String tag: tags) {
            if (!lvpClansByTag.containsKey(tag)) {
                LVPClan lvpClan = new LVPClan(tag);
                this.insertLvpClan(lvpClan);
            }
        }

        for (String tag : new HashSet<>(lvpClansByTag.keySet())) {
            if (!tags.contains(tag)) {
                this.deleteLvpClanByTag(tag);
            }
        }
    }

    public void applyToAllClans(Consumer<LVPClan> lvpClanConsumer) {
        this.lvpClansByTag.values().forEach(lvpClanConsumer);
        this.updateAll();
    }

    public Optional<LVPClan> getLvpClanByTag(String tag) {
        return Optional.ofNullable(lvpClansByTag.get(tag));
    }

    public void insertLvpClan(LVPClan clan) {
        this.plugin.debug("Inserting clan %s into the database".formatted(clan.getTag()));
        dbOperations.insertClan(clan);
        lvpClansByTag.put(clan.getTag(), clan);
        sortedClans.add(clan);
    }

    public void updateLvpClan(LVPClan clan) {
        this.plugin.debug("Updating clan %s".formatted(clan.getTag()));

        if (SimpleClans.getInstance().getSettingsManager().is(SettingsManager.ConfigField.PERFORMANCE_SAVE_PERIODICALLY)) {
            this.plugin.debug("Adding %s to modifiedClans set...".formatted(clan.getTag()));
            boolean added = this.modifiedClans.add(clan);
            this.plugin.debug("Was it already in the set? -> %s".formatted(!added));
            this.plugin.debug("Modified clans content: " + modifiedClans);
            return;
        }

        dbOperations.updateClan(clan);
    }

    public void updateAll() {
        this.lvpClansByTag.values().forEach(this::updateLvpClan);
    }

    public void saveModified() {
        this.plugin.debug("Saving modified clans");

        Iterator<LVPClan> iterator = modifiedClans.iterator();
        while (iterator.hasNext()) {
            LVPClan next = iterator.next();
            iterator.remove();
            dbOperations.updateClan(next);
        }
    }

    public void deleteLvpClanByTag(String tag) {
        this.plugin.debug("Deleting clan %s from the database".formatted(tag));

        dbOperations.deleteClan(tag);
        LVPClan removed = lvpClansByTag.remove(tag);

        if (removed != null) {
            sortedClans.remove(removed);
        }
    }

    public void closeConnection() {
        this.dbOperations.getDbConnection().close();
    }

}
