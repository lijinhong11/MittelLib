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

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NexoContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "Nexo";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        Optional<ItemBuilder> optional = NexoItems.optionalItemFromId(id);
        return optional.map(ItemBuilder::build).orElse(null);
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return NexoItems.idFromItem(item);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(id);
        if (mechanic == null) {
            return null;
        }

        return new PackedNexoBlock(mechanic);
    }

    @Override
    public void destroyBlock(Location loc) {
        NexoBlocks.remove(loc);
    }

    @Override
    public List<String> getItemSuggestions() {
        return NexoItems.itemNames().stream().map(i -> "nexo:" + i).toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return Arrays.stream(NexoBlocks.blockIDs()).map(b -> "nexo:" + b).toList();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(Location loc) {
        CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(loc);
        if (mechanic == null) {
            return null;
        }

        return new PackedNexoBlock(mechanic);
    }

    private record PackedNexoBlock(CustomBlockMechanic mechanic) implements PackedBlock {
        @Override
        public void place(@NotNull Location location) {
            NexoBlocks.place(mechanic.getItemID(), location);
        }

        @Override
        public String getId() {
            return mechanic.getItemID();
        }

        @Override
        public @Nullable ItemStack toItem() {
            ItemBuilder item = NexoItems.itemFromId(mechanic.getItemID());
            return item == null ? null : item.build();
        }
    }
}
