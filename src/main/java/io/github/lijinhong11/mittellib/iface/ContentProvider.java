package io.github.lijinhong11.mittellib.iface;

import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ContentProvider {
    /**
     * Get the content provider's id
     */
    @NotNull String getId();

    /**
     * Get an item by its id
     *
     * @param id the item's id
     * @return the item, null if not found
     */
    @Nullable ItemStack getItem(@NotNull String id);

    /**
     * Get item's id
     *
     * @param item the item
     * @return the item's id in content provider, null if not found
     */
    @Nullable String getIdFromItem(@NotNull ItemStack item);

    /**
     * Get a block by its id
     *
     * @param id the block's id
     * @return the block, null if not found
     */
    @Nullable PackedBlock getBlock(@NotNull String id);

    /**
     * Destroy a block
     *
     * @param loc the location of the block
     */
    void destroyBlock(Location loc);

    /**
     * Get all item suggestions (including blocks) for tab complete
     *
     * @return a list of item suggestion
     */
    List<String> getItemSuggestions();

    /**
     * Get all block suggestions for tab complete
     *
     * @return a list of block suggestion
     */
    List<String> getBlockSuggestions();
}
