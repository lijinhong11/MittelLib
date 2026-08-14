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
package io.github.lijinhong11.mittellib.gui.inventory.impl;

import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CoordinateChestGUI implements MittelGUI {
    private final Inventory inv;
    private final MittelGUIItem[] items;
    private final int rows;
    @Nullable private final MittelGUIItem axisX, axisY, origin;

    @Nullable private final String[] structure;

    @Nullable private final Character viewBind;

    private final Map<Character, MittelGUIItem> bindings;
    private final List<ViewSlot> viewSlots;

    private final Map<String, MittelGUIItem> placed = new HashMap<>();
    private int ox, oy;
    private final BiConsumer<Player, CoordinateChestGUI> onOpen, onClose;

    private CoordinateChestGUI(Builder b) {
        this.rows = b.rows;
        this.inv = Bukkit.createInventory(this, rows * 9, b.title);
        this.items = new MittelGUIItem[rows * 9];
        this.axisX = b.axisX;
        this.axisY = b.axisY;
        this.origin = b.origin;
        this.structure = b.structure == null ? null : b.structure.clone();
        this.viewBind = b.viewBind;
        this.bindings = new HashMap<>(b.bindings);
        this.viewSlots = collectViewSlots(b.viewBind);
        this.onOpen = b.onOpen;
        this.onClose = b.onClose;
        render();
    }

    private List<ViewSlot> collectViewSlots(@Nullable Character viewBind) {
        if (structure == null) return legacyViewSlots();
        if (viewBind == null) {
            throw new IllegalStateException("coordinate chest gui requires a view bind when using a structure");
        }

        List<ViewSlot> slots = new ArrayList<>();
        int minRow = rows;
        int minColumn = 9;
        for (int row = 0; row < structure.length; row++) {
            String line = structure[row];
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) == viewBind) {
                    slots.add(new ViewSlot(row * 9 + column, column, row));
                    minRow = Math.min(minRow, row);
                    minColumn = Math.min(minColumn, column);
                }
            }
        }
        if (slots.isEmpty()) throw new IllegalArgumentException("coordinate chest gui requires at least one view slot");

        List<ViewSlot> normalized = new ArrayList<>(slots.size());
        for (ViewSlot slot : slots) {
            normalized.add(new ViewSlot(slot.inventorySlot(), slot.x() - minColumn, slot.y() - minRow));
        }
        return normalized;
    }

    private List<ViewSlot> legacyViewSlots() {
        List<ViewSlot> slots = new ArrayList<>((rows - 1) * 8);
        for (int i = 0; i < (rows - 1) * 8; i++) {
            slots.add(new ViewSlot((i / 8 + 1) * 9 + (i % 8 + 1), i % 8, i / 8));
        }
        return slots;
    }

    private void render() {
        Arrays.fill(items, null);
        inv.clear();

        if (structure == null) {
            if (origin != null) set(0, origin);
            for (int c = 1; c < 9; c++) if (axisX != null) set(c, axisX);
            for (int r = 1; r < rows; r++) if (axisY != null) set(r * 9, axisY);
        } else {
            for (int row = 0; row < structure.length; row++) {
                String line = structure[row];
                for (int column = 0; column < line.length(); column++) {
                    char bind = line.charAt(column);
                    if (bind == ' ') continue;
                    if (viewBind != null && bind == viewBind) continue;
                    MittelGUIItem item = bindings.get(bind);
                    if (item != null) set(row * 9 + column, item);
                }
            }
        }

        for (ViewSlot slot : viewSlots) {
            MittelGUIItem item = placed.get(key(ox + slot.x(), oy + slot.y()));
            if (item != null) set(slot.inventorySlot(), item);
        }
    }

    private void set(int slot, MittelGUIItem item) {
        items[slot] = item;
        inv.setItem(slot, item.getItem());
    }

    public void putItem(int x, int y, @NotNull MittelGUIItem item) {
        placed.put(key(x, y), item);
        render();
    }

    public void removeItem(int x, int y) {
        placed.remove(key(x, y));
        render();
    }

    public void openCentered(@NotNull Player p, int x, int y) {
        int maxX = viewSlots.stream().mapToInt(ViewSlot::x).max().orElse(0);
        int maxY = viewSlots.stream().mapToInt(ViewSlot::y).max().orElse(0);
        ox = x - maxX / 2;
        oy = y - maxY / 2;
        render();
        p.closeInventory();
        p.openInventory(inv);
    }

    public void moveUp() {
        oy--;
        render();
    }

    public void moveDown() {
        oy++;
        render();
    }

    public void moveLeft() {
        ox--;
        render();
    }

    public void moveRight() {
        ox++;
        render();
    }

    public int offsetX() {
        return ox;
    }

    public int offsetY() {
        return oy;
    }

    private static String key(int x, int y) {
        return x + "," + y;
    }

    @Override
    public void open(@NotNull Player p) {
        p.closeInventory();
        p.openInventory(inv);
    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        return inv.getViewers();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    @Override
    public void handleClick(int slot, @NotNull InventoryClickEvent e) {
        if (slot >= 0 && slot < items.length) {
            MittelGUIItem item = items[slot];
            if (item != null) e.setCancelled(!item.onClick(this, e));
        }
    }

    @Override
    public void handleOpen(@NotNull InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player p && onOpen != null) onOpen.accept(p, this);
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player p && onClose != null) onClose.accept(p, this);
    }

    private record ViewSlot(int inventorySlot, int x, int y) {}

    public static final class Builder implements CoordinateBuilder {
        private Component title = Component.empty();
        private int rows;
        private MittelGUIItem axisX, axisY, origin;
        private String[] structure;
        private Character viewBind;
        private final Map<Character, MittelGUIItem> bindings = new HashMap<>();
        private BiConsumer<Player, CoordinateChestGUI> onOpen, onClose;

        @Override
        public @NotNull CoordinateBuilder title(@NotNull Component t) {
            this.title = t;
            return this;
        }

        @Override
        public CoordinateBuilder rows(int r) {
            this.rows = r;
            return this;
        }

        @Override
        public CoordinateBuilder structure(@NotNull String... structure) {
            this.structure = structure.clone();
            return this;
        }

        @Override
        public CoordinateBuilder bind(char bind, @NotNull MittelGUIItem item) {
            this.bindings.put(bind, item);
            return this;
        }

        @Override
        public CoordinateBuilder view(char bind) {
            this.viewBind = bind;
            return this;
        }

        @Override
        public CoordinateBuilder moveUp(char bind, @NotNull MittelGUIItem item) {
            return bind(bind, navigationItem(item, CoordinateChestGUI::moveUp));
        }

        @Override
        public CoordinateBuilder moveDown(char bind, @NotNull MittelGUIItem item) {
            return bind(bind, navigationItem(item, CoordinateChestGUI::moveDown));
        }

        @Override
        public CoordinateBuilder moveLeft(char bind, @NotNull MittelGUIItem item) {
            return bind(bind, navigationItem(item, CoordinateChestGUI::moveLeft));
        }

        @Override
        public CoordinateBuilder moveRight(char bind, @NotNull MittelGUIItem item) {
            return bind(bind, navigationItem(item, CoordinateChestGUI::moveRight));
        }

        private static MittelGUIItem navigationItem(MittelGUIItem item, NavigationAction action) {
            return new MittelGUIItem() {
                @Override
                public org.bukkit.inventory.ItemStack getItem() {
                    return item.getItem();
                }

                @Override
                public boolean onClick(MittelGUI gui, InventoryClickEvent event) {
                    if (gui instanceof CoordinateChestGUI coordinate) action.accept(coordinate);
                    return false;
                }
            };
        }

        @Override
        public CoordinateBuilder axisX(@Nullable MittelGUIItem i) {
            this.axisX = i;
            return this;
        }

        @Override
        public CoordinateBuilder axisY(@Nullable MittelGUIItem i) {
            this.axisY = i;
            return this;
        }

        @Override
        public CoordinateBuilder origin(@Nullable MittelGUIItem i) {
            this.origin = i;
            return this;
        }

        @Override
        public CoordinateBuilder onOpen(@NotNull BiConsumer<Player, CoordinateChestGUI> c) {
            this.onOpen = c;
            return this;
        }

        @Override
        public CoordinateBuilder onClose(@NotNull BiConsumer<Player, CoordinateChestGUI> c) {
            this.onClose = c;
            return this;
        }

        @Override
        public @NotNull CoordinateChestGUI build() {
            if (structure != null) {
                if (structure.length < 1 || structure.length > 6) {
                    throw new IllegalStateException("structure must contain between 1 and 6 rows");
                }
                for (String line : structure) {
                    if (line.length() > 9)
                        throw new IllegalStateException("structure rows must contain at most 9 columns");
                }
                if (rows == 0) rows = structure.length;
                if (rows != structure.length) throw new IllegalStateException("rows must match structure length");
            }
            if (rows < 2) throw new IllegalStateException("rows must be at least 2");
            return new CoordinateChestGUI(this);
        }
    }

    @FunctionalInterface
    private interface NavigationAction {
        void accept(CoordinateChestGUI gui);
    }
}
