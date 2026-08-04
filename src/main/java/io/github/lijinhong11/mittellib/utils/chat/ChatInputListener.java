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

import io.github.lijinhong11.mittellib.utils.components.ComponentUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

class ChatInputListener implements Listener {
    private final Plugin plugin;
    protected Map<UUID, Set<ChatInputHandler>> handlers;

    protected ChatInputListener(Plugin plugin) {
        this.plugin = plugin;
        this.handlers = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    void addCallback(UUID uuid, ChatInputHandler input) {
        Set<ChatInputHandler> callbacks = handlers.computeIfAbsent(uuid, _ -> new HashSet<>());
        callbacks.add(input);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        if (e.getPlugin() == plugin) {
            ChatInput.listener = null;
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        handlers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        handlers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent e) {
        checkInput(e, e.getPlayer(), ComponentUtils.serialize(e.message()));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        checkInput(e, e.getPlayer(), e.getMessage());
    }

    private void checkInput(Cancellable e, Player p, String msg) {
        Set<ChatInputHandler> callbacks = handlers.get(p.getUniqueId());

        if (callbacks != null) {
            Iterator<ChatInputHandler> iterator = callbacks.iterator();

            while (iterator.hasNext()) {
                ChatInputHandler handler = iterator.next();

                if (handler.test(msg)) {
                    iterator.remove();
                    handler.onChat(p, msg);

                    e.setCancelled(true);
                    return;
                }
            }

            if (callbacks.isEmpty()) handlers.remove(p.getUniqueId());
        }
    }
}
