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
package io.github.lijinhong11.mittellib.gui.inventory.item;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface MittelGUIItem {
    ItemStack getItem();

    /**
     * Handle the click event.
     *
     * @param gui the gui
     * @param event the click event
     * @return true if allows the click, otherwise deny
     */
    @CanIgnoreReturnValue
    default boolean onClick(MittelGUI gui, InventoryClickEvent event) {
        return true;
    }
}
