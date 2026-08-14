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
import io.github.lijinhong11.mittellib.gui.inventory.impl.ChestGUI;
import io.github.lijinhong11.mittellib.gui.inventory.impl.CoordinateChestGUI;
import io.github.lijinhong11.mittellib.gui.inventory.impl.PaginatedChestGUI;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.view.AnvilView;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MittelGUI extends InventoryHolder {
    void open(@NotNull Player player);

    @NotNull List<HumanEntity> viewers();

    void handleClick(int slot, @NotNull InventoryClickEvent e);

    void handleOpen(@NotNull InventoryOpenEvent e);

    void handleClose(@NotNull InventoryCloseEvent e);

    static @NotNull ChestBuilder chestBuilder() {
        return new ChestGUI.Builder();
    }

    static @NotNull PagedChestBuilder pagedChestBuilder() {
        return new PaginatedChestGUI.Builder();
    }

    static @NotNull CoordinateBuilder coordinateBuilder() {
        return new CoordinateChestGUI.Builder();
    }

    @ApiStatus.Experimental
    static @NotNull AnvilBuilder anvilBuilder() {
        return new AnvilGUI.Builder();
    }

    interface ChestBuilder {
        @NotNull ChestBuilder title(@NotNull Component title);

        @NotNull ChestBuilder size(int size);

        @NotNull ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure);

        @NotNull ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure);

        default @NotNull ChestBuilder layout(@NotNull @ArrayLenRange(from = 1, to = 6) String... layout) {
            return structure(layout);
        }

        default @NotNull ChestBuilder layout(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> layout) {
            return structure(layout);
        }

        @NotNull ChestBuilder bind(char bind, @NotNull MittelGUIItem item);

        @NotNull ChestBuilder onOpen(@NotNull BiConsumer<Player, ChestGUI> openConsumer);

        @NotNull ChestBuilder onClose(@NotNull BiConsumer<Player, ChestGUI> closeConsumer);

        @NotNull ChestGUI build();
    }

    interface PagedChestBuilder {
        @NotNull PagedChestBuilder title(@NotNull Component title);

        @NotNull PagedChestBuilder size(int size);

        PagedChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure);

        PagedChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure);

        default PagedChestBuilder layout(@NotNull @ArrayLenRange(from = 1, to = 6) String... layout) {
            return structure(layout);
        }

        default PagedChestBuilder layout(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> layout) {
            return structure(layout);
        }

        PagedChestBuilder bind(char bind, @NotNull MittelGUIItem item);

        PagedChestBuilder content(char bind);

        PagedChestBuilder previousPage(char bind, @NotNull MittelGUIItem item);

        PagedChestBuilder nextPage(char bind, @NotNull MittelGUIItem item);

        PagedChestBuilder bindSearch(char bind, @NotNull MittelGUIItem item);

        /**
         * @param searchCallback Sets the callback used to decide whether an item matches a search query.
         */
        PagedChestBuilder onSearch(@NotNull BiPredicate<String, MittelGUIItem> searchCallback);

        PagedChestBuilder items(@NotNull Collection<? extends MittelGUIItem> items);

        PagedChestBuilder addItem(@NotNull MittelGUIItem item);

        PagedChestBuilder onOpen(@NotNull BiConsumer<Player, PaginatedChestGUI> openConsumer);

        PagedChestBuilder onClose(@NotNull BiConsumer<Player, PaginatedChestGUI> closeConsumer);

        @NotNull PaginatedChestGUI build();
    }

    interface CoordinateBuilder {
        @NotNull CoordinateBuilder title(@NotNull Component title);

        CoordinateBuilder rows(int rows);

        CoordinateBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure);

        CoordinateBuilder bind(char bind, @NotNull MittelGUIItem item);

        CoordinateBuilder view(char bind);

        CoordinateBuilder moveUp(char bind, @NotNull MittelGUIItem item);

        CoordinateBuilder moveDown(char bind, @NotNull MittelGUIItem item);

        CoordinateBuilder moveLeft(char bind, @NotNull MittelGUIItem item);

        CoordinateBuilder moveRight(char bind, @NotNull MittelGUIItem item);

        CoordinateBuilder axisX(@Nullable MittelGUIItem item);

        CoordinateBuilder axisY(@Nullable MittelGUIItem item);

        CoordinateBuilder origin(@Nullable MittelGUIItem item);

        CoordinateBuilder onOpen(@NotNull BiConsumer<Player, CoordinateChestGUI> openConsumer);

        CoordinateBuilder onClose(@NotNull BiConsumer<Player, CoordinateChestGUI> closeConsumer);

        @NotNull CoordinateChestGUI build();
    }

    interface AnvilBuilder {
        @NotNull AnvilBuilder title(@NotNull Component title);

        AnvilBuilder firstItem(@NotNull MittelGUIItem item);

        AnvilBuilder secondItem(@NotNull MittelGUIItem item);

        AnvilBuilder resultItem(@NotNull MittelGUIItem item);

        AnvilBuilder prepareListener(@NotNull BiConsumer<Player, AnvilView> consumer);

        AnvilBuilder onOpen(@NotNull BiConsumer<Player, AnvilGUI> openConsumer);

        AnvilBuilder onClose(@NotNull BiConsumer<Player, AnvilGUI> closeConsumer);

        @NotNull AnvilGUI build();
    }
}
