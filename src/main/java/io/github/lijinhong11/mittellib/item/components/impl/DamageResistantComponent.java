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
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "damageResistant", requiredVersion = MCVersion.V26_1_X)
public final class DamageResistantComponent extends ReadWriteItemComponent {
    private final RegistryKeySet<DamageType> types;

    public DamageResistantComponent(RegistryKeySet<DamageType> types) {
        this.types = types;
    }

    public static DamageResistantComponent fromMinecraftComponent(DamageResistant damageResistant) {
        return new DamageResistantComponent(damageResistant.types());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DAMAGE_RESISTANT;
    }

    public static @Nullable DamageResistantComponent readFromSection(ConfigurationSection cs) {
        List<NamespacedKey> keys = BukkitUtils.getNamespacedKeys(cs.getStringList("damageResistants"));
        if (keys.isEmpty()) {
            return null;
        }

        List<TypedKey<DamageType>> typedKeys = keys.stream()
                .map(key -> TypedKey.create(RegistryKey.DAMAGE_TYPE, key.asString()))
                .toList();
        return new DamageResistantComponent(RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, typedKeys));
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(types));
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set(
                "damageResistants",
                types.values().stream().map(key -> key.key().asString()).toList());
    }
}
