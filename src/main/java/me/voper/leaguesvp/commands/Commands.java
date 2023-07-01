package me.voper.leaguesvp.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.ClanData;
import me.voper.leaguesvp.data.GsonManager;
import me.voper.leaguesvp.managers.SettingsManager;
import me.voper.leaguesvp.utils.Pair;
import me.voper.leaguesvp.utils.SoundUtils;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.acf.BaseCommand;
import net.sacredlabyrinth.phaed.simpleclans.acf.CommandHelp;
import net.sacredlabyrinth.phaed.simpleclans.acf.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.acf.bukkit.contexts.OnlinePlayer;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

@CommandAlias("%leaguesvp")
@Description("Plugin's root command")
public class Commands extends BaseCommand {

    @Dependency
    private ClanManager clanManager;
    @Dependency
    private GsonManager dataManager;
    @Dependency
    private LeaguesVP plugin;
    @Dependency
    private SettingsManager sm;

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%givepoints")
    @CommandPermission("leaguesvp.admin.givepoints")
    @Description("Gives points to a certain clan passing a player as a parameter")
    public void onGivePoints(CommandSender sender, OnlinePlayer player, int points) {
        Player p = player.getPlayer();
        Pair<ClanPlayer, ClanData> clanPair = getClanPair(sender, p);
        if (clanPair == null) return;

        clanPair.second.addPoints(points);
        dataManager.updateAndSave();
        String message = this.sm.getString(SettingsManager.ConfigField.GIVEP_MESSAGE).replaceAll("%points%", String.valueOf(points));
        sendGlobalMessage(message, player.getPlayer());
        playGlobalSound(XSound.ENTITY_FIREWORK_ROCKET_BLAST_FAR);
        sendConfirmationMessage(sender);
    }

    @Subcommand("%giveall")
    @CommandPermission("leaguesvp.admin.givepoints")
    @Description("Gives points to all the clans registered")
    public void onGiveAll(CommandSender sender, int points) {
        dataManager.getClanData().forEach(c -> c.addPoints(points));
        dataManager.updateAndSave();
        String message = this.sm.getString(SettingsManager.ConfigField.GIVEALL_MESSAGE).replaceAll("%points%", String.valueOf(points));
        sendGlobalMessage(message);
        sendConfirmationMessage(sender);
    }

    @Subcommand("%removepoints")
    @CommandPermission("leaguesvp.admin.removepoints")
    @Description("Removes the points of a certain clan passing a player as a parameter")
    public void onRemovePoints(CommandSender sender, OnlinePlayer player, int points) {
        Player p = player.getPlayer();
        Pair<ClanPlayer, ClanData> clanPair = getClanPair(sender, p);
        if (clanPair == null) {return;}
        if (points > clanPair.second.getPoints() && !this.sm.getBoolean(SettingsManager.ConfigField.NEGATIVE_BALANCE)) {
            this.sendMessage(sender, "&cERROR: Could not perform this command.", "&cconfig.negative-balance is set to false.");
            return;
        }
        clanPair.second.subPoints(points);
        dataManager.updateAndSave();
        String message = this.sm.getString(SettingsManager.ConfigField.REMOVEP_MESSAGE).replaceAll("%points%", String.valueOf(points));
        sendGlobalMessage(message, player.getPlayer());
        sendConfirmationMessage(sender);
    }

    @Subcommand("%removeall")
    @CommandPermission("leaguesvp.admin.removepoints")
    @Description("Removes the points of all the clans registered")
    public void onRemoveAll(CommandSender sender, int points) {
        dataManager.getClanData().forEach(c -> {
            if (c.getPoints() < points && !this.sm.getBoolean(SettingsManager.ConfigField.NEGATIVE_BALANCE)) {
                this.sendMessage(sender, "&cERROR: Could not perform this command to the clan " + c.getTag() + ".",
                        "&cconfig.negative-balance is set to false.");
            } else {
                c.subPoints(points);
            }
        });
        dataManager.updateAndSave();
        String message = this.sm.getString(SettingsManager.ConfigField.REMOVEALL_MESSAGE).replaceAll("%points%", String.valueOf(points));
        sendGlobalMessage(message);
        sendConfirmationMessage(sender);
    }

    @Subcommand("%resetpoints")
    @CommandPermission("leaguesvp.admin.resetpoints")
    @Description("Resets the points of a clan to zero")
    public void onResetPoints(CommandSender sender, OnlinePlayer player) {
        Player p = player.getPlayer();
        Pair<ClanPlayer, ClanData> clanPair = getClanPair(sender, p);
        if (clanPair == null) {return;}
        clanPair.second.setPoints(0);
        dataManager.updateAndSave();
        String message = this.sm.getString(SettingsManager.ConfigField.RESETP_MESSAGE);
        sendGlobalMessage(message, player.getPlayer());
        sendConfirmationMessage(sender);
    }

    @Subcommand("%resetall")
    @CommandPermission("leaguesvp.admin.resetpoints")
    @Description("Resets the points of all the clans to zero")
    public void onResetAllPoints(CommandSender sender) {
        dataManager.getClanData().forEach(clanData -> clanData.setPoints(0));
        dataManager.updateAndSave();
        String message = this.sm.getString(SettingsManager.ConfigField.RESETALL_MESSAGE);
        sendGlobalMessage(message);
        sendConfirmationMessage(sender);
    }

    @Subcommand("%register")
    @CommandPermission("leaguesvp.admin.register")
    @CommandCompletion("@clans")
    @Description("Registers a clan to the clansdata.json file")
    public void onRegister(CommandSender sender, @Name("clan") ClanInput clanInput) {
        if (dataManager.isRegistered(clanInput.getClan())) {
            this.sendMessage(sender, "&bThe clan " + clanInput.getClan().getTag() + " is already registered!");
            return;
        }
        dataManager.registerClan(clanInput.getClan());
        this.sendMessage(sender,"&bThe clan " + clanInput.getClan().getTag() + " was &asuccessfully registered.");
    }

    @Subcommand("%unregister")
    @CommandPermission("leaguesvp.admin.unregister")
    @CommandCompletion("@clans")
    @Description("Unregisters a clan from the clansdata.json file")
    public void onUnregister(CommandSender sender, @Name("clan") ClanInput clanInput) {
        if (dataManager.isRegistered(clanInput.getClan())) {
            dataManager.unregisterClan(clanInput.getClan());
            this.sendMessage(sender,"&bThe clan " + clanInput.getClan().getTag() + " was &asuccessfully unregistered.");
        } else {
            this.sendMessage(sender, "&bThe clan " + clanInput.getClan().getTag() + " &cis not registered.");
        }
    }

    @Subcommand("%info")
    @CommandPermission("leaguesvp.anyone.info")
    @CommandCompletion("@clans")
    @Description("Shows how many points a certain clan has")
    public void onInfo(CommandSender sender, @Name("clan") ClanInput clanInput) {
        Clan clan = clanInput.getClan();
        ClanData data = dataManager.findClan(clan);
        if (data == null) {
            this.sendMessage(sender,"&cThe clan is not registered");
            return;
        }
        UUID uuid = clan.getMembers().get(0).getUniqueId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String message = sm.getString(SettingsManager.ConfigField.INFO_MESSAGE);
        if (message != null && !message.equals("")) {
            this.sendMessage(sender, PlaceholderAPI.setPlaceholders(offlinePlayer, message));
        }
    }

    @Subcommand("%top")
    @CommandPermission("leaguesvp.anyone.top")
    @Description("Show the punctuation rank of the clans")
    public void onTop(CommandSender sender, @Optional Integer page) {
        if (page == null) { page = 1; }
        dataManager.updateClanTop();
        ArrayList<ClanData> orderedClans = new ArrayList<>(dataManager.getClanTop());
        if (orderedClans.isEmpty()) {
            this.sendMessage(sender, "&cThere isn't any clan!");
            return;
        }
        if ((page * 10) - 9 > orderedClans.size() || page <= 0) {
            this.sendMessage(sender, sm.getString(SettingsManager.ConfigField.TOP_INVALID_PAGE));
            return;
        }
        this.sendMessage(sender, sm.getString(SettingsManager.ConfigField.TOP_HEADER));
        if (orderedClans.size() <= 10) {
            for (int i = 0; i < orderedClans.size() ; i++) {
                this.sendMessage(sender, topMessage(orderedClans, i));
            }
        } else {
            for (int i = 10 * (page - 1); i < page * 10 && i < orderedClans.size(); i++) {
                this.sendMessage(sender, topMessage(orderedClans, i));
            }
        }
    }

    private Pair<ClanPlayer, ClanData> getClanPair(CommandSender sender, Player player) {
        ClanPlayer cp = clanManager.getClanPlayer(player);
        if (cp == null) {
            this.sendMessage(sender,"&cERROR: The player " + player.getName() + " is not a clan player!");
            return null;
        }
        ClanData data = dataManager.findClan(cp.getClan());
        if (data == null) {
            this.sendMessage(sender,"&cERROR: Clan not found!");
            return null;
        }

        return new Pair<>(cp, data);
    }

    private void sendGlobalMessage(@NotNull String message, @Nullable Player player) {
        if (sm.getBoolean(SettingsManager.ConfigField.ENABLE_GLOBAL_MESSAGES)) {
            String prefix = sm.getString(SettingsManager.ConfigField.PREFIX);

            if (message.isBlank()) return;

            if (player != null) {
                Bukkit.broadcastMessage(ChatUtils.parseColors(PlaceholderAPI.setPlaceholders(player, prefix + " " + message).trim()));
            } else {
                Bukkit.broadcastMessage(ChatUtils.parseColors(prefix + " " + message).trim());
            }
        }
    }

    private void sendGlobalMessage(@NotNull String message) {
        this.sendGlobalMessage(message, null);
    }

    private void sendConfirmationMessage(CommandSender sender) {
        if (sm.getBoolean(SettingsManager.ConfigField.CONFIRMATION_MESSAGE)) {
            this.sendMessage(sender, "&b[LeaguesVP] &9Command successfully executed.");
        }
    }

    private String topMessage(@NotNull ArrayList<ClanData> clanData, int position) {
        // Some of the placeholders used require a player
        UUID playerID = clanManager.getClan(clanData.get(position).getTag()).getMembers().get(0).getUniqueId();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);

        String message = sm.getString(SettingsManager.ConfigField.TOP_POSITION);
        message = message.replaceAll("%position%", String.valueOf(position + 1));
        message = ChatUtils.parseColors(PlaceholderAPI.setPlaceholders(player, message));
        return message;
    }

    private void sendMessage(@NotNull CommandSender sender, @NotNull String... message) {
        if (sender instanceof Player p) {
            for (String m : message) {
                p.sendMessage(ChatUtils.parseColors(m));
            }
        } else {
            for (String m : message) {
                sender.sendMessage(ChatUtils.stripColors(m));
            }
        }
    }

    private void playGlobalSound(@NotNull XSound sound) {
        if (sm.getBoolean(SettingsManager.ConfigField.GLOBAL_SOUND)) {
            SoundUtils.playAll(sound);
        }
    }

}
