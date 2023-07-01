package me.voper.leaguesvp.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public final class ClanData implements Comparable<ClanData> {

    private static int initialPoints = LeaguesVP.getInstance().getSettingsManager().getInt(SettingsManager.ConfigField.STARTING_POINTS);
    private String tag;
    private int points;

    public ClanData(@NotNull Clan clan) {
        this.tag = clan.getTag();
        this.points = initialPoints;
    }

    public void addPoints(int points) {
        this.setPoints(this.getPoints() + points);
    }

    public void subPoints(int points) {
        this.setPoints(this.points - points);
    }

    @Override
    public int compareTo(@NotNull ClanData o) {
        return getPoints() - o.getPoints();
    }
}
