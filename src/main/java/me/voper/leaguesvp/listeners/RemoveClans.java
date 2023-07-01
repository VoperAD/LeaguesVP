package me.voper.leaguesvp.listeners;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.GsonManager;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RemoveClans implements Listener {

    private final GsonManager gsonManager;

    public RemoveClans() {
        LeaguesVP ins = LeaguesVP.getInstance();
        this.gsonManager = LeaguesVP.getDataManager();
        Bukkit.getPluginManager().registerEvents(this, ins);
    }

    @EventHandler
    public void onClanDisband(DisbandClanEvent e) {
        gsonManager.unregisterClan(e.getClan());
    }

}
