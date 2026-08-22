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
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@ItemComponentSpec(key = "modelData")
@RequiredArgsConstructor
@AllArgsConstructor
public final class CustomModelDataComponent extends ReadWriteItemComponent {
    private List<Float> floats = new ArrayList<>();
    private List<Boolean> flags = new ArrayList<>();
    private List<String> strings = new ArrayList<>();
    private List<Color> colors = new ArrayList<>();

    public static CustomModelDataComponent fromMinecraftComponent(CustomModelData customModelData) {
        return new CustomModelDataComponent(
                customModelData.floats(), customModelData.flags(), customModelData.strings(), customModelData.colors());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.CUSTOM_MODEL_DATA;
    }

    @NotNull public static CustomModelDataComponent readFromSection(ConfigurationSection cs) {
        List<Float> floats = cs.getFloatList("floats");
        List<Boolean> flags = cs.getBooleanList("flags");
        List<String> strings = cs.getStringList("strings");
        List<Color> colors = BukkitUtils.toColors(cs.getMapList("colors"));

        return new CustomModelDataComponent(floats, flags, strings, colors);
    }

    @Override
    public void applyToItem(ItemStack item) {
        CustomModelData cmd = CustomModelData.customModelData()
                .addFloats(floats)
                .addFlags(flags)
                .addStrings(strings)
                .addColors(colors)
                .build();

        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("floats", floats);
        cs.set("flags", flags);
        cs.set("strings", strings);

        if (this.colors != null) {
            cs.set("colors", BukkitUtils.writeColors(colors));
        }
    }

    public void applyToMeta(ItemMeta meta) {
        var cmd = meta.getCustomModelDataComponent();
        cmd.setFloats(floats);
        cmd.setFlags(flags);
        cmd.setStrings(strings);
        cmd.setColors(colors);

        meta.setCustomModelDataComponent(cmd);
    }
}
