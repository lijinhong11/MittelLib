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
package io.github.lijinhong11.mittellib.gui.inventory.item;

import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import io.github.lijinhong11.mittellib.item.MittelItem;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonItem implements MittelGUIItem {
    public static final ButtonItem BACKGROUND;

    static {
        MittelItem back = new MittelItem(Material.BLACK_STAINED_GLASS_PANE);
        back.getMeta().setDisplayName(Component.space());
        BACKGROUND = unclickable(back.get());
    }

    private final ItemStack item;

    private final BiFunction<MittelGUI, InventoryClickEvent, Boolean> onClick;

    public static ButtonItem clickable(ItemStack item, BiFunction<MittelGUI, InventoryClickEvent, Boolean> onClick) {
        return new ButtonItem(item, onClick);
    }

    public static ButtonItem openGUI(ItemStack item, MittelGUI gui) {
        return new ButtonItem(item, (_, i) -> {
            gui.open((Player) i.getWhoClicked());
            return false;
        });
    }

    public static ButtonItem unclickable(ItemStack item) {
        return new ButtonItem(item, (_, _) -> false);
    }

    public static ButtonItem getSearchButton(Component name) {
        MittelItem back = new MittelItem(Material.COMPASS);
        back.getMeta().setDisplayName(name);
        return ButtonItem.unclickable(back.get());
    }

    public static ButtonItem getPageButton(Component name) {
        MittelItem prev = new MittelItem(Material.ARROW);
        prev.getMeta().setDisplayName(name);
        return ButtonItem.unclickable(prev.get());
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
