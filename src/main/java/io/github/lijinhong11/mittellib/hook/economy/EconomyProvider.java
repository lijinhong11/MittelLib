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

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface EconomyProvider {
    /**
     * Returns the unique provider identifier.
     *
     * @return the provider identifier
     */
    @NotNull String getId();

    /**
     * Checks whether the provider is available on the current server.
     *
     * @return true when the provider is ready to handle operations
     */
    boolean isEnabled();

    /**
     * Returns a player's balance.
     *
     * @param player the player
     * @return the current balance
     */
    double getBalance(@NotNull OfflinePlayer player);

    /**
     * Sets a player's balance.
     *
     * @param player the player
     * @param amount the target balance
     * @return true when the operation succeeds
     */
    boolean setBalance(@NotNull OfflinePlayer player, double amount);

    /**
     * Deposits money into a player's account.
     *
     * @param player the player
     * @param amount the amount to deposit
     * @return true when the operation succeeds
     */
    boolean deposit(@NotNull OfflinePlayer player, double amount);

    /**
     * Withdraws money from a player's account.
     *
     * @param player the player
     * @param amount the amount to withdraw
     * @return true when the operation succeeds
     */
    boolean withdraw(@NotNull OfflinePlayer player, double amount);

    /**
     * Checks whether a player has at least the requested amount.
     *
     * @param player the player
     * @param amount the required amount
     * @return true when the player has enough money
     */
    boolean has(@NotNull OfflinePlayer player, double amount);
}
