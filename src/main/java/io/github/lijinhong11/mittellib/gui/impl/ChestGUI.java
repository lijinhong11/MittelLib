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
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public final class ChestGUI implements MittelGUI {
    private final Inventory inv;
    private final Map<Integer, MittelGUIItem> items = new HashMap<>();

    private BiConsumer<Player, ChestGUI> openConsumer;
    private BiConsumer<Player, ChestGUI> closeConsumer;

    private ChestGUI(Builder builder) {
        this.inv = Bukkit.createInventory(this, builder.size, builder.title);

        init(builder);
    }

    private void init(Builder builder) {
        this.openConsumer = builder.openConsumer;
        this.closeConsumer = builder.closeConsumer;

        final int len = builder.structure.length;

        if (len < 1 || len > 6) {
            throw new IllegalArgumentException("the structure array length should be 1 <= length <= 6");
        }

        if (!builder.bindings.isEmpty()) {
            for (int i = 1; i <= len; i++) {
                String structure = builder.structure[i];
                if (structure.length() > 9) {
                    throw new IllegalArgumentException("the structure element length should be length <= 9");
                }

                if (!structure.isEmpty()) {
                    for (int c = 1; c <= structure.length(); c++) {
                        char ch = structure.charAt(c);

                        if (ch == ' ') {
                            continue;
                        }

                        MittelGUIItem item = builder.bindings.get(ch);
                        if (item == null) {
                            continue;
                        }

                        final int finalSlot = i * c - 1;

                        this.inv.setItem(finalSlot, item.getItem());
                        this.items.put(finalSlot, item);
                    }
                }
            }
        }
    }

    @Override
    public void open(@NonNull Player player) {
        player.closeInventory();
        player.openInventory(inv);
    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        return this.inv.getViewers();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    public void putItem(@Range(from = 0, to = 53) int slot, @NotNull MittelGUIItem item) {
        if (this.items.containsKey(slot)) {
            return;
        }

        this.items.put(slot, item);
        this.inv.setItem(slot, item.getItem());
    }

    public void removeItem(@Range(from = 0, to = 53) int slot) {
        this.items.remove(slot);
        this.inv.setItem(slot, null);
    }

    @Override
    public void handleClick(int slot, @NotNull InventoryClickEvent e) {
        MittelGUIItem item = items.get(slot);
        if (item != null) {
            e.setCancelled(!item.onClick(this, e));
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

    public static class Builder implements ChestBuilder {
        private Component title;
        private int size;

        private String[] structure;
        private final Map<Character, MittelGUIItem> bindings = new HashMap<>();

        private BiConsumer<Player, ChestGUI> openConsumer;
        private BiConsumer<Player, ChestGUI> closeConsumer;

        @Override
        public ChestBuilder title(@NonNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        public ChestBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure) {
            this.structure = structure;
            return this;
        }

        @Override
        public ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure) {
            return structure(structure.toArray(String[]::new));
        }

        @Override
        public ChestBuilder bind(char bind, @NotNull MittelGUIItem item) {
            this.bindings.put(bind, item);
            return this;
        }

        @Override
        public ChestBuilder onOpen(@NotNull BiConsumer<Player, ChestGUI> openConsumer) {
            this.openConsumer = openConsumer;
            return this;
        }

        @Override
        public ChestBuilder onClose(@NotNull BiConsumer<Player, ChestGUI> closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        @Override
        public ChestGUI build() {
            return new ChestGUI(this);
        }
    }
}
