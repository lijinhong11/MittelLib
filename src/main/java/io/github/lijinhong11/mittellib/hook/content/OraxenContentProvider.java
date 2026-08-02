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

import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import io.github.lijinhong11.mittellib.utils.NullUtils;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class OraxenContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "Oraxen";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        Optional<ItemBuilder> optional = OraxenItems.getOptionalItemById(id);
        return optional.map(ItemBuilder::build).orElse(null);
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return OraxenItems.getIdByItem(item);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        Mechanic block = NullUtils.findAnyNonNull(
                OraxenBlocks.getChorusMechanic(id),
                OraxenBlocks.getNoteBlockMechanic(id),
                OraxenBlocks.getStringMechanic(id));
        if (block == null) {
            return null;
        }

        Optional<ItemBuilder> itemBuilderOptional = OraxenItems.getOptionalItemById(block.getItemID());
        if (itemBuilderOptional.isEmpty()) {
            return null;
        }

        return new PackedOraxenBlock(block);
    }

    @Override
    public void destroyBlock(Location loc) {
        OraxenBlocks.remove(loc, null);
    }

    @Override
    public List<String> getItemSuggestions() {
        return Arrays.stream(OraxenItems.getItemNames()).map(i -> "oraxen:" + i).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return OraxenBlocks.getBlockIDs().stream().map(b -> "oraxen:" + b).toList();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(Location loc) {
        Mechanic mechanic = OraxenBlocks.getOraxenBlock(loc);
        if (mechanic == null) {
            return null;
        }

        return new PackedOraxenBlock(mechanic);
    }

    private record PackedOraxenBlock(Mechanic mechanic) implements PackedBlock {
        @Override
        public void place(@NotNull Location location) {
            OraxenBlocks.place(mechanic.getItemID(), location);
        }

        @Override
        public String getId() {
            return mechanic.getItemID();
        }

        @Override
        public @Nullable ItemStack toItem() {
            ItemBuilder item = OraxenItems.getItemById(mechanic.getItemID());
            return item == null ? null : item.build();
        }
    }
}
