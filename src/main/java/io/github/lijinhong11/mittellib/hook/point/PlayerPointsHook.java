/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.hook.point;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerPointsHook {
    private static PlayerPointsAPI pointsAPI;

    public static void init() {
        if (pointsAPI != null) {
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            return;
        }

        pointsAPI = PlayerPoints.getInstance().getAPI();
    }

    public static boolean isEnabled() {
        return pointsAPI != null;
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
