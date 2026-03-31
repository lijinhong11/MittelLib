package io.github.lijinhong11.mittellib.gui.handlers;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface InventoryClickHandler {
    void onClick(InventoryClickEvent event);
}
