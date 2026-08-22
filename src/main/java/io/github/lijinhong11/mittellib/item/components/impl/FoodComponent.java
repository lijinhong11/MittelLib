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

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "food")
@AllArgsConstructor
@NoArgsConstructor
public final class FoodComponent extends ReadWriteItemComponent {
    private @NonNegative int nutrition = 0;

    private float saturation = 0.0f;

    private boolean canAlwaysEat = false;

    public static FoodComponent fromMinecraftComponent(FoodProperties properties) {
        return new FoodComponent(properties.nutrition(), properties.saturation(), properties.canAlwaysEat());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.FOOD;
    }

    @Nullable public static FoodComponent readFromSection(ConfigurationSection cs) {
        if (!cs.contains("nutrition")) {
            MittelLib.getInstance().getLogger().severe("Failed to define food component: 'nutrition' is not set");
            return null;
        }

        if (!cs.contains("saturation")) {
            MittelLib.getInstance().getLogger().severe("Failed to define food component: 'saturation' is not set");
            return null;
        }

        int nutrition = NumberUtils.asUnsigned(cs.getInt("nutrition", -1));

        double saturationDouble = cs.getDouble("saturation", -1.0d);
        if (saturationDouble < 0.0d) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Failed to define food component: 'saturation' must be >= 0 (was " + saturationDouble
                            + ")");
            return null;
        }

        float saturation = (float) saturationDouble;
        boolean canAlwaysEat = cs.getBoolean("canAlwaysEat", false);

        return new FoodComponent(nutrition, saturation, canAlwaysEat);
    }

    @Override
    public void applyToItem(ItemStack item) {
        FoodProperties properties = FoodProperties.food()
                .nutrition(nutrition)
                .saturation(saturation)
                .canAlwaysEat(canAlwaysEat)
                .build();

        item.setData(DataComponentTypes.FOOD, properties);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("nutrition", nutrition);
        cs.set("saturation", saturation);
        cs.set("canAlwaysEat", canAlwaysEat);
    }
}
