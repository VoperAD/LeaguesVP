package me.voper.leaguesvp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.voper.leaguesvp.LeaguesVP;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.jetbrains.annotations.NotNull;

import static me.voper.leaguesvp.managers.SettingsManager.ConfigField.STARTING_POINTS;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "tag")
public class LVPClan implements Comparable<LVPClan> {

    private final String tag;
    private int points;

    public LVPClan(String tag) {
        this.tag = tag;
        this.points = LeaguesVP.getInstance().getSettingsManager().getInt(STARTING_POINTS);
    }

    public LVPClan(Clan clan) {
        this(clan.getTag());
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void subPoints(int points) {
        this.points -= points;
    }

    @Override
    public int compareTo(@NotNull LVPClan o) {
        return this.points - o.getPoints();
    }

}
