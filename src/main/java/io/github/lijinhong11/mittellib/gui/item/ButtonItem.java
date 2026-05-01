package io.github.lijinhong11.mittellib.gui.item;

import io.github.lijinhong11.mittellib.gui.MittelGUI;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@AllArgsConstructor
public class ButtonItem implements MittelGUIItem {
    private final ItemStack item;

    private BiFunction<MittelGUI, InventoryClickEvent, Boolean> onClick;

    public static ButtonItem unclickable(ItemStack item) {
        return new ButtonItem(item, (g, i) -> false);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public boolean onClick(MittelGUI gui, InventoryClickEvent event) {
        if (onClick != null) {
            return onClick.apply(gui, event);
        }

        return true;
    }
}
