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
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.components.ComponentUtils;
import java.util.List;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CraftEngineContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "CraftEngine";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        NamespacedKey key = BukkitUtils.getNamespacedKey(id);
        if (key == null) {
            return null;
        }

        BukkitItemDefinition item = CraftEngineItems.byId(Key.of(key.namespace(), key.value()));
        if (item == null) {
            return null;
        }

        return item.buildBukkitItem();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        BukkitItemDefinition customItem = CraftEngineItems.byItemStack(item);

        if (customItem == null) {
            return null;
        }

        return customItem.id().asString();
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        NamespacedKey key = BukkitUtils.getNamespacedKey(id);
        if (key == null) {
            return null;
        }

        BlockDefinition ci = CraftEngineBlocks.byId(Key.of(key.namespace(), key.value()));
        if (ci == null) {
            return null;
        }

        return new PackedCraftEngineBlock(ci);
    }

    @Override
    public void destroyBlock(@NotNull Location loc) {
        CraftEngineBlocks.remove(loc.getBlock());
    }

    @Override
    public @NotNull List<String> getItemSuggestions() {
        return CraftEngineItems.loadedItems().keySet().stream()
                .map(k -> "craftengine:" + k.asString())
                .toList();
    }

    @Override
    public @NotNull List<String> getBlockSuggestions() {
        return CraftEngineBlocks.loadedBlocks().entrySet().stream()
                .filter(b -> new PackedCraftEngineBlock(b.getValue()).toItem() != null)
                .map(k -> "craftengine:" + k.getKey().asString())
                .toList();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(@NotNull Location loc) {
        Block block = loc.getBlock();
        if (!CraftEngineBlocks.isCustomBlock(block)) {
            return null;
        }

        ImmutableBlockState ibs = CraftEngineBlocks.getCustomBlockState(block);
        if (ibs == null) {
            return null;
        }

        return new PackedCraftEngineBlock(ibs.behavior().block());
    }

    private record PackedCraftEngineBlock(BlockDefinition block) implements PackedBlock {
        @Override
        public void place(@NotNull Location location) {
            CraftEngineBlocks.place(location, block.id(), true);
        }

        @Override
        public @NotNull String getId() {
            return block.id().asString();
        }

        @Override
        public @Nullable ItemStack toItem() {
            BukkitItemDefinition bind = CraftEngineItems.byId(block.id());
            if (bind == null) {
                ItemStack def = ItemStack.of(Material.PAPER, 1);
                def.editMeta(m -> m.displayName(ComponentUtils.text(block.id().asString())));
                return def;
            }

            return bind.buildBukkitItem();
        }
    }
}
