package me.voper.leaguesvp.utils;

import net.sacredlabyrinth.phaed.simpleclans.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SoundUtils {

    public static void playAll(@NotNull final XSound sound) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            play(p, sound);
        }
    }

    public static void play(@NotNull final Player player, @NotNull final XSound sound) {
        if (sound.isSupported() && sound.parseSound() != null) {
            player.playSound(player.getLocation(), sound.parseSound(), 1.0F, 1.0F);
        }
    }

}
