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
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseEffects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Range;

@ItemComponentSpec(key = "useEffects")
@NoArgsConstructor
@AllArgsConstructor
public class UseEffectsComponent extends ReadWriteItemComponent {
    private boolean canSprint = false;
    private boolean interactVibrations = true;
    private @Range(from = 0, to = 1) @NonNegative float speedMultiplier = 0.2f;

    public static UseEffectsComponent fromMinecraftComponent(UseEffects useEffects) {
        return new UseEffectsComponent(
                useEffects.canSprint(), useEffects.interactVibrations(), useEffects.speedMultiplier());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.USE_EFFECTS;
    }

    @Override
    public void applyToItem(ItemStack item) {
        UseEffects useEffects = UseEffects.useEffects()
                .canSprint(canSprint)
                .interactVibrations(interactVibrations)
                .speedMultiplier(speedMultiplier)
                .build();

        item.setData(DataComponentTypes.USE_EFFECTS, useEffects);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("canSprint", canSprint);
        cs.set("interactVibrations", interactVibrations);
        cs.set("speedMultiplier", speedMultiplier);
    }

    public static UseEffectsComponent readFromSection(ConfigurationSection cs) {
        boolean canSprint = cs.getBoolean("canSprint", false);
        boolean interactVibrations = cs.getBoolean("interactVibrations", true);
        float speedMultiplier = NumberUtils.asUnsigned((float) cs.getDouble("speedMultiplier", 0.2));

        return new UseEffectsComponent(canSprint, interactVibrations, speedMultiplier);
    }
}
