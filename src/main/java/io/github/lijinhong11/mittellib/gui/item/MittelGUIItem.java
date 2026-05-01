package io.github.lijinhong11.mittellib.gui.item;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.lijinhong11.mittellib.gui.MittelGUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface MittelGUIItem {
    ItemStack getItem();

    /**
     * Handle the click event.
     *
     * @param gui the gui
     * @param event the click event
     * @return true if allows the click, otherwise deny
     */
    @CanIgnoreReturnValue
    default boolean onClick(MittelGUI gui, InventoryClickEvent event) {
        return true;
    }
}
