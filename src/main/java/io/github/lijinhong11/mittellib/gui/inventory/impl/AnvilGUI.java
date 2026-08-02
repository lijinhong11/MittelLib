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
import java.util.List;
import java.util.function.BiConsumer;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public final class AnvilGUI implements MittelGUI {
    private final Inventory inv;

    @Setter
    private MittelGUIItem firstItem;

    @Setter
    private MittelGUIItem secondItem;

    @Setter
    private MittelGUIItem resultItem;

    private BiConsumer<Player, AnvilView> prepareListener;

    private BiConsumer<Player, AnvilGUI> openConsumer;
    private BiConsumer<Player, AnvilGUI> closeConsumer;

    private AnvilGUI(Builder builder) {
        this.inv = Bukkit.createInventory(this, InventoryType.ANVIL, builder.title);

        init(builder);
    }

    private void init(Builder builder) {
        this.firstItem = builder.first;
        this.secondItem = builder.second;
        this.resultItem = builder.result;

        this.prepareListener = builder.prepareListener;

        this.openConsumer = builder.openConsumer;
        this.closeConsumer = builder.closeConsumer;

        if (this.firstItem != null) {
            this.inv.setItem(0, this.firstItem.getItem());
        }

        if (this.secondItem != null) {
            this.inv.setItem(1, this.secondItem.getItem());
        }

        if (this.resultItem != null) {
            this.inv.setItem(2, this.resultItem.getItem());
        }
    }

    @Override
    public void open(@NotNull Player player) {
        player.closeInventory();
        player.openInventory(inv);
    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        return inv.getViewers();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    public void handleClick(@Range(from = 0, to = 2) int slot, @NotNull InventoryClickEvent e) {
        MittelGUIItem item;
        switch (slot) {
            case 0 -> item = firstItem;
            case 1 -> item = secondItem;
            case 2 -> item = resultItem;
            default -> {
                return;
            }
        }

        if (item != null) {
            e.setCancelled(!item.onClick(this, e));
        }
    }

    public void handlePrepare(AnvilView view) {
        if (prepareListener != null) {
            prepareListener.accept((Player) view.getPlayer(), view);
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
        if (e.getPlayer() instanceof Player p && closeConsumer != null) {
            closeConsumer.accept(p, this);
        }
    }

    public static class Builder implements AnvilBuilder {
        private Component title;

        private MittelGUIItem first;
        private MittelGUIItem second;
        private MittelGUIItem result;

        private BiConsumer<Player, AnvilView> prepareListener;

        private BiConsumer<Player, AnvilGUI> openConsumer;
        private BiConsumer<Player, AnvilGUI> closeConsumer;

        @Override
        public AnvilBuilder title(@NonNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        public AnvilBuilder firstItem(@NotNull MittelGUIItem item) {
            this.first = item;
            return this;
        }

        @Override
        public AnvilBuilder secondItem(@NotNull MittelGUIItem item) {
            this.second = item;
            return this;
        }

        @Override
        public AnvilBuilder resultItem(@NotNull MittelGUIItem item) {
            this.result = item;
            return this;
        }

        @Override
        public AnvilBuilder prepareListener(@NotNull BiConsumer<Player, AnvilView> prepareListener) {
            this.prepareListener = prepareListener;
            return this;
        }

        @Override
        public AnvilBuilder onOpen(@NotNull BiConsumer<Player, AnvilGUI> openConsumer) {
            this.openConsumer = openConsumer;
            return this;
        }

        @Override
        public AnvilBuilder onClose(@NotNull BiConsumer<Player, AnvilGUI> closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        @Override
        public AnvilGUI build() {
            return new AnvilGUI(this);
        }
    }
}
