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
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class MMOItemsContentProvider implements ContentProvider {
    @Override
    public @NotNull String getId() {
        return "MMOItems";
    }

    @Override
    public @Nullable ItemStack getItem(@NonNull String id) {
        if (!id.contains(":")) {
            return null;
        }

        String type = id.substring(0, id.indexOf(':'));
        String itemId = id.substring(type.length());

        Type mmoType = Type.get(type);
        if (mmoType == null) {
            return null;
        }

        MMOItem item = MMOItems.plugin.getMMOItem(mmoType, itemId);
        if (item == null) {
            return null;
        }

        return item.newBuilder().build();
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        Type type = MMOItems.getType(item);
        if (type == null) {
            return null;
        }

        String id = MMOItems.getID(item);
        if (id == null) {
            return null;
        }

        return type.getId() + ":" + id;
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        return null; // its custom blocks are very limited
    }

    @Override
    public void destroyBlock(@NotNull Location loc) {
        // its custom blocks are very limited
    }

    @Override
    public @NotNull List<String> getItemSuggestions() {
        return MMOItems.plugin.getTemplates().collectTemplates().stream()
                .map(t -> "mmoitems:" + t.getType().toString() + ":" + t.getId())
                .toList();
    }

    @Override
    public @NotNull List<String> getBlockSuggestions() {
        return List.of(); // its custom blocks are very limited
    }

    @Override
    public @Nullable PackedBlock getBlockByLocation(@NotNull Location loc) {
        return null; // its custom blocks are very limited
    }
}
