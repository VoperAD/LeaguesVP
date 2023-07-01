package me.voper.leaguesvp.listeners;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.GsonManager;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RegisterClans implements Listener {

    private final GsonManager gsonManager;
    private final ClanManager clanManager;

    public RegisterClans() {
        LeaguesVP ins = LeaguesVP.getInstance();
        this.clanManager = ins.getClanManager();
        this.gsonManager = LeaguesVP.getDataManager();
        Bukkit.getPluginManager().registerEvents(this, ins);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Clan clan = clanManager.getClanByPlayerUniqueId(p.getUniqueId());
        if (clan != null) {
            gsonManager.registerClan(clan);
        }
    }

    @EventHandler
    public void onClanCreate(CreateClanEvent e) {
        gsonManager.registerClan(e.getClan());
    }

}
