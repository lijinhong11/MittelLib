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
package io.github.lijinhong11.mittellib.gui.dialog.impl;

import io.github.lijinhong11.mittellib.gui.dialog.MittelDialog;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import org.bukkit.entity.Player;

public abstract class AbstractDialog implements MittelDialog {
    private Dialog dialog = null;

    private void build() {
        DialogBase base = DialogBase.builder(getTitle())
                .canCloseWithEscape(canCloseWithEsc())
                .build();

        dialog = Dialog.create(b -> b.empty().base(base).type(getDialogType()));
    }

    @Override
    public void show(Player player) {
        if (dialog == null) {
            build();
        }

        player.showDialog(dialog);
    }
}
