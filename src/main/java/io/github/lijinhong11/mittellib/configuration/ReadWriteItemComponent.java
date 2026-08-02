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
package io.github.lijinhong11.mittellib.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public abstract class ReadWriteItemComponent implements ReadWriteObject {
    protected ReadWriteItemComponent() {
        super();
    }

    protected ReadWriteItemComponent(ConfigurationSection cs) {
        throw new UnsupportedOperationException("Please use #readFromSection method");
    }

    public abstract void applyToItem(ItemStack item);

    public final void read(ConfigurationSection cs) {
        // Should move to static readFromSection method
        throw new RuntimeException(new IllegalAccessException("Please use #readFromSection method"));
    }
}
