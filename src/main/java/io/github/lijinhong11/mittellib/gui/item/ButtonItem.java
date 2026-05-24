package io.github.lijinhong11.mittellib.gui.item;

import io.github.lijinhong11.mittellib.gui.MittelGUI;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonItem implements MittelGUIItem {
    private final ItemStack item;

    private final BiFunction<MittelGUI, InventoryClickEvent, Boolean> onClick;

    public static ButtonItem clickable(ItemStack item, BiFunction<MittelGUI, InventoryClickEvent, Boolean> onClick) {
        return new ButtonItem(item, onClick);
    }

    public static ButtonItem openGUI(ItemStack item, MittelGUI gui) {
        return new ButtonItem(item, (p, i) -> {
            gui.open((Player) i.getWhoClicked());
            return false;
        });
    }

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
