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
package io.github.lijinhong11.mittellib.hook.content;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ItemsAdderContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "ItemsAdder";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        CustomStack stack = CustomStack.getInstance(id);
        if (stack == null) {
            return null;
        }

        return stack.getItemStack();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null) {
            return null;
        }

        return customStack.getId();
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        CustomBlock block = CustomBlock.getInstance(id);
        if (block == null) {
            return null;
        }

        return new PackedItemsAdderBlock(block);
    }

    @Override
    public void destroyBlock(Location loc) {
        CustomBlock.remove(loc);
    }

    @Override
    public List<String> getItemSuggestions() {
        return CustomStack.getNamespacedIdsInRegistry().stream()
                .map(s -> "itemsadder:" + s)
                .toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return CustomBlock.getNamespacedIdsInRegistry().stream()
                .map(s -> "itemsadder:" + s)
                .toList();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(Location loc) {
        CustomBlock block = CustomBlock.byAlreadyPlaced(loc.getBlock());
        if (block == null) {
            return null;
        }

        return new PackedItemsAdderBlock(block);
    }

    private record PackedItemsAdderBlock(CustomBlock block) implements PackedBlock {
        @Override
        public void place(@NotNull Location location) {
            block.place(location);
        }

        @Override
        public String getId() {
            return block.getId();
        }

        @Override
        public @Nullable ItemStack toItem() {
            return block.getItemStack();
        }
    }
}
