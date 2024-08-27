package me.voper.leaguesvp.listeners;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.LVPClan;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegisterClans implements Listener {

    private final LeaguesVP plugin;

    public RegisterClans(LeaguesVP plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClanCreate(CreateClanEvent e) {
        LVPClan lvpClan = new LVPClan(e.getClan());
        plugin.getStorageManager().insertLvpClan(lvpClan);
    }

}
