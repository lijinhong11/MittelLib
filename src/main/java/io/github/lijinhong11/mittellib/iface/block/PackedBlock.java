package io.github.lijinhong11.mittellib.iface.block;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface of every content provider's blocks
 */
public interface PackedBlock {
    /**
     * Place the block at a location
     * @param location the location
     */
    void place(@NotNull Location location);

    /**
     * Get the id of the block
     * @return id of the block
     */
    String getId();

    /**
     * Gets the item of the block
     * @return the item of the block
     */
    @Nullable ItemStack toItem();
}
