package io.github.lijinhong11.mittellib.gui.handlers;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface InventoryOpenHandler {
    void onOpen(Player player);
}
