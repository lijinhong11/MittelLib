package io.github.lijinhong11.mittellib.gui;

import io.github.lijinhong11.mittellib.gui.impl.AnvilGUI;
import io.github.lijinhong11.mittellib.gui.impl.ChestGUI;
import io.github.lijinhong11.mittellib.gui.impl.CoordinateChestGUI;
import io.github.lijinhong11.mittellib.gui.impl.PaginatedChestGUI;
import io.github.lijinhong11.mittellib.gui.item.MittelGUIItem;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
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

    @NotNull
    List<HumanEntity> viewers();

    void handleClick(int slot, @NotNull InventoryClickEvent e);

    void handleOpen(@NotNull InventoryOpenEvent e);

    void handleClose(@NotNull InventoryCloseEvent e);

    static ChestBuilder chestBuilder() {
        return new ChestGUI.Builder();
    }

    static PagedChestBuilder pagedChestBuilder() {
        return new PaginatedChestGUI.Builder();
    }

    static CoordinateBuilder coordinateBuilder() {
        return new CoordinateChestGUI.Builder();
    }

    @ApiStatus.Experimental
    static AnvilBuilder anvilBuilder() {
        return new AnvilGUI.Builder();
    }

    interface ChestBuilder {
        ChestBuilder title(@NotNull Component title);

        ChestBuilder size(int size);

        ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure);

        ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure);

        default ChestBuilder layout(@NotNull @ArrayLenRange(from = 1, to = 6) String... layout) {
            return structure(layout);
        }

        default ChestBuilder layout(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> layout) {
            return structure(layout);
        }

        ChestBuilder bind(char bind, @NotNull MittelGUIItem item);

        ChestBuilder onOpen(@NotNull BiConsumer<Player, ChestGUI> openConsumer);

        ChestBuilder onClose(@NotNull BiConsumer<Player, ChestGUI> closeConsumer);

        ChestGUI build();
    }

    interface PagedChestBuilder {
        PagedChestBuilder title(@NotNull Component title);

        PagedChestBuilder size(int size);

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

        PagedChestBuilder items(@NotNull Collection<? extends MittelGUIItem> items);

        PagedChestBuilder addItem(@NotNull MittelGUIItem item);

        PagedChestBuilder onOpen(@NotNull BiConsumer<Player, PaginatedChestGUI> openConsumer);

        PagedChestBuilder onClose(@NotNull BiConsumer<Player, PaginatedChestGUI> closeConsumer);

        PaginatedChestGUI build();
    }

    interface CoordinateBuilder {
        CoordinateBuilder title(@NotNull Component title);

        CoordinateBuilder rows(int rows);

        CoordinateBuilder axisX(@Nullable MittelGUIItem item);

        CoordinateBuilder axisY(@Nullable MittelGUIItem item);

        CoordinateBuilder origin(@Nullable MittelGUIItem item);

        CoordinateBuilder onOpen(@NotNull BiConsumer<Player, CoordinateChestGUI> openConsumer);

        CoordinateBuilder onClose(@NotNull BiConsumer<Player, CoordinateChestGUI> closeConsumer);

        CoordinateChestGUI build();
    }

    interface AnvilBuilder {
        AnvilBuilder title(@NotNull Component title);

        AnvilBuilder firstItem(@NotNull MittelGUIItem item);

        AnvilBuilder secondItem(@NotNull MittelGUIItem item);

        AnvilBuilder resultItem(@NotNull MittelGUIItem item);

        AnvilBuilder prepareListener(@NotNull BiConsumer<Player, AnvilView> consumer);

        AnvilBuilder onOpen(@NotNull BiConsumer<Player, AnvilGUI> openConsumer);

        AnvilBuilder onClose(@NotNull BiConsumer<Player, AnvilGUI> closeConsumer);

        AnvilGUI build();
    }
}
