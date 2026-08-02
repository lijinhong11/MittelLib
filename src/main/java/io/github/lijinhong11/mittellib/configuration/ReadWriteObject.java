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

import com.google.common.base.Preconditions;
import io.github.lijinhong11.mittellib.MittelLib;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReadWriteObject {
    static @Nullable <T extends ReadWriteObject> T read(@NotNull Class<T> clazz, @NotNull ConfigurationSection cs) {
        Preconditions.checkNotNull(clazz);

        try {
            return clazz.getConstructor(ConfigurationSection.class).newInstance(cs);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            MittelLib.getInstance().getLogger().log(Level.SEVERE, "Failed to instance " + clazz.getName(), e);
        }

        return null;
    }

    void write(ConfigurationSection cs);

    void read(ConfigurationSection cs);
}
