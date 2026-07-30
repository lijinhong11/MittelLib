package io.github.lijinhong11.mittellib.gui.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MittelDialog {
    default boolean canCloseWithEsc() {
        return true;
    }

    default @NotNull DialogBase.DialogAfterAction getAfterAction() {
        return DialogBase.DialogAfterAction.CLOSE;
    }

    @NotNull Component getTitle(Player player);

    @Nullable Component externalTitle(Player player);

    void show(Player player);
}
