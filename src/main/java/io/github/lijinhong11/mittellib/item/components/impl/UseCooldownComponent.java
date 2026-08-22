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
import io.github.lijinhong11.mittellib.utils.BukkitUtils;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "useCooldown")
@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor
@AllArgsConstructor
public final class UseCooldownComponent extends ReadWriteItemComponent {
    private final @Positive float cooldown;
    private @Nullable Key cooldownGroup;

    public static UseCooldownComponent fromMinecraftComponent(UseCooldown useCooldown) {
        return new UseCooldownComponent(useCooldown.seconds(), useCooldown.cooldownGroup());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.USE_COOLDOWN;
    }

    @Nullable public static UseCooldownComponent readFromSection(ConfigurationSection cs) {
        float sec = NumberUtils.asUnsigned((float) cs.getDouble("seconds"));

        if (sec <= 0) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Failed to define a use cooldown component: seconds is lower than or equals to 0");
            return null;
        }

        NamespacedKey cooldownGroup = null;
        if (cs.contains("cooldownGroup")) {
            NamespacedKey key = BukkitUtils.getNamespacedKey(cs.getString("cooldownGroup", "null"));
            if (key != null) {
                cooldownGroup = key;
            }
        }

        return new UseCooldownComponent(sec, cooldownGroup);
    }

    @Override
    public void applyToItem(ItemStack item) {
        UseCooldown useCooldown =
                UseCooldown.useCooldown(cooldown).cooldownGroup(cooldownGroup).build();

        item.setData(DataComponentTypes.USE_COOLDOWN, useCooldown);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("seconds", cooldown);

        if (cooldownGroup != null) {
            cs.set("cooldownGroup", cooldownGroup.asString());
        }
    }
}
