package io.github.lijinhong11.mittellib.gui.dialog.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputDialog extends AbstractDialog {
    @Override
    public @NotNull Component getTitle(Player player) {
        return null;
    }

    @Override
    public @Nullable Component externalTitle(Player player) {
        return null;
    }

    @Override
    public void show(Player player) {}
}
