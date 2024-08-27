package me.voper.leaguesvp.tasks;

import me.voper.leaguesvp.LeaguesVP;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveDataTaskVP extends BukkitRunnable {

    private LeaguesVP plugin;

    public SaveDataTaskVP(LeaguesVP plugin) {
        this.plugin = plugin;
        this.start();
    }

    private void start() {
        long interval = SimpleClans.getInstance().getSettingsManager().getMinutes(SettingsManager.ConfigField.PERFORMANCE_SAVE_INTERVAL);
        this.runTaskTimerAsynchronously(plugin, interval, interval);
    }

    @Override
    public void run() {
        this.plugin.debug("%s: Running task...".formatted(this.getClass().getName()));
        this.plugin.getStorageManager().saveModified();
    }

}
