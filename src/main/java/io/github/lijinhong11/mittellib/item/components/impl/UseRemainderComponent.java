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
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@ItemComponentSpec(key = "useRemainder")
@AllArgsConstructor
public final class UseRemainderComponent extends ReadWriteItemComponent {
    private final @NotNull ItemStack transfromInto;

    public static UseRemainderComponent fromMinecraftComponent(UseRemainder useRemainder) {
        return new UseRemainderComponent(useRemainder.transformInto());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.USE_REMAINDER;
    }

    public static UseRemainderComponent readFromSection(ConfigurationSection cs) {
        ConfigurationSection itemSection = cs.getConfigurationSection("transformInto");
        if (itemSection == null) {
            return null;
        }

        MittelItem mittelItem = MittelItem.readFromSection(itemSection);
        return new UseRemainderComponent(mittelItem.get());
    }

    @Override
    public void applyToItem(ItemStack item) {
        UseRemainder useRemainder = UseRemainder.useRemainder(transfromInto);
        item.setData(DataComponentTypes.USE_REMAINDER, useRemainder);
    }

    @Override
    public void write(ConfigurationSection cs) {
        MittelItem mittelItem = new MittelItem(transfromInto);
        ConfigurationSection transformInto = cs.createSection("transformInto");
        mittelItem.write(transformInto);
    }
}
