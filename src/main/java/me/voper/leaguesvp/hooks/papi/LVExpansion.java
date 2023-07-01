package me.voper.leaguesvp.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.ClanData;
import me.voper.leaguesvp.data.GsonManager;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LVExpansion extends PlaceholderExpansion {

    private static final GsonManager gsonManager = LeaguesVP.getDataManager();
    private final LeaguesVP plugin;
    private final ClanManager clanManager;

    public LVExpansion(LeaguesVP plugin) {
        this.plugin = plugin;
        this.clanManager = this.plugin.getClanManager();
        this.register();
    }

    @Override
    public @NotNull String getName() {
        return this.plugin.getName();
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        ClanPlayer cp;
        ClanData clanData;
        if (player == null) {
            return "";
        }

        cp = clanManager.getClanPlayer(player);
        clanData = cp != null ? gsonManager.findClan(cp.getClan()) : null;

        if (params.equals("cpoints")) {
            return clanData != null ? String.valueOf(clanData.getPoints()) : "0";
        }

        if (params.equals("top_position")) {
            List<ClanData> topClans = gsonManager.getClanTop();
            return topClans.contains(clanData) ? String.valueOf(topClans.indexOf(clanData) + 1) : "0";
        }

        return null;
    }
}
