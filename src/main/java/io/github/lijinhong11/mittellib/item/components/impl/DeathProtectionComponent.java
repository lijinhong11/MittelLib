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
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@ItemComponentSpec(key = "deathProtection")
@AllArgsConstructor
public class DeathProtectionComponent extends ReadWriteItemComponent {
    private final List<ConsumeEffect> effects;

    public static DeathProtectionComponent fromMinecraftComponent(DeathProtection deathProtection) {
        return new DeathProtectionComponent(deathProtection.deathEffects());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.DEATH_PROTECTION;
    }

    public static DeathProtectionComponent readFromSection(ConfigurationSection cs) {
        List<ConsumeEffect> effectList = ComponentCommons.readConsumeEffects(cs);
        return new DeathProtectionComponent(effectList);
    }

    @Override
    public void applyToItem(ItemStack item) {
        item.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection(effects));
    }

    @Override
    public void write(ConfigurationSection cs) {
        if (effects != null && !effects.isEmpty()) {
            ComponentCommons.writeConsumeEffects(effects, cs);
        }
    }
}
