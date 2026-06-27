package io.github.lijinhong11.mittellib.gui.impl;

import io.github.lijinhong11.mittellib.gui.MittelGUI;
import io.github.lijinhong11.mittellib.gui.item.MittelGUIItem;
import java.util.*;
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

public final class PaginatedChestGUI implements MittelGUI {
    private final Inventory inv;
    private final MittelGUIItem[] renderedItems;
    private final Map<Character, MittelGUIItem> bindings;
    private final List<MittelGUIItem> pageItems;
    private final List<Integer> contentSlots = new ArrayList<>();
    private final String[] structure;
    private final char contentBind;
    private final char previousPageBind;
    private final char nextPageBind;
    private final MittelGUIItem previousPageItem;
    private final MittelGUIItem nextPageItem;

    private BiConsumer<Player, PaginatedChestGUI> openConsumer;
    private BiConsumer<Player, PaginatedChestGUI> closeConsumer;

    private int page;

    private final PageButtonItem cachedPrevButton;
    private final PageButtonItem cachedNextButton;

    private PaginatedChestGUI(Builder builder) {
        this.inv = Bukkit.createInventory(this, builder.size, builder.title);
        this.renderedItems = new MittelGUIItem[builder.size];
        this.bindings = new HashMap<>(builder.bindings);
        this.pageItems = new ArrayList<>(builder.pageItems);
        this.structure = builder.structure.clone();
        this.contentBind = builder.contentBind;
        this.previousPageBind = builder.previousPageBind;
        this.nextPageBind = builder.nextPageBind;
        this.previousPageItem = builder.previousPageItem;
        this.nextPageItem = builder.nextPageItem;
        this.cachedPrevButton = new PageButtonItem(this.previousPageItem, PaginatedChestGUI::previousPage);
        this.cachedNextButton = new PageButtonItem(this.nextPageItem, PaginatedChestGUI::nextPage);
        init(builder);
    }

    private void init(Builder builder) {
        this.openConsumer = builder.openConsumer;
        this.closeConsumer = builder.closeConsumer;

        validateStructure(this.structure);
        collectContentSlots();
        if (this.contentSlots.isEmpty()) {
            throw new IllegalArgumentException("paged chest gui requires at least one content slot");
        }

        render();
    }

    private void collectContentSlots() {
        for (int row = 0; row < this.structure.length; row++) {
            String line = this.structure[row];
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) == this.contentBind) {
                    int slot = row * 9 + column;
                    if (slot < this.inv.getSize()) {
                        this.contentSlots.add(slot);
                    }
                }
            }
        }
    }

    private void render() {
        Arrays.fill(this.renderedItems, null);
        this.inv.clear();

        for (int row = 0; row < this.structure.length; row++) {
            String line = this.structure[row];
            for (int column = 0; column < line.length(); column++) {
                char bind = line.charAt(column);
                int slot = row * 9 + column;
                if (slot >= this.inv.getSize() || bind == ' ' || bind == this.contentBind) {
                    continue;
                }

                MittelGUIItem item = getStructureItem(bind);
                if (item == null) {
                    continue;
                }

                setRenderedItem(slot, item);
            }
        }

        int firstItem = this.page * this.contentSlots.size();
        for (int i = 0; i < this.contentSlots.size(); i++) {
            int itemIndex = firstItem + i;
            if (itemIndex >= this.pageItems.size()) {
                break;
            }

            setRenderedItem(this.contentSlots.get(i), this.pageItems.get(itemIndex));
        }
    }

    private MittelGUIItem getStructureItem(char bind) {
        if (bind == this.previousPageBind) {
            return this.cachedPrevButton;
        }

        if (bind == this.nextPageBind) {
            return this.cachedNextButton;
        }

        return this.bindings.get(bind);
    }

    private void setRenderedItem(int slot, MittelGUIItem item) {
        this.renderedItems[slot] = item;
        this.inv.setItem(slot, item.getItem());
    }

    @Override
    public void open(@NonNull Player player) {
        player.closeInventory();
        player.openInventory(this.inv);
    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        return this.inv.getViewers();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    public int currentPage() {
        return this.page;
    }

    public int pageCount() {
        return Math.max(1, (int) Math.ceil((double) this.pageItems.size() / this.contentSlots.size()));
    }

    public @NotNull List<Integer> contentSlots() {
        return List.copyOf(this.contentSlots);
    }

    public @NotNull List<MittelGUIItem> pageItems() {
        return List.copyOf(this.pageItems);
    }

    public void setPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        int maxPage = pageCount() - 1;
        this.page = Math.clamp(page, 0, maxPage);
        render();
    }

    public void nextPage() {
        setPage(this.page + 1);
    }

    public void previousPage() {
        setPage(this.page - 1);
    }

    public void addPageItem(@NotNull MittelGUIItem item) {
        this.pageItems.add(item);
        setPage(this.page);
    }

    public void setPageItems(@NotNull Collection<? extends MittelGUIItem> items) {
        this.pageItems.clear();
        this.pageItems.addAll(items);
        setPage(this.page);
    }

    @Override
    public void handleClick(int slot, @NotNull InventoryClickEvent e) {
        if (slot >= 0 && slot < renderedItems.length) {
            MittelGUIItem item = this.renderedItems[slot];
            if (item != null) {
                e.setCancelled(!item.onClick(this, e));
            }
        }
    }

    @Override
    public void handleOpen(@NotNull InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player p && this.openConsumer != null) {
            this.openConsumer.accept(p, this);
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player p && this.closeConsumer != null) {
            this.closeConsumer.accept(p, this);
        }
    }

    private static void validateStructure(String[] structure) {
        int len = structure.length;
        if (len < 1 || len > 6) {
            throw new IllegalArgumentException("the structure array length should be 1 <= length <= 6");
        }

        for (String line : structure) {
            if (line.length() > 9) {
                throw new IllegalArgumentException("the structure element length should be length <= 9");
            }
        }
    }

    private record PageButtonItem(MittelGUIItem item, PageAction action) implements MittelGUIItem {
        @Override
        public org.bukkit.inventory.ItemStack getItem() {
            return item.getItem();
        }

        @Override
        public boolean onClick(MittelGUI gui, InventoryClickEvent event) {
            if (gui instanceof PaginatedChestGUI paginated) {
                action.accept(paginated);
            }

            return false;
        }
    }

    @FunctionalInterface
    private interface PageAction {
        void accept(PaginatedChestGUI gui);
    }

    public static class Builder implements PagedChestBuilder {
        private Component title = Component.empty();
        private int size;
        private String[] structure;
        private final Map<Character, MittelGUIItem> bindings = new HashMap<>();
        private final List<MittelGUIItem> pageItems = new ArrayList<>();
        private Character contentBind;
        private Character previousPageBind;
        private Character nextPageBind;
        private MittelGUIItem previousPageItem;
        private MittelGUIItem nextPageItem;

        private BiConsumer<Player, PaginatedChestGUI> openConsumer;
        private BiConsumer<Player, PaginatedChestGUI> closeConsumer;

        @Override
        public PagedChestBuilder title(@NonNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        public PagedChestBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public PagedChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure) {
            this.structure = structure;
            return this;
        }

        @Override
        public PagedChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure) {
            return structure(structure.toArray(String[]::new));
        }

        @Override
        public PagedChestBuilder bind(char bind, @NotNull MittelGUIItem item) {
            this.bindings.put(bind, item);
            return this;
        }

        @Override
        public PagedChestBuilder content(char bind) {
            this.contentBind = bind;
            return this;
        }

        @Override
        public PagedChestBuilder previousPage(char bind, @NotNull MittelGUIItem item) {
            this.previousPageBind = bind;
            this.previousPageItem = item;
            return this;
        }

        @Override
        public PagedChestBuilder nextPage(char bind, @NotNull MittelGUIItem item) {
            this.nextPageBind = bind;
            this.nextPageItem = item;
            return this;
        }

        @Override
        public PagedChestBuilder items(@NotNull Collection<? extends MittelGUIItem> items) {
            this.pageItems.clear();
            this.pageItems.addAll(items);
            return this;
        }

        @Override
        public PagedChestBuilder addItem(@NotNull MittelGUIItem item) {
            this.pageItems.add(item);
            return this;
        }

        @Override
        public PagedChestBuilder onOpen(@NotNull BiConsumer<Player, PaginatedChestGUI> openConsumer) {
            this.openConsumer = openConsumer;
            return this;
        }

        @Override
        public PagedChestBuilder onClose(@NotNull BiConsumer<Player, PaginatedChestGUI> closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        @Override
        public PaginatedChestGUI build() {
            if (this.structure == null) {
                throw new IllegalStateException("paged chest gui requires a structure");
            }

            if (this.contentBind == null) {
                throw new IllegalStateException("paged chest gui requires a content bind");
            }

            if (this.size == 0 || this.size % 9 != 0) {
                this.size = this.structure.length * 9;
            }

            return new PaginatedChestGUI(this);
        }
    }
}
