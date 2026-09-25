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
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicMobsContentProvider implements ContentProvider {
    private final MythicBukkit api = MythicBukkit.inst();

    @Override
    public @NotNull String getId() {
        return "mythicmobs";
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        ItemExecutor itemExecutor = api.getItemManager();
        Optional<MythicItem> optional = itemExecutor.getItem(id);
        return optional.map(mythicItem -> BukkitAdapter.adapt(mythicItem.generateItemStack(1)))
                .orElse(null);
    }

    @Override
    public @Nullable String getIdFromItem(@NotNull ItemStack item) {
        return api.getItemManager().getMythicTypeFromItem(item);
    }

    @Override
    public @Nullable PackedBlock getBlock(@NotNull String id) {
        return null;
    }

    @Override
    public void destroyBlock(@NotNull Location loc) {}

    @Override
    public @NotNull List<String> getItemSuggestions() {
        return api.getItemManager().getItems().stream()
                .map(MythicItem::getInternalName)
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
