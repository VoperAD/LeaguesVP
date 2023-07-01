package me.voper.leaguesvp.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class GsonManager {

    private final LeaguesVP plugin;
    private final ClanManager clanManager;
    private final SettingsManager sm;
    private final Gson gson;
    private File file;
    @Getter private List<ClanData> clanData;
    @Getter private List<ClanData> clanTop;

    public GsonManager(@NotNull LeaguesVP plugin) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.plugin = plugin;
        this.clanManager = plugin.getClanManager();
        this.sm = plugin.getSettingsManager();
        this.start();
    }

    public void start() {
        this.file = new File(plugin.getDataFolder().getAbsolutePath() + "/clansdata.json");
        try {
            if (file.createNewFile()) {
                clanData = new ArrayList<>();
            } else {
                FileReader reader = new FileReader(file);
                ClanData[] p = gson.fromJson(reader, ClanData[].class);
                if (sm.getBoolean(SettingsManager.ConfigField.AUTO_LOAD_CLANS)) {
                    clanData = p != null ? new ArrayList<>(Arrays.asList(p)) : new ArrayList<>();
                } else {
                    clanData = new ArrayList<>();
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        clanData.removeIf(c -> clanManager.getClan(c.getTag()) == null);
        
        clanManager.getClans()
                .stream()
                .filter(c -> this.findClan(c) == null)
                .map(ClanData::new)
                .forEach(clanData::add);

        updateClanTop(); 
    }

    public void save() {
        try (FileWriter writer = new FileWriter(this.file, false)) {
            gson.toJson(clanData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public @Nullable ClanData findClan(Clan clan) {
        for (ClanData data : clanData) {
            if (clan.getTag().equals(data.getTag())) {
                return data;
            }
        }
        return null;
    }

    public @Nullable ClanData findClanByTag(String tag) {
        for (ClanData data : clanData) {
            if (data.getTag().equals(tag)) {
                return data;
            }
        }
        return null;
    }

    public boolean isRegistered(@NotNull Clan clan) {
        return this.findClan(clan) != null;
    }

    public void registerClan(@NotNull Clan clan) {
        if (!isRegistered(clan)) {
            ClanData data = new ClanData(clan);
            getClanData().add(data);
            updateAndSave();
        } else {
            this.plugin.getLogger().info("The clan " + clan.getName() + " is already registered!");
        }
    }

    public void unregisterClan(@NotNull Clan clan) {
        ClanData data = this.findClan(clan);
        if (data != null) {
            getClanData().remove(data);
            updateAndSave();
        }
    }

    public void updateClanTop() {
        clanTop = clanData.isEmpty() ?
                new ArrayList<>() :
                clanData.stream()
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());
    }

    public void updateAndSave() {
        save();
        updateClanTop();
    }
}
