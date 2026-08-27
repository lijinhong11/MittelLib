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

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.gui.dialog.impl.input.TextInputDialog;
import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import io.github.lijinhong11.mittellib.gui.inventory.PlayerInventoryHolder;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public final class PaginatedChestGUI implements MittelGUI {
    private final Inventory inv;
    private final Map<UUID, PaginatedChestGUI> playerViews = new HashMap<>();
    private final Component title;
    private final PaginatedChestGUI owner;
    private final MittelGUIItem[] renderedItems;
    private final Map<Character, MittelGUIItem> bindings;
    private final List<MittelGUIItem> pageItems;
    private final List<MittelGUIItem> visiblePageItems = new ArrayList<>();
    private final IntList contentSlots = new IntArrayList();
    private final String[] structure;
    private final char contentBind;
    private final char previousPageBind;
    private final char nextPageBind;
    private final Character searchBind;
    private final BiPredicate<String, MittelGUIItem> searchCallback;

    private BiConsumer<Player, PaginatedChestGUI> openConsumer;
    private BiConsumer<Player, PaginatedChestGUI> closeConsumer;

    private int page;
    private String searchQuery = "";
    private int pageBeforeSearch;
    private boolean hasPageBeforeSearch;

    private final PageButtonItem cachedPrevButton;
    private final PageButtonItem cachedNextButton;
    private final SearchButtonItem cachedSearchButton;

    private PaginatedChestGUI(Builder builder) {
        this.owner = this;
        this.title = builder.title;
        this.inv = Bukkit.createInventory(this, builder.size, builder.title);
        this.renderedItems = new MittelGUIItem[builder.size];
        this.bindings = new HashMap<>(builder.bindings);
        this.pageItems = new ArrayList<>(builder.pageItems);
        this.structure = builder.structure.clone();
        this.contentBind = builder.contentBind;
        this.previousPageBind = builder.previousPageBind;
        this.nextPageBind = builder.nextPageBind;
        this.searchBind = builder.searchBind;
        this.searchCallback = builder.searchCallback;
        this.cachedPrevButton = new PageButtonItem(builder.previousPageItem, PaginatedChestGUI::previousPage);
        this.cachedNextButton = new PageButtonItem(builder.nextPageItem, PaginatedChestGUI::nextPage);
        this.cachedSearchButton = builder.searchItem == null ? null : new SearchButtonItem(builder.searchItem);
        init(builder);
    }

    private PaginatedChestGUI(PaginatedChestGUI source, Player player) {
        this.owner = source;
        PlayerInventoryHolder holder = new PlayerInventoryHolder(this, player.getUniqueId());
        this.title = source.title;
        this.inv = Bukkit.createInventory(holder, source.inv.getSize(), source.title);
        holder.inventory(this.inv);
        this.renderedItems = new MittelGUIItem[source.renderedItems.length];
        this.bindings = new HashMap<>(source.bindings);
        this.pageItems = new ArrayList<>(source.pageItems);
        this.structure = source.structure.clone();
        this.contentBind = source.contentBind;
        this.previousPageBind = source.previousPageBind;
        this.nextPageBind = source.nextPageBind;
        this.searchBind = source.searchBind;
        this.searchCallback = source.searchCallback;
        this.openConsumer = source.openConsumer;
        this.closeConsumer = source.closeConsumer;
        this.cachedPrevButton = new PageButtonItem(source.cachedPrevButton.item(), PaginatedChestGUI::previousPage);
        this.cachedNextButton = new PageButtonItem(source.cachedNextButton.item(), PaginatedChestGUI::nextPage);
        this.cachedSearchButton =
                source.cachedSearchButton == null ? null : new SearchButtonItem(source.cachedSearchButton.item());
        collectContentSlots();
        updateVisibleItems();
        render();
    }

    private void init(Builder builder) {
        this.openConsumer = builder.openConsumer;
        this.closeConsumer = builder.closeConsumer;

        validateStructure(this.structure);
        collectContentSlots();
        if (this.contentSlots.isEmpty()) {
            throw new IllegalArgumentException("paged chest gui requires at least one content slot");
        }

        updateVisibleItems();
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
            if (itemIndex >= this.visiblePageItems.size()) {
                break;
            }

            setRenderedItem(this.contentSlots.getInt(i), this.visiblePageItems.get(itemIndex));
        }
    }

    private MittelGUIItem getStructureItem(char bind) {
        if (bind == this.previousPageBind) {
            return this.cachedPrevButton;
        }

        if (bind == this.nextPageBind) {
            return this.cachedNextButton;
        }

        if (this.searchBind != null && bind == this.searchBind) {
            return this.cachedSearchButton;
        }

        return this.bindings.get(bind);
    }

    private void setRenderedItem(int slot, MittelGUIItem item) {
        this.renderedItems[slot] = item;
        this.inv.setItem(slot, item.getItem());
    }

    @Override
    public void open(@NotNull Player player) {
        player.closeInventory();
        if (owner != this) {
            owner.playerViews.put(player.getUniqueId(), this);
            player.openInventory(inv);
            return;
        }

        PaginatedChestGUI view = new PaginatedChestGUI(this, player);
        this.playerViews.put(player.getUniqueId(), view);
        view.open(player);
    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        if (owner != this) return inv.getViewers();
        return this.playerViews.values().stream()
                .flatMap(view -> view.inv.getViewers().stream())
                .toList();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    public int currentPage(@NotNull Player player) {
        return viewFor(player).page;
    }

    public int pageCount(@NotNull Player player) {
        return viewFor(player).pageCount();
    }

    private int pageCount() {
        return Math.max(1, (int) Math.ceil((double) this.visiblePageItems.size() / this.contentSlots.size()));
    }

    public @NotNull List<Integer> contentSlots() {
        return List.copyOf(this.contentSlots);
    }

    public @NotNull List<MittelGUIItem> pageItems() {
        return List.copyOf(this.pageItems);
    }

    public @NotNull List<MittelGUIItem> visiblePageItems(@NotNull Player player) {
        return List.copyOf(viewFor(player).visiblePageItems);
    }

    public @NotNull String searchQuery(@NotNull Player player) {
        return viewFor(player).searchQuery;
    }

    public boolean searchEnabled() {
        return this.searchBind != null;
    }

    public void setPage(@NotNull Player player, @Range(from = 0, to = Integer.MAX_VALUE) int page) {
        viewFor(player).setPageInternal(page);
    }

    private void setPageInternal(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        int maxPage = pageCount() - 1;
        this.page = Math.clamp(page, 0, maxPage);
        render();
    }

    public void nextPage(@NotNull Player player) {
        PaginatedChestGUI view = viewFor(player);
        view.setPageInternal(view.page + 1);
    }

    public void previousPage(@NotNull Player player) {
        PaginatedChestGUI view = viewFor(player);
        view.setPageInternal(view.page - 1);
    }

    public void addPageItem(@NotNull MittelGUIItem item) {
        this.pageItems.add(item);
        updateVisibleItems();
        setPageInternal(this.page);
        if (owner == this) {
            playerViews.values().forEach(view -> {
                view.pageItems.add(item);
                view.updateVisibleItems();
                view.setPageInternal(view.page);
            });
        }
    }

    public void setPageItems(@NotNull Collection<? extends MittelGUIItem> items) {
        this.pageItems.clear();
        this.pageItems.addAll(items);
        updateVisibleItems();
        setPageInternal(this.page);
        if (owner == this) {
            playerViews.values().forEach(view -> {
                view.pageItems.clear();
                view.pageItems.addAll(items);
                view.updateVisibleItems();
                view.setPageInternal(view.page);
            });
        }
    }

    public void search(@NotNull Player player, @NotNull String query) {
        viewFor(player).searchInternal(query);
    }

    private void searchInternal(@NotNull String query) {
        if (!searchEnabled()) {
            return;
        }

        String newQuery = query.trim();
        if (this.searchQuery.isEmpty() && !newQuery.isEmpty()) {
            this.pageBeforeSearch = this.page;
            this.hasPageBeforeSearch = true;
        }

        if (!newQuery.isEmpty()) {
            this.searchQuery = newQuery;
            updateVisibleItems();
            setPageInternal(0);
            return;
        }

        this.searchQuery = "";
        updateVisibleItems();
        setPageInternal(this.hasPageBeforeSearch ? this.pageBeforeSearch : this.page);
        this.hasPageBeforeSearch = false;
    }

    public void clearSearch(@NotNull Player player) {
        search(player, "");
    }

    private void updateVisibleItems() {
        this.visiblePageItems.clear();
        this.visiblePageItems.addAll(filterItems(this.pageItems, this.searchQuery, this.searchCallback));
    }

    static List<MittelGUIItem> filterItems(
            List<MittelGUIItem> items, String query, BiPredicate<String, MittelGUIItem> searchCallback) {
        if (query.isEmpty()) {
            return List.copyOf(items);
        }

        return items.stream().filter(item -> searchCallback.test(query, item)).toList();
    }

    private void beginSearch(Player player) {
        PaginatedChestGUI view = viewFor(player);
        int page = view.page;
        player.closeInventory();
        TextInputDialog.create(
                        MittelLib.getInstance()
                                .getLanguageManager()
                                .getMsgComponent(player, "common.search-dialog-title"),
                        MittelLib.getInstance()
                                .getLanguageManager()
                                .getMsgComponent(player, "common.search-dialog-label"),
                        query -> {
                            view.searchInternal(query);
                            view.open(player);
                        },
                        () -> {
                            view.open(player);
                            view.setPageInternal(page);
                        })
                .show(player);
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
        if (owner != this && e.getPlayer() instanceof Player p) owner.playerViews.remove(p.getUniqueId(), this);
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
            if (gui instanceof PaginatedChestGUI paginated && event.getWhoClicked() instanceof Player player) {
                action.accept(paginated, player);
            }

            return false;
        }
    }

    private record SearchButtonItem(MittelGUIItem item) implements MittelGUIItem {
        @Override
        public ItemStack getItem() {
            return item.getItem();
        }

        @Override
        public boolean onClick(MittelGUI gui, InventoryClickEvent event) {
            if (gui instanceof PaginatedChestGUI paginated && event.getWhoClicked() instanceof Player player) {
                paginated.beginSearch(player);
            }
            return false;
        }
    }

    private PaginatedChestGUI viewFor(@NotNull Player player) {
        PaginatedChestGUI view = owner.playerViews.get(player.getUniqueId());
        if (view == null) {
            throw new IllegalStateException("Player does not have this paginated GUI open");
        }
        return view;
    }

    @FunctionalInterface
    private interface PageAction {
        void accept(PaginatedChestGUI gui, Player player);
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
        private Character searchBind;
        private MittelGUIItem searchItem;
        private BiPredicate<String, MittelGUIItem> searchCallback;

        private BiConsumer<Player, PaginatedChestGUI> openConsumer;
        private BiConsumer<Player, PaginatedChestGUI> closeConsumer;

        @Override
        public @NotNull PagedChestBuilder title(@NotNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        public @NonNull PagedChestBuilder size(int size) {
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
        public PagedChestBuilder bindSearch(char bind, @NotNull MittelGUIItem item) {
            this.searchBind = bind;
            this.searchItem = item;
            return this;
        }

        @Override
        public PagedChestBuilder onSearch(@NotNull BiPredicate<String, MittelGUIItem> searchCallback) {
            this.searchCallback = searchCallback;
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
        public @NotNull PaginatedChestGUI build() {
            if (this.structure == null) {
                throw new IllegalStateException("paged chest gui requires a structure");
            }

            if (this.contentBind == null) {
                throw new IllegalStateException("paged chest gui requires a content bind");
            }

            if (this.searchBind != null && this.searchCallback == null) {
                throw new IllegalStateException("paged chest gui search requires an onSearch callback");
            }

            if (this.size == 0 || this.size % 9 != 0) {
                this.size = this.structure.length * 9;
            }

            return new PaginatedChestGUI(this);
        }
    }
}
