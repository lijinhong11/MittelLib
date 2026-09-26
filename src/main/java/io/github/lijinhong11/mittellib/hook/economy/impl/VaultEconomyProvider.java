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
package io.github.lijinhong11.mittellib.hook.economy.impl;

import io.github.lijinhong11.mittellib.hook.economy.EconomyProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public final class VaultEconomyProvider implements EconomyProvider {
    private Economy economy;

    public VaultEconomyProvider() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")
                && !Bukkit.getPluginManager().isPluginEnabled("ServiceIO")) {
            return;
        }

        RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (service != null) {
            economy = service.getProvider();
        }
    }

    @Override
    public @NotNull String getId() {
        return "vault";
    }

    @Override
    public boolean isEnabled() {
        return economy != null;
    }

    @Override
    public double getBalance(@NotNull OfflinePlayer player) {
        return economy.getBalance(player);
    }

    @Override
    public boolean setBalance(@NotNull OfflinePlayer player, double amount) {
        double delta = amount - economy.getBalance(player);
        return delta >= 0 ? deposit(player, delta) : withdraw(player, -delta);
    }

    @Override
    public boolean deposit(@NotNull OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    @Override
    public boolean withdraw(@NotNull OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    @Override
    public boolean has(@NotNull OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }
}
