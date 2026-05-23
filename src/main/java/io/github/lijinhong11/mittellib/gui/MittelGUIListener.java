package io.github.lijinhong11.mittellib.gui;

import io.github.lijinhong11.mittellib.gui.impl.AnvilGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;

public final class MittelGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MittelGUI mg)) {
            return;
        }

        mg.handleClick(event.getRawSlot(), event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MittelGUI mg)) {
            return;
        }

        mg.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof MittelGUI mg)) {
            return;
        }

        mg.handleClose(event);
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {
        if (event.getInventory().getHolder() instanceof AnvilGUI ag) {
            ag.handlePrepare(event.getView());
        }
    }
}
