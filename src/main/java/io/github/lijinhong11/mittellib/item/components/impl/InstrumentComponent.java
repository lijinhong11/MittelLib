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
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.AllArgsConstructor;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "instrument")
@AllArgsConstructor
public final class InstrumentComponent extends ReadWriteItemComponent {
    private final @NotNull MusicInstrument instrument;

    public static InstrumentComponent fromMinecraftComponent(MusicInstrument instrument) {
        return new InstrumentComponent(instrument);
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.INSTRUMENT;
    }

    @Nullable public static InstrumentComponent readFromSection(ConfigurationSection cs) {
        String keyStr = cs.getString("instrument", "null");
        NamespacedKey key = BukkitUtils.getNamespacedKey(keyStr);
        if (key == null) {
            return null;
        }

        MusicInstrument instrument = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.INSTRUMENT)
                .get(key);
        if (instrument == null) {
            MittelLib.getInstance().getLogger().severe("Failed to find a MusicInstrument with key " + key.asString());
            return null;
        }

        return new InstrumentComponent(instrument);
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.INSTRUMENT, instrument);
    }

    @Override
    public void write(ConfigurationSection cs) {
        NamespacedKey key = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.INSTRUMENT)
                .getKey(instrument);

        cs.set("instrument", key.asString());
    }
}
