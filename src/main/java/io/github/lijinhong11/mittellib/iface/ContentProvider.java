/*
 * MittelLib
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.mittellib.iface;

import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Get the packed block by its location
     *
     * @return a packed block
     */
    @Nullable PackedBlock getBlockByLocation(Location loc);

    /**
     * Get all blocks
     *
     * @return all blocks in this content provider
     */
    default @NotNull List<PackedBlock> getAllBlocks() {
        return getBlockSuggestions().stream().map(this::getBlock).toList();
    }
}
