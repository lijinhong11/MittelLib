package me.mmmjjkx.mittellib.hook;

import me.mmmjjkx.mittellib.item.block.PackedBlock;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ContentProvider {
    @Nullable ItemStack getItem(@NotNull String id);

    @Nullable PackedBlock getBlock(@NotNull String id);

    /**
     * Destroy a block
     * @param loc the location of the block
     */
    void destroyBlock(Location loc);

    /**
     * Get all item suggestions (including blocks) for tab complete
     * @return a list of item suggestion
     */
    List<String> getItemSuggestions();

    /**
     * Get all block suggestions for tab complete
     * @return a list of block suggestion
     */
    List<String> getBlockSuggestions();
}
