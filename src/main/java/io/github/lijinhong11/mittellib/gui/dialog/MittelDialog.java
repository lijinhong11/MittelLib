package io.github.lijinhong11.mittellib.gui.dialog;

import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.List;
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

    @NotNull Component getTitle();

    @Nullable Component externalTitle();

    @NotNull DialogType getDialogType();

    @NotNull List<? extends DialogBody> getBody();

    @NotNull List<? extends DialogInput> getInputs();

    void show(Player player);
}
