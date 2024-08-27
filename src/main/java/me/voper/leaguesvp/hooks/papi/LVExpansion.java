package me.voper.leaguesvp.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.ClanData;
import me.voper.leaguesvp.data.GsonManager;
import me.voper.leaguesvp.data.LVPClan;
import me.voper.leaguesvp.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LVExpansion extends PlaceholderExpansion {

    private final LeaguesVP plugin;
    private final StorageManager storageManager;
    private final ClanManager clanManager;

    public LVExpansion(LeaguesVP plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
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
        if (player == null) {
            return "";
        }

        ClanPlayer cp = clanManager.getClanPlayer(player);
        List<LVPClan> clanTop = storageManager.getClansSorted();
        LVPClan lvpClan = null;

        if (cp != null) {
            Optional<LVPClan> clanOptional = storageManager.getLvpClanByTag(cp.getClan().getTag());
            lvpClan = clanOptional.orElse(null);
        }

        Pattern pattern;
        Matcher matcher;

        // %leaguesvp_cpoints%
        if (params.equals("cpoints")) {
            return lvpClan != null ? String.valueOf(lvpClan.getPoints()) : "0";
        }

        // %leaguesvp_top_position%
        if (params.equals("top_position")) {
            return clanTop.contains(lvpClan) ? String.valueOf(clanTop.indexOf(lvpClan) + 1) : "0";
        }

        // %leaguesvp_cpoints_<position>%
        pattern = Pattern.compile("cpoints_(?<position>\\d+)");
        matcher = pattern.matcher(params);
        if (matcher.matches()) {
            String posString = matcher.group("position");
            int pos = Integer.parseInt(posString) - 1;
            if (pos >= clanTop.size() || pos < 0) return "";
            return String.valueOf(clanTop.get(pos).getPoints());
        }

        // %leaguesvp_clan_<position>%
        pattern = Pattern.compile("clan_(?<position>\\d+)");
        matcher = pattern.matcher(params);
        if (matcher.matches()) {
            String posString = matcher.group("position");
            int pos = Integer.parseInt(posString) - 1;
            if (pos >= clanTop.size() || pos < 0) return "";
            Clan clan = clanManager.getClan(clanTop.get(pos).getTag());
            return clan.getName();
        }

        // %leaguesvp_clantag_<position>%
        pattern = Pattern.compile("clantag_(?<position>\\d+)");
        matcher = pattern.matcher(params);
        if (matcher.matches()) {
            String posString = matcher.group("position");
            int pos = Integer.parseInt(posString) - 1;
            if (pos >= clanTop.size() || pos < 0) return "";
            Clan clan = clanManager.getClan(clanTop.get(pos).getTag());
            return clan.getColorTag();
        }

        return null;
    }
}
