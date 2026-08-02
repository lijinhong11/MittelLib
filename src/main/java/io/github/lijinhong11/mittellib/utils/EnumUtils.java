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
package io.github.lijinhong11.mittellib.utils;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class EnumUtils {
    public static @NotNull TriState readTriState(@NotNull String name) {
        return readEnum(TriState.class, name, TriState.NOT_SET);
    }

    public static @Nullable <T extends Enum<T>> T readEnum(@NotNull Class<T> clazz, @NotNull String name) {
        return readEnum(clazz, name, null);
    }

    @Contract("_, _, !null -> !null")
    public static @Nullable <T extends Enum<T>> T readEnum(
            @NotNull Class<T> clazz, @NotNull String name, @Nullable T def) {
        Preconditions.checkNotNull(name);

        for (T t : clazz.getEnumConstants()) {
            if (t.toString().equalsIgnoreCase(name)) {
                return t;
            }
        }

        return def;
    }
}
