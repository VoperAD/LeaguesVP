package me.voper.leaguesvp.listeners;

import me.voper.leaguesvp.LeaguesVP;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RemoveClans implements Listener {

    private final LeaguesVP plugin;

    public RemoveClans(LeaguesVP plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClanDisband(DisbandClanEvent e) {
        plugin.getStorageManager().deleteLvpClanByTag(e.getClan().getTag());
    }

}
