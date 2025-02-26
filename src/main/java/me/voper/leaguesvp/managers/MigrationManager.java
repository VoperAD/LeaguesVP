package me.voper.leaguesvp.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.LVPClan;

import java.io.File;
import java.io.FileReader;
import java.util.Optional;
import java.util.logging.Logger;

public final class MigrationManager {

    private final LeaguesVP plugin;
    private final Gson gson;
    private final Logger logger;
    private final StorageManager storageManager;
    private File file;

    public MigrationManager(LeaguesVP plugin) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.storageManager = plugin.getStorageManager();
    }

    public void migrateFromJsonToDb() {
        this.file = new File(plugin.getDataFolder().getAbsolutePath() + "/clansdata.json");
        if (file.exists()) {
            logger.info("%s/clansdata.json exists".formatted(plugin.getDataFolder().getName()));
            logger.info("Attempting migration to the new storage solution");

            if (migrateClansData()) {
                renameOldFile();
                return;
            }

            logger.severe("Migration failed. The file 'clansdata.json' was not renamed.");
        }
    }

    private void renameOldFile() {
        File renamed = new File(file.getParentFile(), "clansdata-migrated.json");
        if (file.renameTo(renamed)) {
            logger.info("%s/clansdata.json has been renamed to clansdata-migrated.json".formatted(plugin.getDataFolder().getName()));
            return;
        }

        logger.severe("Failed to rename %s/clansdata.json".formatted(plugin.getDataFolder().getName()));
    }

    private boolean migrateClansData() {
        return readClansDataJson().map(lvpClans -> {
            for (LVPClan lvpClan : lvpClans) {
                storageManager.getLvpClanByTag(lvpClan.getTag()).ifPresentOrElse(
                        storageManager::updateLvpClan,
                        () -> storageManager.insertLvpClan(lvpClan)
                );
            }

            return true;
        }).orElse(false);
    }

    private Optional<LVPClan[]> readClansDataJson() {
        try (FileReader reader = new FileReader(file)) {
            LVPClan[] lvpClans = gson.fromJson(reader, LVPClan[].class);
            if (lvpClans == null || lvpClans.length == 0) {
                logger.info("No clans found in %s/clansdata.json".formatted(plugin.getDataFolder().getName()));
                return Optional.empty();
            }

            return Optional.of(lvpClans);
        } catch (Exception e) {
            logger.severe("Failed to read clansdata.json: " + e.getMessage());
        }

        return Optional.empty();
    }

}
