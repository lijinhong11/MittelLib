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

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.hook.economy.impl.VaultEconomyProvider;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;

@UtilityClass
public class EconomyProviders {
    private static final Map<String, EconomyProvider> PROVIDERS = new LinkedHashMap<>();
    private static EconomyProvider activeProvider;

    /**
     * Initializes the economy providers bundled with MittelLib.
     */
    public static void init() {
        if (!PROVIDERS.isEmpty()) {
            return;
        }

        register(new VaultEconomyProvider());
    }

    /**
     * Registers an economy provider.
     *
     * @param provider the provider to register
     */
    public static void register(EconomyProvider provider) {
        String id = provider.getId().toLowerCase();
        PROVIDERS.put(id, provider);

        MittelLib.getInstance().getLogger().info("Registered economy provider: " + id);

        selectProvider();
    }

    /**
     * Unregisters an economy provider.
     *
     * @param id the provider identifier
     */
    public static void unregister(String id) {
        PROVIDERS.remove(id.toLowerCase());
        selectProvider();
    }

    /**
     * Gets a registered provider by identifier.
     *
     * @param id the provider identifier
     * @return the provider, or null when it is not registered
     */
    public static EconomyProvider get(String id) {
        return PROVIDERS.get(id.toLowerCase());
    }

    /**
     * Gets the provider currently selected for economy operations.
     *
     * @return the active provider, or null when no provider is enabled
     */
    public static EconomyProvider getActive() {
        return activeProvider;
    }

    /**
     * Checks whether an economy provider is available.
     *
     * @return true when an enabled provider is available
     */
    public static boolean isEnabled() {
        return activeProvider != null;
    }

    /**
     * Gets a player's balance through the active provider.
     *
     * @param player the player
     * @return the current balance
     * @throws IllegalStateException when no provider is enabled
     */
    public static double getBalance(OfflinePlayer player) {
        return requireProvider().getBalance(player);
    }

    /**
     * Sets a player's balance through the active provider.
     *
     * @param player the player
     * @param amount the target balance
     * @return true when the operation succeeds
     * @throws IllegalStateException when no provider is enabled
     */
    public static boolean setBalance(OfflinePlayer player, double amount) {
        return requireProvider().setBalance(player, amount);
    }

    /**
     * Deposits money through the active provider.
     *
     * @param player the player
     * @param amount the amount to deposit
     * @return true when the operation succeeds
     * @throws IllegalStateException when no provider is enabled
     */
    public static boolean deposit(OfflinePlayer player, double amount) {
        return requireProvider().deposit(player, amount);
    }

    /**
     * Withdraws money through the active provider.
     *
     * @param player the player
     * @param amount the amount to withdraw
     * @return true when the operation succeeds
     * @throws IllegalStateException when no provider is enabled
     */
    public static boolean withdraw(OfflinePlayer player, double amount) {
        return requireProvider().withdraw(player, amount);
    }

    /**
     * Checks a player's balance through the active provider.
     *
     * @param player the player
     * @param amount the required amount
     * @return true when the player has enough money
     * @throws IllegalStateException when no provider is enabled
     */
    public static boolean has(OfflinePlayer player, double amount) {
        return requireProvider().has(player, amount);
    }

    private static void selectProvider() {
        activeProvider = PROVIDERS.values().stream()
                .filter(EconomyProvider::isEnabled)
                .findFirst()
                .orElse(null);
    }

    private static EconomyProvider requireProvider() {
        if (activeProvider == null) {
            throw new IllegalStateException("No economy provider is enabled");
        }
        return activeProvider;
    }
}
