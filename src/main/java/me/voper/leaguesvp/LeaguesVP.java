package me.voper.leaguesvp;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import lombok.Getter;
import me.voper.leaguesvp.commands.LVCommandsManager;
import me.voper.leaguesvp.data.GsonManager;
import me.voper.leaguesvp.hooks.papi.LVExpansion;
import me.voper.leaguesvp.listeners.RegisterClans;
import me.voper.leaguesvp.listeners.RemoveClans;
import me.voper.leaguesvp.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LeaguesVP extends JavaPlugin {

    private static final int SPIGOT_RESOURCE_ID = 110922;
    private static LeaguesVP instance;
    private static GsonManager dataManager;
    @Getter private ClanManager clanManager;
    @Getter private SettingsManager settingsManager;

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
        dataManager = new GsonManager(this);
        new LVExpansion(this);
        new LVCommandsManager(this);
        this.registerEvents();
        this.logStatus();
    }
    
    @Override
    public void onDisable() {
        getDataManager().save();
    }

    private void logStatus() {
        this.getLogger().info("===================================================");
        this.getLogger().info("=                    LeaguesVP                    =");
        this.getLogger().info("=                    by Voper                     =");
        this.getLogger().info("=                Discord -> _voper                =");
        this.getLogger().info("===================================================");
    }

    private void registerEvents() {
        new RegisterClans();
        new RemoveClans();
    }

    public static LeaguesVP getInstance() {
        return instance;
    }

    public static GsonManager getDataManager() { return dataManager; }

}
