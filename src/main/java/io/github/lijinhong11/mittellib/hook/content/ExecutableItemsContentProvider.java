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

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import com.ssomar.score.sobject.SObjectInterface;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecutableItemsContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "ExecutableItems";
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        ExecutableItemsManagerInterface manager = ExecutableItemsAPI.getExecutableItemsManager();
        Optional<ExecutableItemInterface> itemOptional = manager.getExecutableItem(id);
        return itemOptional.map(item -> item.buildItem(1, Optional.empty())).orElse(null);
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        ExecutableItemsManagerInterface manager = ExecutableItemsAPI.getExecutableItemsManager();
        Optional<ExecutableItemInterface> itemOptional = manager.getExecutableItem(item);

        return itemOptional.map(SObjectInterface::getId).orElse(null);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        return null;
    }

    @Override
    public void destroyBlock(Location loc) {}

    @Override
    public List<String> getItemSuggestions() {
        return ExecutableItemsAPI.getExecutableItemsManager().getExecutableItemIdsList().stream()
                .map(s -> "executableitems:" + s)
                .toList();
    }

    @Override
    public List<String> getBlockSuggestions() {
        return List.of();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(Location loc) {
        return null;
    }
}
