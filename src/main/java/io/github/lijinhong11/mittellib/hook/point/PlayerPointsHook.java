package io.github.lijinhong11.mittellib.hook.point;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerPointsHook {
    private static PlayerPointsAPI pointsAPI;

    public static void init() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            return;
        }

        pointsAPI = PlayerPoints.getInstance().getAPI();
    }

    public static int getPoints(OfflinePlayer player) {
        return pointsAPI.look(player.getUniqueId());
    }

    public static void setPoints(OfflinePlayer player, int amount) {
        pointsAPI.set(player.getUniqueId(), amount);
    }

    public static void givePoints(OfflinePlayer player, int amount) {
        pointsAPI.give(player.getUniqueId(), amount);
    }

    public static void takePoints(OfflinePlayer player, int amount) {
        pointsAPI.take(player.getUniqueId(), amount);
    }

    public static boolean has(OfflinePlayer player, int amount) {
        return pointsAPI.look(player.getUniqueId()) >= amount;
    }
}
