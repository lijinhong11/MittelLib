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
package io.github.lijinhong11.mittellib.gui.inventory.choosers;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import io.github.lijinhong11.mittellib.gui.inventory.impl.PaginatedChestGUI;
import io.github.lijinhong11.mittellib.gui.inventory.item.ButtonItem;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.bukkit.entity.Player;

public class MaterialChooser {
    private static final BiPredicate<String, MittelGUIItem> LOOKUP;

    static {
        LOOKUP = (s, i) -> i.getItem().getType().toString().contains(s.toUpperCase());
    }

    public static void openUsableBlockChooser(Player p, Consumer<PackedBlock> blockConsumer) {
        PaginatedChestGUI gui = MittelGUI.pagedChestBuilder()
                .structure("BBBBBBBSB", "BMMMMMMMB", "BMMMMMMMB", "BMMMMMMMB", "BMMMMMMMB", "BBPBBBNBB")
                .bind('B', ButtonItem.BACKGROUND)
                .bindSearch(
                        'S',
                        ButtonItem.getSearchButton(
                                MittelLib.getInstance().getLanguageManager().getMsgComponent(p, "common.search")))
                .content('M')
                .onSearch(LOOKUP)
                .previousPage(
                        'P',
                        ButtonItem.getPageButton(MittelLib.getInstance()
                                .getLanguageManager()
                                .getMsgComponent(p, "common.previous-page")))
                .nextPage(
                        'N',
                        ButtonItem.getPageButton(
                                MittelLib.getInstance().getLanguageManager().getMsgComponent(p, "common.next-page")))
                .build();

        List<PackedBlock> usableBlocks = ContentProviders.getAllUsableBlocks();
        usableBlocks = usableBlocks.stream()
                .filter(u -> u.toItem() != null && !u.toItem().getType().isAir())
                .toList();

        gui.setPageItems(usableBlocks.stream()
                .map(b -> ButtonItem.clickable(b.toItem(), (g, e) -> {
                    blockConsumer.accept(b);
                    p.closeInventory();
                    return false;
                }))
                .toList());

        gui.open(p);
    }
}
