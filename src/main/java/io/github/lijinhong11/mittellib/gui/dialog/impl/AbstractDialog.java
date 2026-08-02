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
