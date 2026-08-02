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
package io.github.lijinhong11.mittellib.gui.inventory;

import io.github.lijinhong11.mittellib.gui.inventory.impl.AnvilGUI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;

public final class MittelGUIListener implements Listener {
    private final Map<UUID, Long> lastClickTime = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MittelGUI mg)) {
            if (event.getClick() == ClickType.DOUBLE_CLICK
                    && event.getView().getTopInventory().getHolder() instanceof MittelGUI) {
                event.setCancelled(true);
            }
            return;
        }

        Player player = (Player) event.getWhoClicked();
        long now = System.currentTimeMillis();
        Long last = lastClickTime.get(player.getUniqueId());

        if (last != null && (now - last) < 50L) {
            event.setCancelled(true);
            return;
        }

        lastClickTime.put(player.getUniqueId(), now);
        mg.handleClick(event.getRawSlot(), event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MittelGUI mg)) {
            return;
        }

        mg.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MittelGUI mg)) {
            return;
        }

        mg.handleClose(event);
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {
        if (event.getInventory().getHolder() instanceof AnvilGUI ag) {
            ag.handlePrepare(event.getView());
        }
    }
}
