package me.voper.leaguesvp.commands;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.GsonManager;
import me.voper.leaguesvp.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.SCCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LVCommandsManager extends SCCommandManager {

    private final LeaguesVP leaguesVP;
    private final SettingsManager sm;

    public LVCommandsManager(@NotNull LeaguesVP leaguesVP) {
        super(SimpleClans.getInstance());
        this.leaguesVP = leaguesVP;
        this.sm = leaguesVP.getSettingsManager();
        this.setup();
    }

    private void setup() {
        this.enableUnstableAPI("help");
        this.registerDependencies();
        this.registerCommandsReplacements();
        this.registerAllCommands();
    }

    private void registerDependencies() {
        registerDependency(GsonManager.class, LeaguesVP.getDataManager());
        registerDependency(LeaguesVP.class, this.leaguesVP);
        registerDependency(SettingsManager.class, this.leaguesVP.getSettingsManager());
    }

    private void registerCommandsReplacements() {
        this.getCommandReplacements().addReplacements(
                "givepoints", this.sm.getString(SettingsManager.ConfigField.COMMAND_GIVEPOINTS),
                "giveall", this.sm.getString(SettingsManager.ConfigField.COMMAND_GIVEALL),
                "removepoints", this.sm.getString(SettingsManager.ConfigField.COMMAND_REMOVEPOINTS),
                "removeall", this.sm.getString(SettingsManager.ConfigField.COMMAND_REMOVEALL),
                "resetpoints", this.sm.getString(SettingsManager.ConfigField.COMMAND_RESETPOINTS),
                "resetall", this.sm.getString(SettingsManager.ConfigField.COMMAND_RESETALL),
                "register", this.sm.getString(SettingsManager.ConfigField.COMMAND_REGISTER),
                "unregister", this.sm.getString(SettingsManager.ConfigField.COMMAND_UNREGISTER),
                "top", this.sm.getString(SettingsManager.ConfigField.COMMAND_TOP),
                "info", this.sm.getString(SettingsManager.ConfigField.COMMAND_INFO)
        );
        // Main command aliases construction
        StringBuilder builder = new StringBuilder();
        List<String> aliases = this.sm.getStringList(SettingsManager.ConfigField.COMMAND_LEAGUESVP);
        aliases.forEach(alias -> {
            builder.append(alias).append("|");
        });
        builder.deleteCharAt(builder.toString().lastIndexOf('|'));
        this.getCommandReplacements().addReplacements("leaguesvp", builder.toString());
    }

    private void registerAllCommands() {
        registerCommand(new Commands());
    }

}
