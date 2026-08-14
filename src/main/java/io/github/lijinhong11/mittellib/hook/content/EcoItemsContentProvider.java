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

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import io.github.lijinhong11.mittellib.iface.ContentProvider;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EcoItemsContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "EcoItems";
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        EcoItem item = EcoItems.INSTANCE.getByID(id);
        if (item == null) {
            return null;
        }

        return item.getItemStack();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return EcoItems.INSTANCE.values().stream()
                .filter(p -> p.getItemStack().equals(item))
                .findFirst()
                .map(e -> e.getId().asString())
                .orElse(null);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        return null;
    }

    @Override
    public void destroyBlock(@NotNull Location loc) {}

    @Override
    public @NotNull List<String> getItemSuggestions() {
        return EcoItems.INSTANCE.values().stream()
                .map(ei -> "ecoitems" + ei.getId().asString())
                .toList();
    }

    @Override
    public @NotNull List<String> getBlockSuggestions() {
        return List.of();
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(@NotNull Location loc) {
        return null;
    }
}
