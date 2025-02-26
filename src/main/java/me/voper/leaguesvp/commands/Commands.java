package me.voper.leaguesvp.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.LVPClan;
import me.voper.leaguesvp.managers.SettingsManager;
import me.voper.leaguesvp.managers.StorageManager;
import me.voper.leaguesvp.utils.CommandUtils;
import me.voper.leaguesvp.utils.SoundUtils;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.acf.BaseCommand;
import net.sacredlabyrinth.phaed.simpleclans.acf.CommandHelp;
import net.sacredlabyrinth.phaed.simpleclans.acf.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

import static me.voper.leaguesvp.managers.SettingsManager.ConfigField.*;

@CommandAlias("%leaguesvp")
@Description("Plugin's root command")
public class Commands extends BaseCommand {

    @Dependency
    private ClanManager clanManager;

    @Dependency
    private LeaguesVP plugin;

    @Dependency
    private SettingsManager sm;

    @Dependency
    private StorageManager storageManager;

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%givepoints")
    @CommandCompletion("@players")
    @CommandPermission("leaguesvp.admin.givepoints")
    @Description("Gives points to a certain clan passing a player as a parameter")
    public void givePoints(CommandSender sender, @Name("player") @Conditions("clan_member") ClanPlayerInput cp, int points) {
        UUID uuid = cp.getClanPlayer().getUniqueId();
        String tag = cp.getClanPlayer().getClan().getTag();

        storageManager.getLvpClanByTag(tag).ifPresentOrElse(lvpClan -> {
            lvpClan.addPoints(points);
            storageManager.updateLvpClan(lvpClan);
            String message = sm.getString(GIVEP_MESSAGE).replaceAll("%points%", String.valueOf(points));
            CommandUtils.sendGlobalMessage(message, uuid);
            SoundUtils.playGlobalSound(XSound.ENTITY_FIREWORK_ROCKET_BLAST);
        }, () -> sender.sendMessage("&cClan %s not found".formatted(tag)));
    }

    @Subcommand("%giveall")
    @CommandPermission("leaguesvp.admin.givepoints")
    @Description("Gives points to all the clans registered")
    public void giveAll(CommandSender sender, int points) {
        storageManager.applyToAllClans(c -> c.addPoints(points));
        String message = this.sm.getString(GIVEALL_MESSAGE).replaceAll("%points%", String.valueOf(points));
        CommandUtils.sendGlobalMessage(message);
    }

    @Subcommand("%removepoints")
    @CommandCompletion("@players")
    @CommandPermission("leaguesvp.admin.removepoints")
    @Description("Removes the points of a certain clan passing a player as a parameter")
    public void removePoints(CommandSender sender, @Conditions("clan_member") ClanPlayerInput cp, int points) {
        UUID uuid = cp.getClanPlayer().getUniqueId();
        String tag = cp.getClanPlayer().getClan().getTag();

        storageManager.getLvpClanByTag(tag).ifPresentOrElse(lvpClan -> {
            if (points > lvpClan.getPoints() && !this.sm.getBoolean(NEGATIVE_BALANCE)) {
                CommandUtils.sendMessage(sender, "&cERROR: Could not perform this command.", "&cconfig.negative-balance is set to false.");
                return;
            }
            lvpClan.subPoints(points);
            storageManager.updateLvpClan(lvpClan);
            String message = sm.getString(REMOVEP_MESSAGE).replaceAll("%points%", String.valueOf(points));
            CommandUtils.sendGlobalMessage(message, uuid);
            SoundUtils.playGlobalSound(XSound.ENTITY_FIREWORK_ROCKET_BLAST);
        }, () -> {
            sender.sendMessage("&cClan %s not found".formatted(tag));
        });

    }

    @Subcommand("%removeall")
    @CommandPermission("leaguesvp.admin.removepoints")
    @Description("Removes the points of all registered clans")
    public void removeAll(CommandSender sender, int points) {
        storageManager.applyToAllClans(clan -> {
            if (clan.getPoints() < points && !sm.getBoolean(NEGATIVE_BALANCE)) {
                CommandUtils.sendMessage(sender, "&cClan %s not affected as its balance would be negative (negative-balance in config.yml is set to false)");
                return;
            }

            clan.subPoints(points);
        });

        String message = sm.getString(REMOVEALL_MESSAGE).replaceAll("%points%", String.valueOf(points));
        CommandUtils.sendGlobalMessage(message);
    }

    @Subcommand("%resetpoints")
    @CommandCompletion("@players")
    @CommandPermission("leaguesvp.admin.resetpoints")
    @Description("Resets the points of a clan to zero")
    public void resetPoints(CommandSender sender, @Name("player") @Conditions("clan_member") ClanPlayerInput cp) {
        String tag = cp.getClanPlayer().getClan().getTag();
        storageManager.getLvpClanByTag(tag).ifPresentOrElse(clan -> {
            clan.setPoints(0);
            storageManager.updateLvpClan(clan);
            String message = sm.getString(RESETP_MESSAGE);
            UUID uuid = cp.getClanPlayer().getUniqueId();
            CommandUtils.sendGlobalMessage(message, uuid);
        }, () -> sender.sendMessage("&cClan %s not found".formatted(tag)));
    }

    @Subcommand("%resetall")
    @CommandPermission("leaguesvp.admin.resetpoints")
    @Description("Resets the points of all the clans to zero")
    public void resetAll(CommandSender sender) {
        storageManager.applyToAllClans(clan -> clan.setPoints(0));
        String message = sm.getString(RESETALL_MESSAGE);
        CommandUtils.sendGlobalMessage(message);
    }

    // TODO: At least for now, the register command will not be implemented
//
//    @Subcommand("%register")
//    @CommandPermission("leaguesvp.admin.register")
//    @CommandCompletion("@clans")
//    @Description("Registers a clan to the clansdata.json file")
//    public void onRegister(CommandSender sender, @Name("clan") ClanInput clanInput) {
//        if (dataManager.isRegistered(clanInput.getClan())) {
//            CommandUtils.sendMessage(sender, "&bThe clan " + clanInput.getClan().getTag() + " is already registered!");
//            return;
//        }
//        dataManager.registerClan(clanInput.getClan());
//        CommandUtils.sendMessage(sender,"&bThe clan " + clanInput.getClan().getTag() + " was &asuccessfully registered.");
//    }
//
//    @Subcommand("%unregister")
//    @CommandPermission("leaguesvp.admin.unregister")
//    @CommandCompletion("@clans")
//    @Description("Unregisters a clan from the clansdata.json file")
//    public void onUnregister(CommandSender sender, @Name("clan") ClanInput clanInput) {
//        if (dataManager.isRegistered(clanInput.getClan())) {
//            dataManager.unregisterClan(clanInput.getClan());
//            CommandUtils.sendMessage(sender,"&bThe clan " + clanInput.getClan().getTag() + " was &asuccessfully unregistered.");
//        } else {
//            CommandUtils.sendMessage(sender, "&bThe clan " + clanInput.getClan().getTag() + " &cis not registered.");
//        }
//    }

    @Subcommand("%info")
    @CommandPermission("leaguesvp.anyone.info")
    @CommandCompletion("@clans")
    @Description("Shows how many points a certain clan has")
    public void info(CommandSender sender, @Name("clan") ClanInput ci) {
        Clan clan = ci.getClan();
        storageManager.getLvpClanByTag(clan.getTag()).ifPresentOrElse(lvpClan -> {
            UUID uuid = clan.getMembers().get(0).getUniqueId();
            String message = sm.getString(INFO_MESSAGE);
            if (message != null && !message.isBlank()) {
                CommandUtils.sendMessage(sender, PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), message));
            }
        }, () -> CommandUtils.sendMessage(sender, "&cClan %s not found".formatted(clan.getTag())));
    }

    @Subcommand("%top")
    @CommandPermission("leaguesvp.anyone.top")
    @Description("Show the punctuation rank of the clans")
    public void top(CommandSender sender, @Optional Integer page) {
        if (page == null) {
            page = 1;
        }

        List<LVPClan> orderedClans = storageManager.getClansSorted();
        if (orderedClans.isEmpty()) {
            CommandUtils.sendMessage(sender, "&cThere isn't any clan!");
            return;
        }

        // If the first position of the given page is higher than the total amount of clans, then this is not a valid page
        if ((page * 10) - 9 > orderedClans.size() || page <= 0) {
            CommandUtils.sendMessage(sender, sm.getString(TOP_INVALID_PAGE));
            return;
        }

        CommandUtils.sendMessage(sender, sm.getString(TOP_HEADER));
        if (orderedClans.size() <= 10) {
            for (int i = 0; i < orderedClans.size() ; i++) {
                CommandUtils.sendMessage(sender, CommandUtils.topMessage(orderedClans, i));
            }
        } else {
            for (int i = 10 * (page - 1); i < page * 10 && i < orderedClans.size(); i++) {
                CommandUtils.sendMessage(sender, CommandUtils.topMessage(orderedClans, i));
            }
        }
    }

}
