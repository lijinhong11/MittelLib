package io.github.lijinhong11.mittellib.gui;

import io.github.lijinhong11.mittellib.gui.impl.ChestGUI;
import io.github.lijinhong11.mittellib.gui.item.MittelGUIItem;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.jetbrains.annotations.NotNull;

public interface MittelGUI extends InventoryHolder {
    void open(@NotNull Player player);

    @NotNull
    List<HumanEntity> viewers();

    static ChestBuilder chestBuilder() {
        return new ChestGUI.Builder();
    }

    interface ChestBuilder {
        ChestBuilder title(@NotNull Component title);

        ChestBuilder size(int size);

        ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) String... structure);

        ChestBuilder structure(@NotNull @ArrayLenRange(from = 1, to = 6) List<String> structure);

        ChestBuilder bind(char bind, @NotNull MittelGUIItem item);

        ChestBuilder onOpen(@NotNull BiConsumer<Player, ChestGUI> openConsumer);

        ChestBuilder onClose(@NotNull BiConsumer<Player, ChestGUI> closeConsumer);

        ChestGUI build();
    }

    interface AnvilBuilder {
        AnvilBuilder title(@NotNull Component title);

        AnvilBuilder firstItem(@NotNull MittelGUIItem item);

        AnvilBuilder secondItem(@NotNull MittelGUIItem item);

        AnvilBuilder resultItem(@NotNull MittelGUIItem item);

        AnvilBuilder textListener(@NotNull Consumer<String> consumer);

        AnvilBuilder onOpen(@NotNull BiConsumer<Player, MittelGUI> openConsumer);

        AnvilBuilder onClose(@NotNull BiConsumer<Player, MittelGUI> closeConsumer);

        ChestGUI build();
    }
}
