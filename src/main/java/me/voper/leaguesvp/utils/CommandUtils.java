package me.voper.leaguesvp.utils;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.voper.leaguesvp.LeaguesVP;
import me.voper.leaguesvp.data.LVPClan;
import me.voper.leaguesvp.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@UtilityClass
public final class CommandUtils {

    private static SettingsManager sm = LeaguesVP.getInstance().getSettingsManager();

    public static void sendGlobalMessage(@NotNull String msg, @Nullable OfflinePlayer player) {
        if (sm.getBoolean(SettingsManager.ConfigField.ENABLE_GLOBAL_MESSAGES)) {
            String prefix = sm.getString(SettingsManager.ConfigField.PREFIX);

            if (msg.isBlank()) return;

            if (player != null) {
                Bukkit.broadcastMessage(ChatUtils.parseColors(PlaceholderAPI.setPlaceholders(player, prefix + " " + msg).trim()));
            } else {
                Bukkit.broadcastMessage(ChatUtils.parseColors(prefix + " " + msg).trim());
            }
        }
    }

    public static void sendGlobalMessage(@NotNull String msg, @NotNull UUID uuid) {
        sendGlobalMessage(msg, Bukkit.getOfflinePlayer(uuid));
    }

    public static void sendGlobalMessage(@NotNull String message) {
        sendGlobalMessage(message, (OfflinePlayer) null);
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String... message) {
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

    public static String topMessage(@NotNull List<LVPClan> clanData, int position) {
        // Some of the placeholders used require a player
        ClanManager clanManager = LeaguesVP.getInstance().getClanManager();
        UUID playerID = clanManager.getClan(clanData.get(position).getTag()).getMembers().get(0).getUniqueId();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);

        String message = sm.getString(SettingsManager.ConfigField.TOP_POSITION);
        message = message.replaceAll("%position%", String.valueOf(position + 1));
        message = ChatUtils.parseColors(PlaceholderAPI.setPlaceholders(player, message));
        return message;
    }



}
