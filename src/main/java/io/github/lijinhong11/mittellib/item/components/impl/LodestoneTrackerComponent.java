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
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@ItemComponentSpec(key = "lodestoneTracker")
@AllArgsConstructor
@NoArgsConstructor
public class LodestoneTrackerComponent extends ReadWriteItemComponent {
    private @Nullable Location location;
    private boolean tracked = true;

    public static LodestoneTrackerComponent fromMinecraftComponent(LodestoneTracker tracker) {
        return new LodestoneTrackerComponent(tracker.location(), tracker.tracked());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.LODESTONE_TRACKER;
    }

    @Override
    public void applyToItem(ItemStack item) {
        LodestoneTracker lodestoneTracker = LodestoneTracker.lodestoneTracker(location, tracked);

        item.setData(DataComponentTypes.LODESTONE_TRACKER, lodestoneTracker);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("tracked", tracked);

        if (location != null) {
            BukkitUtils.writeLocationSection(cs.createSection("location"), location);
        }
    }

    public static LodestoneTrackerComponent readFromSection(ConfigurationSection cs) {
        boolean tracked = cs.getBoolean("tracked", true);

        Location location = null;
        ConfigurationSection loc = cs.getConfigurationSection("location");
        if (loc != null) {
            location = BukkitUtils.readLocation(loc);
        }

        return new LodestoneTrackerComponent(location, tracked);
    }
}
