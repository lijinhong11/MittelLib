package io.github.lijinhong11.mittellib.gui.impl;

import io.github.lijinhong11.mittellib.gui.MittelGUI;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnvilGUI implements MittelGUI {
    @Override
    public void open(@NotNull Player player) {

    }

    @Override
    public @NotNull List<HumanEntity> viewers() {
        return List.of();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
