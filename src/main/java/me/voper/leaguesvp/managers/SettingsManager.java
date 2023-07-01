package me.voper.leaguesvp.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.voper.leaguesvp.LeaguesVP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SettingsManager {

    private final LeaguesVP plugin;
    @Getter private final FileConfiguration config;

    public SettingsManager(@NotNull LeaguesVP plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.config.options().copyDefaults(true);
        this.plugin.saveConfig();
    }

    public String getString(@NotNull ConfigField field) {
        return this.config.getString(field.path, String.valueOf(field.value));
    }

    public List<String> getStringList(@NotNull ConfigField field) {
        return this.config.getStringList(field.path);
    }

    public int getInt(@NotNull ConfigField field) {
        return this.config.getInt(field.path, NumberConversions.toInt(field.value));
    }

    public boolean getBoolean(@NotNull ConfigField field) {
        return this.config.getBoolean(field.path, (Boolean) field.value);
    }

    @AllArgsConstructor
    public enum ConfigField{

        // LANGUAGE("settings.language", "en"),
        GLOBAL_SOUND("settings.global-sound", true),
        NEGATIVE_BALANCE("settings.clan.negative-balance", false),
        STARTING_POINTS("settings.clan.starting-points", 1000),
        AUTO_LOAD_CLANS("settings.clan.auto-load", true),
        ENABLE_GLOBAL_MESSAGES("commands.global-messages.enabled", true),
        PREFIX("commands.global-messages.prefix", "&f[&bLigas&f]"),
        GIVEP_MESSAGE("commands.global-messages.givepoints", "&bThe clan &f[%simpleclans_clan_color_tag%&f] &b- &9%simpleclans_clan_name% &bhas just &areceived %points% points!"),
        GIVEALL_MESSAGE("commands.global-messages.giveall","&bAll the clans have just received %points% points!"),
        REMOVEP_MESSAGE("commands.global-messages.removepoints","&bThe clan &f[%simpleclans_clan_color_tag%&f] &b- &9%simpleclans_clan_name% &bhas just &clost %points% points!"),
        REMOVEALL_MESSAGE("commands.global-messages.removeall","&bAll the clans have just lost %points% points!"),
        RESETP_MESSAGE("commands.global-messages.resetpoints","&bThe clan &f[%simpleclans_clan_color_tag%&f] &b- &9%simpleclans_clan_name% &bhas just had it''s points &creset to zero!"),
        RESETALL_MESSAGE("commands.global-messages.resetall", "&bAll the clans have just had their points &creset to zero!"),
        CONFIRMATION_MESSAGE("commands.confirmation-message", false),
        TOP_HEADER("commands.top.header","&b------- Top Clans -------"),
        TOP_INVALID_PAGE("commands.top.invalid-page","&cUnable to access this page."),
        TOP_POSITION("commands.top.position","%position% - &f[%simpleclans_clan_color_tag%&f] %simpleclans_clan_name% &a- %leaguesvp_cpoints% points."),
        INFO_MESSAGE("commands.info.message","&bThe clan &f[%simpleclans_clan_color_tag%&f] &b- &9%simpleclans_clan_name% &bhas %leaguesvp_cpoints% points."),
        // COMMANDS NAMES & ALIASES
        COMMAND_LEAGUESVP("commands.leaguesvp"), // FIXME Check it later
        COMMAND_GIVEPOINTS("commands.givepoints","givepoints"),
        COMMAND_GIVEALL("commands.giveall","givepoints all"),
        COMMAND_REMOVEPOINTS("commands.removepoints","removepoints"),
        COMMAND_REMOVEALL("commands.removeall","removepoints all"),
        COMMAND_RESETPOINTS("commands.resetpoints","resetpoints"),
        COMMAND_RESETALL("commands.resetall","resetpoints all"),
        COMMAND_REGISTER("commands.register","register"),
        COMMAND_UNREGISTER("commands.unregister","unregister"),
        COMMAND_TOP("commands.top.name","top"),
        COMMAND_INFO("commands.info.name","info");


        private final String path;
        private final Object value;

        ConfigField(String path) {
            this.path = path;
            this.value = null;
        }

    }

}
