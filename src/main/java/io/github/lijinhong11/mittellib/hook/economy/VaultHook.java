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
package io.github.lijinhong11.mittellib.hook.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy economy;

    public static void init() {
        if (economy != null) {
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> service =
                    Bukkit.getServicesManager().getRegistration(Economy.class);
            if (service != null) {
                economy = service.getProvider();
            }
        }
    }

    public static boolean isEnabled() {
        return economy != null;
    }

    public static double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static void setBalance(OfflinePlayer player, double amount) {
        double delta = economy.getBalance(player) - amount;
        if (delta > 0) {
            economy.withdrawPlayer(player, delta);
        } else {
            economy.depositPlayer(player, (-delta));
        }
    }

    public static void addBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public static void depositBalance(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public static void subtractBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public static void withdrawBalance(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public static boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }
}
