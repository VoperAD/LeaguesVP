package me.voper.leaguesvp.commands;

import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.managers.SettingsManager;
import me.voper.leaguesvp.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.SCCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.voper.leaguesvp.managers.SettingsManager.ConfigField.*;

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
        registerDependency(LeaguesVP.class, this.leaguesVP);
        registerDependency(StorageManager.class, this.leaguesVP.getStorageManager());
        registerDependency(SettingsManager.class, this.leaguesVP.getSettingsManager());
    }

    private void registerCommandsReplacements() {
        this.getCommandReplacements().addReplacements(
                "givepoints", this.sm.getString(COMMAND_GIVEPOINTS),
                "giveall", this.sm.getString(COMMAND_GIVEALL),
                "removepoints", this.sm.getString(COMMAND_REMOVEPOINTS),
                "removeall", this.sm.getString(COMMAND_REMOVEALL),
                "resetpoints", this.sm.getString(COMMAND_RESETPOINTS),
                "resetall", this.sm.getString(COMMAND_RESETALL),
                "register", this.sm.getString(COMMAND_REGISTER),
                "unregister", this.sm.getString(COMMAND_UNREGISTER),
                "top", this.sm.getString(COMMAND_TOP),
                "info", this.sm.getString(COMMAND_INFO)
        );
        // Main command aliases construction
        StringBuilder builder = new StringBuilder();
        List<String> aliases = this.sm.getStringList(COMMAND_LEAGUESVP);
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
