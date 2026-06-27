package io.github.lijinhong11.mittellib.gui.impl;

import io.github.lijinhong11.mittellib.gui.MittelGUI;
import io.github.lijinhong11.mittellib.gui.item.MittelGUIItem;
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
import org.jspecify.annotations.NonNull;

public final class CoordinateChestGUI implements MittelGUI {
    private final Inventory inv;
    private final MittelGUIItem[] items;
    private final int rows;
    @Nullable
    private final MittelGUIItem axisX, axisY, origin;

    private final Map<String, MittelGUIItem> placed = new HashMap<>();
    private int ox, oy;
    private BiConsumer<Player, CoordinateChestGUI> onOpen, onClose;

    private CoordinateChestGUI(Builder b) {
        this.rows = b.rows;
        this.inv = Bukkit.createInventory(this, rows * 9, b.title);
        this.items = new MittelGUIItem[rows * 9];
        this.axisX = b.axisX;
        this.axisY = b.axisY;
        this.origin = b.origin;
        this.onOpen = b.onOpen;
        this.onClose = b.onClose;
        render();
    }

    private void render() {
        java.util.Arrays.fill(items, null);
        inv.clear();

        if (origin != null) set(0, origin);
        for (int c = 1; c < 9; c++) if (axisX != null) set(c, axisX);
        for (int r = 1; r < rows; r++) if (axisY != null) set(r * 9, axisY);

        for (int i = 0; i < (rows - 1) * 8; i++) {
            int slot = (i / 8 + 1) * 9 + (i % 8 + 1);
            MittelGUIItem item = placed.get(key(ox + i % 8, oy + i / 8));
            if (item != null) set(slot, item);
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
        ox = x - 4;
        oy = y - (rows - 1) / 2;
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
    public void open(@NonNull Player p) {
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

    public static final class Builder implements CoordinateBuilder {
        private Component title = Component.empty();
        private int rows;
        private MittelGUIItem axisX, axisY, origin;
        private BiConsumer<Player, CoordinateChestGUI> onOpen, onClose;

        @Override
        public CoordinateBuilder title(@NonNull Component t) {
            this.title = t;
            return this;
        }

        @Override
        public CoordinateBuilder rows(int r) {
            this.rows = r;
            return this;
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
        public CoordinateChestGUI build() {
            if (rows < 2) throw new IllegalStateException("rows must be at least 2");
            return new CoordinateChestGUI(this);
        }
    }
}
