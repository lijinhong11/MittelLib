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
package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.MittelItem;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SulfurCubeContent;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@ItemComponentSpec(key = "sulfurCubeContent", requiredVersion = MCVersion.V26_2_X)
@AllArgsConstructor
public final class SulfurCubeContentComponent extends ReadWriteItemComponent {
    private final @NotNull ItemStack absorbedItem;

    public static SulfurCubeContentComponent fromMinecraftComponent(SulfurCubeContent sulfurCubeContent) {
        return new SulfurCubeContentComponent(sulfurCubeContent.absorbedItem());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.SULFUR_CUBE_CONTENT;
    }

    public static SulfurCubeContentComponent readFromSection(ConfigurationSection cs) {
        return new SulfurCubeContentComponent(MittelItem.readFromSection(cs).get());
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.SULFUR_CUBE_CONTENT, SulfurCubeContent.sulfurCubeContent(absorbedItem));
    }

    @Override
    public void write(@NotNull ConfigurationSection cs) {
        new MittelItem(absorbedItem).write(cs);
    }
}
