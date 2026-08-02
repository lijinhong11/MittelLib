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
package io.github.lijinhong11.mittellib.utils.chat;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ChatInput {
    static ChatInputListener listener;

    private ChatInput() {}

    /**
     * This method waits for the Player to write something in chat.
     * Afterward the given callback will be invoked.
     *
     * @param plugin  The Plugin performing this action
     * @param p       The Player that we are waiting for
     * @param handler A callback to invoke when the Player has entered some text
     */
    public static void waitForPlayer(@Nonnull Plugin plugin, @Nonnull Player p, @Nonnull Consumer<String> handler) {
        waitForPlayer(plugin, p, s -> true, handler);
    }

    /**
     * This method waits for the Player to write something in chat.
     * Afterward the given callback will be invoked.
     * With the predicate you can filter out unwanted inputs.
     * Like commands for example.
     *
     * @param plugin    The Plugin performing this action
     * @param p         The Player that we are waiting for
     * @param predicate A Filter for the messages the Player types in
     * @param handler   A callback to invoke when the Player has entered some text
     */
    public static void waitForPlayer(
            @Nonnull Plugin plugin,
            @Nonnull Player p,
            @Nonnull Predicate<String> predicate,
            @Nonnull Consumer<String> handler) {
        queue(plugin, p, new ChatInputHandler() {
            @Override
            public boolean test(String msg) {
                return predicate.test(msg);
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onChat(Player p, String msg) {
                handler.accept(msg);
            }
        });
    }

    /**
     * This method waits for the Player to write something in chat.
     * Afterward the given callback will be invoked.
     *
     * @param plugin  The Plugin performing this action
     * @param p       The Player that we are waiting for
     * @param handler A callback to invoke when the Player has entered some text
     */
    public static void waitForPlayer(
            @Nonnull Plugin plugin, @Nonnull Player p, @Nonnull BiConsumer<Player, String> handler) {
        waitForPlayer(plugin, p, s -> true, handler);
    }

    /**
     * This method waits for the Player to write something in chat.
     * Afterward the given callback will be invoked.
     * With the predicate you can filter out unwanted inputs.
     * Like commands for example.
     *
     * @param plugin    The Plugin performing this action
     * @param p         The Player that we are waiting for
     * @param predicate A Filter for the messages the Player types in
     * @param handler   A callback to invoke when the Player has entered some text
     */
    public static void waitForPlayer(
            @Nonnull Plugin plugin,
            @Nonnull Player p,
            @Nonnull Predicate<String> predicate,
            @Nonnull BiConsumer<Player, String> handler) {
        queue(plugin, p, new ChatInputHandler() {

            @Override
            public boolean test(String msg) {
                return predicate.test(msg);
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onChat(Player p, String msg) {
                handler.accept(p, msg);
            }
        });
    }

    public static void queue(@Nonnull Plugin plugin, @Nonnull Player p, @Nonnull ChatInputHandler callback) {
        if (listener == null) {
            listener = new ChatInputListener(plugin);
        }

        listener.addCallback(p.getUniqueId(), callback);
    }
}
