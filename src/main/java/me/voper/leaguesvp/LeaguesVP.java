package me.voper.leaguesvp;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import lombok.Getter;
import me.voper.leaguesvp.commands.LVCommandsManager;
import me.voper.leaguesvp.hooks.papi.LVExpansion;
import me.voper.leaguesvp.listeners.RegisterClans;
import me.voper.leaguesvp.listeners.RemoveClans;
import me.voper.leaguesvp.managers.SettingsManager;
import me.voper.leaguesvp.managers.StorageManager;
import me.voper.leaguesvp.tasks.SaveDataTaskVP;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.PERFORMANCE_SAVE_PERIODICALLY;

public final class LeaguesVP extends JavaPlugin {

    private static final int SPIGOT_RESOURCE_ID = 110922;
    private static LeaguesVP instance;
    @Getter private ClanManager clanManager;
    @Getter private SettingsManager settingsManager;
    @Getter private StorageManager storageManager;

    @Override
    public void onEnable() {
        instance = this;

        new UpdateChecker(this, UpdateCheckSource.SPIGET, String.valueOf(SPIGOT_RESOURCE_ID))
                .setDownloadLink(SPIGOT_RESOURCE_ID)
                .setDonationLink("https://ko-fi.com/voper")
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .setChangelogLink(SPIGOT_RESOURCE_ID)
                .setNotifyOpsOnJoin(true)
                .checkEveryXHours(24)
                .checkNow();

        this.clanManager = SimpleClans.getInstance().getClanManager();
        this.settingsManager = new SettingsManager(this);
        // dataManager = new GsonManager(this);
        this.storageManager = new StorageManager(this);
        new LVExpansion(this);
        new LVCommandsManager(this);
        this.registerTasks();
        this.registerEvents();
        this.logStatus();
        this.startMetrics();
    }
    
    @Override
    public void onDisable() {
        if (SimpleClans.getInstance().getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
            this.storageManager.saveModified();
        }

        this.storageManager.closeConnection();
    }

    private void startMetrics() {
        Metrics metrics = new Metrics(this, 23182);
        metrics.addCustomChart(new SimplePie("auto_load", () -> settingsManager.getBoolean(SettingsManager.ConfigField.AUTO_LOAD_CLANS) ? "on" : "off"));
    }

    private void logStatus() {
        this.getLogger().info("===================================================");
        this.getLogger().info("=                    LeaguesVP                    =");
        this.getLogger().info("=                    by Voper                     =");
        this.getLogger().info("=                Discord -> _voper                =");
        this.getLogger().info("===================================================");
    }

    private void registerEvents() {
        new RegisterClans(this);
        new RemoveClans(this);
    }

    private void registerTasks() {
        if (SimpleClans.getInstance().getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
            new SaveDataTaskVP(this);
        }
    }

    public void debug(Level level, String message) {
        if (settingsManager.getBoolean(SettingsManager.ConfigField.DEBUG_MODE)) {
            getLogger().log(level, message);
        }
    }

    public void debug(String message) {
        this.debug(Level.INFO, message);
    }

    public static LeaguesVP getInstance() {
        return instance;
    }

}
