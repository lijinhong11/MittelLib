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
import java.util.function.BiPredicate;
import org.bukkit.entity.Player;

class ChooserCommons {
    static PaginatedChestGUI buildGUI(Player p, BiPredicate<String, MittelGUIItem> lookup) {
        return MittelGUI.pagedChestBuilder()
                .structure("BBBBBBBSB", "BMMMMMMMB", "BMMMMMMMB", "BMMMMMMMB", "BMMMMMMMB", "BBPBBBNBB")
                .bind('B', ButtonItem.BACKGROUND)
                .bindSearch(
                        'S',
                        ButtonItem.getSearchButton(
                                MittelLib.getInstance().getLanguageManager().getMsgComponent(p, "common.search")))
                .content('M')
                .onSearch(lookup)
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
    }
}
