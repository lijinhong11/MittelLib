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
import io.github.lijinhong11.mittellib.gui.inventory.PlayerInventoryHolder;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
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
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public final class ChestGUI implements MittelGUI {
    private final Inventory template;
    private final Component title;
    private final ChestGUI owner;
    private final Map<java.util.UUID, ChestGUI> playerViews = new HashMap<>();
    private final MittelGUIItem[] items;

    private BiConsumer<Player, ChestGUI> openConsumer;
    private BiConsumer<Player, ChestGUI> closeConsumer;

    private ChestGUI(Builder builder) {
        this.owner = this;
        this.title = builder.title;
        this.template = Bukkit.createInventory(this, builder.size, builder.title);
        this.items = new MittelGUIItem[builder.size];

        init(builder);
    }

    private ChestGUI(ChestGUI source, Player player) {
        this.owner = source;
        this.title = source.title;
        PlayerInventoryHolder holder = new PlayerInventoryHolder(this, player.getUniqueId());
        this.template = Bukkit.createInventory(holder, source.template.getSize(), source.title);
        holder.inventory(this.template);
        this.items = source.items.clone();
        this.openConsumer = source.openConsumer;
        this.closeConsumer = source.closeConsumer;
        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot] != null) template.setItem(slot, items[slot].getItem());
        }
    }

    private void init(Builder builder) {
        this.openConsumer = builder.openConsumer;
        this.closeConsumer = builder.closeConsumer;

        final int len = builder.structure.length;

        if (len < 1 || len > 6) {
            throw new IllegalArgumentException("the structure array length should be 1 <= length <= 6");
        }

        for (int i = 0; i < len; i++) {
            String structure = builder.structure[i];
            if (structure.length() > 9) {
                throw new IllegalArgumentException("the structure element length should be length <= 9");
            }

            if (structure.isEmpty()) {
                continue;
            }

            for (int c = 0; c < structure.length(); c++) {
                char ch = structure.charAt(c);

                if (ch == ' ') {
                    continue;
                }

                MittelGUIItem item = builder.bindings.get(ch);
                if (item == null) {
                    continue;
                }

                final int finalSlot = i * 9 + c;
                if (finalSlot >= this.template.getSize()) {
                    continue;
                }

                this.template.setItem(finalSlot, item.getItem());
                this.items[finalSlot] = item;
            }
        }
    }

    @Override
    public void open(@NotNull Player player) {
        player.closeInventory();
        if (owner != this) {
            owner.playerViews.put(player.getUniqueId(), this);
            player.openInventory(template);
            return;
        }
        ChestGUI view = new ChestGUI(this, player);
        playerViews.put(player.getUniqueId(), view);
        view.open(player);
    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        if (owner != this) return template.getViewers();
        return playerViews.values().stream()
                .flatMap(view -> view.template.getViewers().stream())
                .toList();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.template;
    }

    public void putItem(@Range(from = 0, to = 53) int slot, @NotNull MittelGUIItem item) {
        this.items[slot] = item;
        this.template.setItem(slot, item.getItem());
        playerViews.values().forEach(view -> {
            view.items[slot] = item;
            view.template.setItem(slot, item.getItem());
        });
    }

    public void removeItem(@Range(from = 0, to = 53) int slot) {
        this.items[slot] = null;
        this.template.setItem(slot, null);
        playerViews.values().forEach(view -> {
            view.items[slot] = null;
            view.template.setItem(slot, null);
        });
    }

    @Override
    public void handleClick(int slot, @NotNull InventoryClickEvent e) {
        if (slot >= 0 && slot < items.length) {
            MittelGUIItem item = items[slot];
            if (item != null) {
                e.setCancelled(!item.onClick(this, e));
            }
        }
    }

    @Override
    public void handleOpen(@NotNull InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player p && openConsumer != null) {
            openConsumer.accept(p, this);
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent e) {
        if (owner != this && e.getPlayer() instanceof Player p) owner.playerViews.remove(p.getUniqueId(), this);
        if (e.getPlayer() instanceof Player p && closeConsumer != null) {
            closeConsumer.accept(p, this);
        }
    }

    public static class Builder implements ChestBuilder {
        private Component title = Component.empty();
        private int size;

        private String[] structure;
        private final Map<Character, MittelGUIItem> bindings = new HashMap<>();

        private BiConsumer<Player, ChestGUI> openConsumer;
        private BiConsumer<Player, ChestGUI> closeConsumer;

        @Override
        public @NotNull ChestBuilder title(@NotNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        public @NonNull ChestBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public @NonNull ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure) {
            this.structure = structure;
            return this;
        }

        @Override
        public @NonNull ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure) {
            return structure(structure.toArray(String[]::new));
        }

        @Override
        public @NonNull ChestBuilder bind(char bind, @NotNull MittelGUIItem item) {
            this.bindings.put(bind, item);
            return this;
        }

        @Override
        public @NonNull ChestBuilder onOpen(@NotNull BiConsumer<Player, ChestGUI> openConsumer) {
            this.openConsumer = openConsumer;
            return this;
        }

        @Override
        public @NonNull ChestBuilder onClose(@NotNull BiConsumer<Player, ChestGUI> closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        @Override
        public @NotNull ChestGUI build() {
            if (this.size == 0 || this.size % 9 != 0) {
                this.size = this.structure.length * 9;
            }

            return new ChestGUI(this);
        }
    }
}
